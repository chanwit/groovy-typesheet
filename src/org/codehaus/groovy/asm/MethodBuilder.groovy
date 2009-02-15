package org.codehaus.groovy.asm

import org.objectweb.asm.*

class MethodBuilder implements Opcodes {

    ClassBuilder classBuilder
    String name
    int modifiers
    Closure methodBody

    MethodVisitor mv

    private getClassName(owner) {
        def ownerName = owner;
        if(owner instanceof Class) {
            ownerName = owner.name.replace(".", "/")
        }
        return ownerName
    }

    private splitNameDesc(s) {
        [s[0..s.indexOf('(')-1], s[s.indexOf('(')..-1]]
    }

    def build() {        
        def (n, desc) = splitNameDesc(this.name)    
        this.mv = classBuilder.cw.visitMethod(modifiers, n, desc, null, null);

        methodBody.delegate = this
        methodBody.resolveStrategy = Closure.DELEGATE_FIRST
        methodBody.call()

        mv.visitMaxs(0,0);
        mv.visitEnd();
    }

    def getNop(){
        mv.visitInsn(Opcodes.NOP)
    }

    def getAconst_null(){
        mv.visitInsn(Opcodes.ACONST_NULL)
    }

    def getIconst_m1(){
        mv.visitInsn(Opcodes.ICONST_M1)
    }

    def getIconst_0(){
        mv.visitInsn(Opcodes.ICONST_0)
    }

    def getIconst_1(){
        mv.visitInsn(Opcodes.ICONST_1)
    }

    def getIconst_2(){
        mv.visitInsn(Opcodes.ICONST_2)
    }

    def getIconst_3(){
        mv.visitInsn(Opcodes.ICONST_3)
    }

    def getIconst_4(){
        mv.visitInsn(Opcodes.ICONST_4)
    }

    def getIconst_5(){
        mv.visitInsn(Opcodes.ICONST_5)
    }

    def getLconst_0(){
        mv.visitInsn(Opcodes.LCONST_0)
    }

    def getLconst_1(){
        mv.visitInsn(Opcodes.LCONST_1)
    }

    def getFconst_0(){
        mv.visitInsn(Opcodes.FCONST_0)
    }

    def getFconst_1(){
        mv.visitInsn(Opcodes.FCONST_1)
    }

    def getFconst_2(){
        mv.visitInsn(Opcodes.FCONST_2)
    }

    def getDconst_0(){
        mv.visitInsn(Opcodes.DCONST_0)
    }

    def getDconst_1(){
        mv.visitInsn(Opcodes.DCONST_1)
    }

    def getIaload(){
        mv.visitInsn(Opcodes.IALOAD)
    }

    def getLaload(){
        mv.visitInsn(Opcodes.LALOAD)
    }

    def getFaload(){
        mv.visitInsn(Opcodes.FALOAD)
    }

    def getDaload(){
        mv.visitInsn(Opcodes.DALOAD)
    }

    def getAaload(){
        mv.visitInsn(Opcodes.AALOAD)
    }

    def getBaload(){
        mv.visitInsn(Opcodes.BALOAD)
    }

    def getCaload(){
        mv.visitInsn(Opcodes.CALOAD)
    }

    def getSaload(){
        mv.visitInsn(Opcodes.SALOAD)
    }

    def getIastore(){
        mv.visitInsn(Opcodes.IASTORE)
    }

    def getLastore(){
        mv.visitInsn(Opcodes.LASTORE)
    }

    def getFastore(){
        mv.visitInsn(Opcodes.FASTORE)
    }

    def getDastore(){
        mv.visitInsn(Opcodes.DASTORE)
    }

    def getAastore(){
        mv.visitInsn(Opcodes.AASTORE)
    }

    def getBastore(){
        mv.visitInsn(Opcodes.BASTORE)
    }

    def getCastore(){
        mv.visitInsn(Opcodes.CASTORE)
    }

    def getSastore(){
        mv.visitInsn(Opcodes.SASTORE)
    }

