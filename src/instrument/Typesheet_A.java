package instrument;

import instrument.analysis.PartialInterpreter;
import instrument.analysis.PartialInterpreter.Pair;

import java.util.HashMap;
import java.util.Map.Entry;

import org.aspectj.weaver.tools.TypePatternMatcher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.sun.org.apache.bcel.internal.generic.AALOAD;

public class Typesheet_A extends Matcher implements Opcodes {

    private String matchedClosureClassName=null;
    private HashMap bindings = new HashMap();
    private MethodNode doCallMethodNode;
    private ShadowMatchInfo[] shadowMatchInfo;
    private int callsiteArrayVar;

    private int curMaxLocals = 0;

    private HashMap<Integer, Pair> analysisResults = new HashMap<Integer, Pair>();
    private HashMap<String, Integer> symtab;

    public void match() {
        if(clazz("*Controller")) {
            if(closure("index") && call("render", new String[]{"s"})) {
                typeAdvice("s", String.class);
            }
        }
    }

    private boolean clazz(String classPat) {
        TypePatternMatcher tpm = parser.parseTypePattern(classPat);
        return tpm.matches(target);
    }

    private boolean closure(String closureName) {
        ClosureScanner cs = new ClosureScanner(target, closureName);
        boolean result = cs.isMatch();
        if(result) {
            matchedClosureClassName = cs.getClosureClassName();
        }
        return result;
    }

    // match call inside the above closure
    // with arg methodPattern
    private boolean call(String methodPattern, String[] bindings) {
        CallMatcherForClosure cmfc = new CallMatcherForClosure(matchedClosureClassName);
        // input is a name
        // return callsite index
        int[] matchedCallSites = cmfc.queryCallSiteIndice(methodPattern);
        boolean result = matchedCallSites.length > 0;
        if(result) {
            doCallMethodNode = cmfc.getDoCallMethodNode();
            curMaxLocals = doCallMethodNode.maxLocals;
            shadowMatchInfo = cmfc.getShadowMatchInfos();
            analyseArgumentForCalls(matchedCallSites);
            symtab = buildSymbolTable(bindings);
        }
        return result;
    }

    private void typeAdvice(String symbol, Class<?> clazz) {

        //
        // IMPORTANT !!! index uses 1-based, not zero-based
        //
        int index = symtab.get(symbol);

        InsnList units = doCallMethodNode.instructions;

        for(Entry<Integer, Pair> e: analysisResults.entrySet()) {
            // 0. resolve conversion, later
            MethodInsnNode m = (MethodInsnNode)e.getValue().key;
            if(m.name.equals("callCurrent")) {
                Type[] argTypes = Type.getArgumentTypes(m.desc);
                int varsInStack = argTypes.length + 1; // include 0, which is the callsite
                int lastLocalVar = curMaxLocals;

                //
                // locals needs only varsInStack - 1
                // because we discard the callsite object itself
                //
                curMaxLocals += varsInStack-1;

                index = index + 1;
                // index = index + 2; // this is the index for e.values[]

                //
                // if varsInStack = 3, then
                // 2, 1, 0
                for(int i=varsInStack-1; i>=0; i--) {
                    units.insertBefore(m, new InsnNode(POP));
                    //
                    // TODO put conversion here before storing it
                    //
                    if (i != 0) {
                        units.insertBefore(m, new VarInsnNode(ASTORE, lastLocalVar + i));
                    }
                }

                // LOAD all arguments

                // some rule need to check here?
                MetaMethod mm = resolveMethodFromMetaClass();
                Method realMethod = resolveSpecialiser(mm);

                // algo:
                // POP
                // ASTORE ${lastLocalVar}
                // POP
                // ASTORE ${lastLocalVar + 1}
                // POP
                // ASTORE ${lastLocalVar + 2}

                // [0] is the callsite
                // [1] is the "this"
                // [2..] is param0
                // (Lgroovy/lang/GroovyObject;Ljava/lang/Object;)Ljava/lang/Object;

            } else {
                throw new RuntimeException("not implemented yet");
            }
            // 1. locate e.getValue().key
            // 2. build new instruction
            // 3. replace
        }
    }

