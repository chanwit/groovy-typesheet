package instrument;

import java.io.IOException;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.EmptyVisitor;

public class ClosureScanner extends ClassAdapter {

    private String closureName;
    private boolean mayBeMatch=false;
    private boolean match=false;
    private String closureClassName;

    private class MV extends MethodAdapter {

        private int state;
        private String mayBeClosureClassName;

        public MV(MethodVisitor mv) {
            super(mv);
        }

//      INVOKESPECIAL TestController$_closure1.<init>(Object,Object) : void
//      DUP
//      ALOAD 0: this
//      SWAP
//      PUTFIELD TestController.index : Object

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if(opcode == Opcodes.INVOKESPECIAL
                    && owner.indexOf("$_closure")!=-1
                    && name.equals("<init>") && state == 0) {
                state = 1;
                mayBeClosureClassName = owner;
            }
            super.visitMethodInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitInsn(int opcode) {
            if(opcode == Opcodes.DUP && state == 1 ) {
                state = 2;
            } else if(opcode == Opcodes.SWAP && state == 3) {
                state = 4;
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if(opcode == Opcodes.ALOAD && var == 0 && state == 2) {
                state = 3;
            }
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name,
                String desc) {
            if(opcode == Opcodes.PUTFIELD && state == 4 && name.equals(closureName)) {
                state = 5;
            }
            if(mayBeMatch && state == 5) {
                match = true;
                closureClassName = mayBeClosureClassName;
            }
            super.visitFieldInsn(opcode, owner, name, desc);
        }

    }

    public ClosureScanner(Class<?> target, String closureName) {
        super(new EmptyVisitor());
        this.closureName = closureName;
        try {
            ClassReader cr = new ClassReader(target.getName());
            cr.accept(this, 0);
        } catch (IOException e) {
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc,
            String signature, Object value) {
        if(name.equals(closureName) &&
                ( desc.equals("java/lang/Object") ||
                  desc.equals("groovy/lang/Closure")
                )
           ) {
            mayBeMatch = true;
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if(name.equals("<init>")) {
            return new MV(mv);
        }
        return mv;
    }

    public boolean isMatch() {
        return this.match;
    }

    public String getClosureClassName() {
        return this.closureClassName;
    }

}