    def getPop(){
        mv.visitInsn(Opcodes.POP)
    }

    def getPop2(){
        mv.visitInsn(Opcodes.POP2)
    }

    def getDup(){
        mv.visitInsn(Opcodes.DUP)
    }

    def getDup_x1(){
        mv.visitInsn(Opcodes.DUP_X1)
    }

    def getDup_x2(){
        mv.visitInsn(Opcodes.DUP_X2)
    }

    def getDup2(){
        mv.visitInsn(Opcodes.DUP2)
    }

    def getDup2_x1(){
        mv.visitInsn(Opcodes.DUP2_X1)
    }

    def getDup2_x2(){
        mv.visitInsn(Opcodes.DUP2_X2)
    }

    def getSwap(){
        mv.visitInsn(Opcodes.SWAP)
    }

    def getIadd(){
        mv.visitInsn(Opcodes.IADD)
    }

    def getLadd(){
        mv.visitInsn(Opcodes.LADD)
    }

    def getFadd(){
        mv.visitInsn(Opcodes.FADD)
    }

    def getDadd(){
        mv.visitInsn(Opcodes.DADD)
    }

    def getIsub(){
        mv.visitInsn(Opcodes.ISUB)
    }

    def getLsub(){
        mv.visitInsn(Opcodes.LSUB)
    }

    def getFsub(){
        mv.visitInsn(Opcodes.FSUB)
    }

    def getDsub(){
        mv.visitInsn(Opcodes.DSUB)
    }

    def getImul(){
        mv.visitInsn(Opcodes.IMUL)
    }

    def getLmul(){
        mv.visitInsn(Opcodes.LMUL)
    }

    def getFmul(){
        mv.visitInsn(Opcodes.FMUL)
    }

    def getDmul(){
        mv.visitInsn(Opcodes.DMUL)
    }

    def getIdiv(){
        mv.visitInsn(Opcodes.IDIV)
    }

    def getLdiv(){
        mv.visitInsn(Opcodes.LDIV)
    }

    def getFdiv(){
        mv.visitInsn(Opcodes.FDIV)
    }

    def getDdiv(){
        mv.visitInsn(Opcodes.DDIV)
    }

    def getIrem(){
        mv.visitInsn(Opcodes.IREM)
    }

    def getLrem(){
        mv.visitInsn(Opcodes.LREM)
    }

    def getFrem(){
        mv.visitInsn(Opcodes.FREM)
    }

    def getDrem(){
        mv.visitInsn(Opcodes.DREM)
    }

    def getIneg(){
        mv.visitInsn(Opcodes.INEG)
    }

    def getLneg(){
        mv.visitInsn(Opcodes.LNEG)
    }

    def getFneg(){
        mv.visitInsn(Opcodes.FNEG)
    }

    def getDneg(){
        mv.visitInsn(Opcodes.DNEG)
    }

    def getIshl(){
        mv.visitInsn(Opcodes.ISHL)
    }

    def getLshl(){
        mv.visitInsn(Opcodes.LSHL)
    }

    def getIshr(){
        mv.visitInsn(Opcodes.ISHR)
    }

    def getLshr(){
        mv.visitInsn(Opcodes.LSHR)
    }

    def getIushr(){
        mv.visitInsn(Opcodes.IUSHR)
    }

    def getLushr(){
        mv.visitInsn(Opcodes.LUSHR)
    }

    def getIand(){
        mv.visitInsn(Opcodes.IAND)
    }

    def getLand(){
        mv.visitInsn(Opcodes.LAND)
    }

    def getIor(){
        mv.visitInsn(Opcodes.IOR)
    }

    def getLor(){
        mv.visitInsn(Opcodes.LOR)
    }

    def getIxor(){
        mv.visitInsn(Opcodes.IXOR)
    }

    def getLxor(){
        mv.visitInsn(Opcodes.LXOR)
    }

    def getI2l(){
        mv.visitInsn(Opcodes.I2L)
    }

    def getI2f(){
        mv.visitInsn(Opcodes.I2F)
    }

