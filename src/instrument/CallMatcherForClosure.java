package instrument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CallMatcherForClosure extends ClassAdapter {

    private String[] callsites;
    private MethodNode doCallMethodNode = new MethodNode();
    private List<ShadowMatchInfo> shadowMatchInfos = new ArrayList<ShadowMatchInfo>();
    private int callSiteVar = -1;

    //private HashMap<Integer,CallS> calls;

//  private static $createCallSiteArray() : CallSiteArray
//  NEW CallSiteArray
//  DUP
//  GETSTATIC TestController$_closure1.$ownClass : Class
//  LDC 2
//  ANEWARRAY String
//  DUP
//  LDC 0
//  LDC "render"
//  AASTORE
//  DUP
//  LDC 1
//  LDC "doCall"
//  AASTORE
    private class CallSiteCollectorMV extends MethodAdapter {

        private int state = 0;
        private int index = 0;

        public CallSiteCollectorMV(MethodVisitor mv) {
            super(mv);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            if (state == -1) {
                super.visitLdcInsn(cst);
                return;
            }

            if(state == 0 && cst instanceof Integer) {
                callsites = new String[(Integer)cst];
                state = 1;
            } else if(state == 1 && cst instanceof Integer) {
                index = (Integer)cst;
                state = 2;
            } else if(state == 2 && cst instanceof String) {
                callsites[index] = (String)cst;
                if(index == callsites.length - 1) {
                    state = -1;
                } else {
                    state = 1;
                }
            }
            super.visitLdcInsn(cst);
        }

    }

    private enum State  {START, FOUND_SITE_ARRAY, CALL_SITE,
                         LOAD_SITE_ARRAY_VAR, LOAD_CALL_SITE_INDEX,
                         LOAD_CALL_SITE_OBJECT};
    private State state;

    private class MV extends MethodAdapter implements Opcodes {

        private int curIndex = -1;
        private Stack<Integer> stack = new Stack<Integer>();

        public MV(MethodVisitor mv) {
            super(mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if(state == State.START && name.equals("$getCallSiteArray")) {
                state = State.FOUND_SITE_ARRAY;
            } else if(state == State.LOAD_CALL_SITE_OBJECT
                    && opcode == INVOKEINTERFACE && name.startsWith("call")) {
                int cs = stack.pop();
                // find a way to key the semantic
                shadowMatchInfos.add(new ShadowMatchInfo(
                    callsites[cs],
                    name,
                    desc
                ));

                state = State.FOUND_SITE_ARRAY;
                // TODO bind stack here
            }
            super.visitMethodInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if(state == State.FOUND_SITE_ARRAY && opcode == ASTORE) {
                callSiteVar = var;
                state = State.CALL_SITE;
            } else if(state == State.CALL_SITE && opcode == ALOAD && var == callSiteVar) {
                state = State.LOAD_SITE_ARRAY_VAR;
            }
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            if(state == State.LOAD_SITE_ARRAY_VAR) {
                state = State.LOAD_CALL_SITE_INDEX;
                curIndex = (Integer)cst;
            }
            super.visitLdcInsn(cst);
        }

        @Override
        public void visitInsn(int opcode) {
            if(state == State.LOAD_CALL_SITE_INDEX && opcode == AALOAD) {
                state = State.LOAD_CALL_SITE_OBJECT;
                stack.push(curIndex);
            }
            super.visitInsn(opcode);
        }

    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if(name.equals("$createCallSiteArray")) {
            return new CallSiteCollectorMV(mv);
        } else if(name.equals("doCall") && desc.equals("(Ljava/lang/Object;)Ljava/lang/Object;")) {
            // public doCall(Object) : Object
            doCallMethodNode.accept(mv);
            return new MV(mv);
        } else {
            return mv;
        }
    }

    public CallMatcherForClosure(String closureClassName) {
        super(new EmptyVisitor());
        String name = closureClassName.replace('/', '.');
        try {
            ClassReader cr = new ClassReader(name);
            cr.accept(this, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public boolean matches(String methodPattern) {
//        for(String cs: callsites) {
//            // TODO performing pattern matching
//            if(cs.equals(methodPattern)) {
//                return true;
//            }
//        }
//        return false;
//    }

    public MethodNode getDoCallMethodNode() {
        return this.doCallMethodNode;
    }

    public ShadowMatchInfo[] getShadowMatchInfos() {
        return shadowMatchInfos.toArray(new ShadowMatchInfo[shadowMatchInfos.size()]);
    }

    // find this pattern
    //    ALOAD ${callSiteVar}
    //	  LDC 0
    public int[] queryCallSiteIndice(String methodPattern) {
        List<Integer> result = new ArrayList<Integer>();
        InsnList units = this.doCallMethodNode.instructions;
        AbstractInsnNode s = units.getFirst();
        while(s!=null) {
            if(s.getOpcode() == Opcodes.LDC) { s = s.getNext(); continue; }
            AbstractInsnNode p = s.getPrevious();
            if(p.getOpcode() == Opcodes.ALOAD) { s = s.getNext(); continue; }
            if(((VarInsnNode)p).var != callSiteVar) { s = s.getNext(); continue; }

            int csIndex = (Integer)((LdcInsnNode)s).cst;

            // TODO use regular expression
            if(callsites[csIndex] == methodPattern) {
                result.add(csIndex);
            }
            s = s.getNext();
        }
        int[] temp = new int[result.size()];
        int j = 0;
        for(Integer i: result) {temp[j++] = i;}
        return temp;
    }

}
