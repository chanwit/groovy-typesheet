package instrument;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import sand.DebugUtils;

public class Analysis implements Opcodes {

    private Data data;
    private MethodNode method;

    public Analysis(MethodNode m, Data d) {
        this.method = m;
        this.data = d;
    }

    public void fail(String msg) {
        throw new RuntimeException(msg);
    }

    // TODO 1. back-loop analysis
    // TODO 2. label re-allocate
    public ArrayList<AbstractInsnNode> process() {
        InsnList stmts = method.instructions;
        ArrayList<AbstractInsnNode> record = new ArrayList<AbstractInsnNode>();

        AbstractInsnNode a = stmts.getFirst();
        int[] checkPoints = data.getCheckpoints();
        int index = 0;
        int currentCheckPoint = checkPoints[index];
        boolean done = false;

        while(a != null) {
            if(a instanceof JumpInsnNode == false) {
                String name = is_call_to_marker(a);
                if(name==null) { // normal case
                    record.add(a);
                } else if (name.equals("args")) { // also clear ALOAD 3
                    record.remove(a.getPrevious().getPrevious());
                    record.remove(a.getPrevious());
                } else {
                    // not add
                }
                a = a.getNext();
                continue;
            }

            JumpInsnNode j = (JumpInsnNode)a;

            // If jump instruction is conditional
            if(j.getOpcode() != GOTO) {

                // Get next two instructions to check for marker
                AbstractInsnNode s1 = j.getNext();
                AbstractInsnNode s2 = s1.getNext();
                if(s1.getOpcode() != SIPUSH && s2.getOpcode() != INVOKESTATIC) {
                    fail("no checkpoint");
                }

                // No jump, so POP is placed
                insert_pop(j.getOpcode(), record);

                //
                // found pair instructions
                // operand of S1 holds checkpoint number
                //
                int cp = ((IntInsnNode)s1).operand;

                // If current check point is this true branch,
                if(currentCheckPoint == cp) {
                    currentCheckPoint = checkPoints[++index];
                    record.add(a);
                    a = s2.getNext();
                } else {
                // No, it's false branch, then do jumping
                    a = j.label;
                    if(a.getNext() instanceof LineNumberNode) {
                        a = a.getNext();
                    }

                    // Find marker instruction pairs
                    s1 = a.getNext();
                    s2 = s1.getNext();
                    if(s1.getOpcode() != SIPUSH && s2.getOpcode() != INVOKESTATIC) {
                        fail("no checkpoint");
                    }

                    // check current checkpoint
                    cp = ((IntInsnNode)s1).operand;
                    if(currentCheckPoint == cp) {
                        if(index >= checkPoints.length-1) {
                            done = true;
                        } else {
                            currentCheckPoint = checkPoints[++index];
                        }
                        if(a instanceof LabelNode) {
                            // Skip all labels
                            System.out.println(" if A is LABEL");
                        } else {
                            record.add(a);
                        }
                        a = s2.getNext();
                    } else {
                        fail("check point number not matched expected: "+ currentCheckPoint + ", found: " + cp);
                    }
                }
            } else { // GOTO
                // record.add(a);
                a = j.label.getNext();
            }
        }
        return record;
    }

    private void insert_pop(int opcode, ArrayList<AbstractInsnNode> record) {
        if(opcode >= IF_ICMPEQ && opcode <= IF_ACMPNE) {
            record.add(new InsnNode(POP2));
        } else
        if(opcode >= IFEQ && opcode <= IFLE) {
            record.add(new InsnNode(POP));
        }
    }

    private String is_call_to_marker(AbstractInsnNode a) {
        if(a.getOpcode() == INVOKESTATIC) {
            MethodInsnNode m = (MethodInsnNode)a;
            if( m.owner.equals("instrument/Marker") ) {
                return m.name;
            }
        }
        return null;
    }


    public static void process(String className, String dataFile, String outDir) throws Throwable {
        Data d = readData(dataFile);
        if(d.getClassName().replace('/', '.').equals(className) == false) {
            return;
        }

        ClassReader cr = new ClassReader(className);
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_DEBUG);

        MethodNode invoke = null;
        MethodNode newM = null;
        List methods = cn.methods;
        for(MethodNode m: (List<MethodNode>)methods) {
            if(m.name.equals("invoke")) {
                invoke = m;
                break;
            }
        }

        if(invoke==null) {
            return;
        }

        ArrayList<AbstractInsnNode> r = new Analysis(invoke, d).process();
        newM = new MethodNode(invoke.access,
                "invoke_render_java_lang_String",
                invoke.desc,
                invoke.signature,
                (String[])invoke.exceptions.toArray(new String[invoke.exceptions.size()])
        );
        for(AbstractInsnNode a : r) {
            newM.instructions.add(a.clone(null));
            // DebugUtils.dump(a);
        }
        newM.visitMaxs(0, 0);

        // re-read
        ClassNode cn2 = new ClassNode();
        cr.accept(cn2, 0);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn2.methods.add(newM);
        cn2.accept(cw);
        System.out.println(cw.toByteArray().length);
        new RandomAccessFile(outDir + "/" + d.getClassName() + ".class", "rw").write(cw.toByteArray());
    }

    public static void main(String[] args) throws Throwable {
        // String className = "org.codehaus.groovy.grails.web.metaclass.RenderDynamicMethod";
        // dataFile "c:/grails/render/test/reports/1236727145094_org_codehaus_groovy_grails_web_metaclass_RenderDynamicMethod.bin"
        // outDir = "target-classes" ; for rebuilding grails-web.jar
        process(args[0], args[1], args[2]);
    }

    private static Data readData(String fname) throws IOException, FileNotFoundException, ClassNotFoundException {
        ObjectInputStream ins = new ObjectInputStream(new FileInputStream(fname));
        Data d = (Data)ins.readObject();
        ins.close();
        return d;
    }

}