    def getI2d(){
        mv.visitInsn(Opcodes.I2D)
    }

    def getL2i(){
        mv.visitInsn(Opcodes.L2I)
    }

    def getL2f(){
        mv.visitInsn(Opcodes.L2F)
    }

    def getL2d(){
        mv.visitInsn(Opcodes.L2D)
    }

    def getF2i(){
        mv.visitInsn(Opcodes.F2I)
    }

    def getF2l(){
        mv.visitInsn(Opcodes.F2L)
    }

    def getF2d(){
        mv.visitInsn(Opcodes.F2D)
    }

    def getD2i(){
        mv.visitInsn(Opcodes.D2I)
    }

    def getD2l(){
        mv.visitInsn(Opcodes.D2L)
    }

    def getD2f(){
        mv.visitInsn(Opcodes.D2F)
    }

    def getI2b(){
        mv.visitInsn(Opcodes.I2B)
    }

    def getI2c(){
        mv.visitInsn(Opcodes.I2C)
    }

    def getI2s(){
        mv.visitInsn(Opcodes.I2S)
    }

    def getLcmp(){
        mv.visitInsn(Opcodes.LCMP)
    }

    def getFcmpl(){
        mv.visitInsn(Opcodes.FCMPL)
    }

    def getFcmpg(){
        mv.visitInsn(Opcodes.FCMPG)
    }

    def getDcmpl(){
        mv.visitInsn(Opcodes.DCMPL)
    }

    def getDcmpg(){
        mv.visitInsn(Opcodes.DCMPG)
    }

    def getIreturn(){
        mv.visitInsn(Opcodes.IRETURN)
    }

    def getLreturn(){
        mv.visitInsn(Opcodes.LRETURN)
    }

    def getFreturn(){
        mv.visitInsn(Opcodes.FRETURN)
    }

    def getDreturn(){
        mv.visitInsn(Opcodes.DRETURN)
    }

    def getAreturn(){
        mv.visitInsn(Opcodes.ARETURN)
    }

    // special name
    def get_return(){
        mv.visitInsn(Opcodes.RETURN)
    }

    def getArraylength(){
        mv.visitInsn(Opcodes.ARRAYLENGTH)
    }

    def getAthrow(){
        mv.visitInsn(Opcodes.ATHROW)
    }

    def getMonitorenter(){
        mv.visitInsn(Opcodes.MONITORENTER)
    }

    def getMonitorexit(){
        mv.visitInsn(Opcodes.MONITOREXIT)
    }

    /** ==  IntInsn == **/

    def bipush(int i) {
        mv.visitIntInsn(Opcodes.BIPUSH, i)
    }

    def sipush(short i) {
        mv.visitIntInsn(Opcodes.SIPUSH, i)
    }

    def newarray(int i) {
        mv.visitIntInsn(Opcodes.NEWARRAY, i)
    }

    /** == field insns == **/

    def getfield(String owner, String name, String desc) {
        mv.visitFieldInsn(Opcodes.GETFIELD, owner, name, desc)
    }

    def putfield(String owner, String name, String desc) {
        mv.visitFieldInsn(Opcodes.PUTFIELD, owner, name, desc)
    }

