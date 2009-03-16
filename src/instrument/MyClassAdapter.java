package instrument;

import java.util.HashMap;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MyClassAdapter extends ClassAdapter implements Opcodes {

    private String name;

    class MyMethodAdapter extends MethodAdapter {

        int num = 0;
        HashMap<Label, Boolean> keep = new HashMap<Label, Boolean>();

        public MyMethodAdapter(MethodVisitor mv) { super(mv); }

        @Override
        public void visitCode() {
            super.visitCode();
            // TODO: make sure visits can be working just after visitCode
            mv.visitLdcInsn(name);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "instrument/Marker", "args", "([Ljava/lang/Object;)V");
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
            if(opcode >= IFEQ && opcode <= IFLE) {
                touch();
                keep(label);
            }
        }

        @Override
        public void visitLabel(Label label) {
            super.visitLabel(label);

            if(keep.containsKey(label)) {
                if(keep.get(label) == true) {
                    touch();
                }
            }
        }

        private void keep(Label label) {
            this.keep.put(label, true);
        }

        private void touch() {
            mv.visitIntInsn(SIPUSH, num++);
            mv.visitMethodInsn(INVOKESTATIC, "instrument/Marker", "touch", "(I)V");
        }

        private void touch_end() {
            mv.visitMethodInsn(INVOKESTATIC, "instrument/Marker", "end", "()V");
        }

        @Override
        public void visitInsn(int opcode) {
            if( (opcode >= IRETURN && opcode <= RETURN) ||
                 opcode == ATHROW ) {
                touch_end();
            }
            super.visitInsn(opcode);
        }
    }

    public MyClassAdapter(ClassVisitor cv) {
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {

        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        if(!"invoke".equals(name))
            return mv;

        return new MyMethodAdapter(mv);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        this.name = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

}