    private HashMap<String, Integer> buildSymbolTable(String[] bindings) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        // IMPORTANT !!! use 1-based, not zero-based
        for (int i = 0; i < bindings.length; i++) {
            result.put(bindings[i], i+1);
        }
        return result;
    }

//  public doCall(Object) : Object
//   L0
//    INVOKESTATIC TestController$_closure1.$getCallSiteArray() : CallSite[]
//    ASTORE 2
//   L1
//    LINENUMBER 4 L1
//    ALOAD 2
//    LDC 0
//    AALOAD <--- find an instruction that USES the object DEFed by this one
//    ALOAD 0: this
//    LDC "Hello World"
//    INVOKEINTERFACE CallSite.callCurrent(GroovyObject,Object) : Object
//   L2
//    ARETURN
//   L3
    private void analyseArgumentForCalls(int[] matchedCallSites) {
        InsnList units = doCallMethodNode.instructions;
        AbstractInsnNode s0 = units.getFirst();
        s0 = getCallSiteArrayVar(s0);
        for(int csi: matchedCallSites) {
            // get callsiteArray variable,
            // then return the next instruction to process
            // - @return null, if not found

            // go finding ALOAD ${callsiteArrayVar}
            // LDC ${csi}
            // AALOAD
            s0 = locateCallSite_AALOAD(csi, s0);

            // s0 will be AALOAD
            PartialInterpreter si = new PartialInterpreter(doCallMethodNode, s0);
            Pair result = si.analyse();
            // result.key = INVOKESTATIC
            // result.values[0] = s0
            // result.values[1] = this (if callCurrent)
            // result.values[2] = "hello world" (just this case)
            analysisResults.put(csi, result);
        }
    }

    private AbstractInsnNode locateCallSite_AALOAD(int csi, AbstractInsnNode s) {
        while(s != null) {
            if(s.getOpcode() != LDC) { s = s.getNext(); continue; }
            LdcInsnNode l = (LdcInsnNode)s;
            if(l.cst instanceof Integer == false) { s = s.getNext(); continue; }

            // check if it's the callsite we are finding: ${csi}
            if((Integer)l.cst != csi) { s = s.getNext(); continue; }

            // if so, check if both s0, and s1 are callsite loading pattern.
            AbstractInsnNode s0 = s.getPrevious();
            if(s0.getOpcode() != ALOAD) { s = s.getNext(); continue; }
            VarInsnNode v = (VarInsnNode)s0;
            if(v.var != callsiteArrayVar) { s = s.getNext(); continue; }
            AbstractInsnNode s1 = s.getNext();
            if(s1.getOpcode() != AALOAD) { s = s.getNext(); continue; }

            return s1; // we return the location of AALOAD to start execution
        }
        return null;
    }

//    private AbstractInsnNode findAALOAD(AbstractInsnNode s0) {
//        AbstractInsnNode s = s0.getNext();
//        AbstractInsnNode s1 = s.getNext();
//        if(s1.getOpcode() == Opcodes.AALOAD) return s1;
//        return null;
//    }
//
//    private AbstractInsnNode locateCallSiteLoad(AbstractInsnNode s) {
//        while(s!=null) {
//            if(s.getOpcode() != Opcodes.ALOAD) { s = s.getNext(); continue; }
//            VarInsnNode v = (VarInsnNode)s;
//            if(v.var == this.callsiteArrayVar) return v;
//        }
//        return null;
//    }

    private AbstractInsnNode getCallSiteArrayVar(AbstractInsnNode s) {
        while(s!=null) {
            while(s.getOpcode() != INVOKESTATIC) {
                s = s.getNext();
                if(s==null) throw new RuntimeException("Error INVOKESTATIC not found");
            }
            MethodInsnNode m = (MethodInsnNode)s;
            if(m.name.equals("$getCallSiteArray")==false) { s = s.getNext(); continue; }

            AbstractInsnNode s1 = m.getNext();
            if(s1.getOpcode() != ASTORE) { s = s.getNext(); continue; }
            VarInsnNode v = (VarInsnNode)s1;
            this.callsiteArrayVar = v.var;
            return v;
        }
        return null;
    }

}