    def getstatic(String owner, String name, String desc) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, owner, name, desc)
    }

    def putstatic(String owner, String name, String desc) {
        mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, name, desc)
    }

    /**  == iinc == **/

    def iinc(int var, int incr) {
        mv.visitIincInsn(var, incr)
    }

    //IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
    //*        IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ,
    //*        IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.

    def ifeq(Label label) {
        mv.visitJumpInsn(Opcodes.IFEQ, label)
    }

    def ifne(Label label) {
        mv.visitJumpInsn(Opcodes.IFNE, label)
    }

    def iflt(Label label) {
        mv.visitJumpInsn(Opcodes.IFLT, label)
    }

    def ifge(Label label) {
        mv.visitJumpInsn(Opcodes.IFGE, label)
    }

    def ifgt(Label label) {
        mv.visitJumpInsn(Opcodes.IFGT, label)
    }

    def ifle(Label label) {
        mv.visitJumpInsn(Opcodes.IFLE, label)
    }

    def if_icmpeq(Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPEQ, label)
    }

    def if_icmpne(Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPNE, label)
    }

    def if_icmplt(Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPLT, label)
    }

    def if_icmpge(Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPGE, label)
    }

    def if_icmpgt(Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPGT, label)
    }

    def if_icmple(Label label) {
        mv.visitJumpInsn(Opcodes.IF_ICMPLE, label)
    }

    def if_acmpeq(Label label) {
        mv.visitJumpInsn(Opcodes.IF_ACMPEQ, label)
    }

    def if_acmpne(Label label) {
        mv.visitJumpInsn(Opcodes.IF_ACMPNE, label)
    }

    def gotolabel(Label label) {
        mv.visitJumpInsn(Opcodes.GOTO, label)
    }

    def jsr(Label label) {
        mv.visitJumpInsn(Opcodes.JSR, label)
    }

    def ifnull(Label label) {
        mv.visitJumpInsn(Opcodes.IFNULL, label)
    }

    def ifnonnull(Label label) {
        mv.visitJumpInsn(Opcodes.IFNONNULL, label)
    }

    def label() {
        def l = new Label()
        mv.visitLabel(l)
        return l
    }

    def ldc(cst) {
        mv.visitLdcInsn(cst)
    }

    // TODO line number
    // TODO local var

    def lookupswitch(Label dflt, int[] keys, Label[] labels) {
        mv.visitLookupSwitchInsn(dflt, keys, labels)
    }

    //    * opcode must be INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or
    //    * INVOKEINTERFACE.
    def invokevirtual(owner, name, desc=null) {
        if(!desc) (name, desc) = splitNameDesc(name)
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getClassName(owner), name, desc)
    }

    def invokespecial(owner, name, desc=null) {
        if(!desc) (name, desc) = splitNameDesc(name)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, getClassName(owner), name, desc)
    }

    def invokestatic(owner, name, desc=null) {
        if(!desc) (name, desc) = splitNameDesc(name)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, getClassName(owner), name, desc)
    }

    def invokeinterface(owner, name, desc=null) {
        if(!desc) (name, desc) = splitNameDesc(name)
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, getClassName(owner), name, desc)
    }

    def multianewarray(desc, dims) {
        mv.visitMultiANewArrayInsn(desc, dims)
    }

    def tableswitch(min, max, dflt, Label[] labels) {
        mv.visitTableSwitchInsn(min, max, dflt, labels)
    }

    // TYPE
    // NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
    def checkcast() {
        throw new RuntimeException("not implemented")
    }

    def anewarray() {
        throw new RuntimeException("not implemented")
    }

    def newobj() {
        throw new RuntimeException("not implemented")
    }

    def instance_of() {
        throw new RuntimeException("not implemented")
    }

    // ILOAD, LLOAD, FLOAD, DLOAD,
    // ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
    def iload(int var) {
        mv.visitVarInsn(Opcodes.ILOAD, var)
    }

    def lload(int var) {
        mv.visitVarInsn(Opcodes.LLOAD, var)
    }

    def fload(int var) {
        mv.visitVarInsn(Opcodes.FLOAD, var)
    }

    def dload(int var) {
        mv.visitVarInsn(Opcodes.DLOAD, var)
    }

    def aload(int var) {
        mv.visitVarInsn(Opcodes.ALOAD, var)
    }

    def istore(int var) {
        mv.visitVarInsn(Opcodes.ISTORE, var)
    }

    def lstore(int var) {
        mv.visitVarInsn(Opcodes.LSTORE, var)
    }

    def fstore(int var) {
        mv.visitVarInsn(Opcodes.FSTORE, var)
    }

    def dstore(int var) {
        mv.visitVarInsn(Opcodes.ASTORE, var)
    }

    def astore(int var) {
        mv.visitVarInsn(Opcodes.ASTORE, var)
    }

    def ret(int var) {
        mv.visitVarInsn(Opcodes.RET, var)
    }

}