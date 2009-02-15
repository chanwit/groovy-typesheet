package org.codehaus.groovy.typesheet.parser

import java.util.*
import org.objectweb.asm.*

import org.antlr.runtime.tree.*
import org.antlr.runtime.*

import static org.codehaus.groovy.typesheet.parser.TypesheetParser.*

public class NewCodeGenerator implements Opcodes {

    private CommonTree ast

    private ClassWriter cw = new ClassWriter(0)
    private FieldVisitor fv
    private MethodVisitor mv
    private AnnotationVisitor av0

    private String className
    private String internalClassName

    NewCodeGenerator(CommonTree ast) {
        this.ast = ast
    }

    public byte[] generate() {
        genClass(this.ast)
        return cw.toByteArray()
    }
    
    def guard(t, type, msg) {
        if(t.token.type != type) throw new RuntimeException(msg)
    }

    /**
    *   Traversal through ^(. A B) to construct a fully string
    **/
    def getQualifiedIdent(CommonTree t) {
        guard(t, DOT, "not dot")

        def c0 = t.children[0]
        def s = c0.toString()
        if(c0.token.type == DOT) 
            s = getQualifiedIdent(c0)

        def c1 = t.children[1]
        return s + "." + c1.toString()
    }

    def getInternalName(s) {
        return s.replace(".", "/")
    }
        
    def genMember(CommonTree t) {
        guard(t, MEMBER, "not member")
        
        
    }
    
    def genClassPCD(CommonTree t) {
        guard(t, CLASS_PCD, "not class pcd")
        
        
    }
    
    def genClassBlock(CommonTree t) {
        guard(t, CLASS, "not class block")
        
        // pseudo code
        // m.method {
        //    
        // }

        genClassPCD(t.children[0])
        t.children[1..-1].each {
            genMember(it)
        }
    }

    def genClass(CommonTree t) {
        if(!t) throw new RuntimeException("error")
        guard(t, UNIT, "not unit")

        // children[0] is the name node
        className = getQualifiedIdent(t.children[0])
        internalClassName = getInternalName(className)

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,
            internalClassName, null,
            "java/lang/Object", null)

        // children[1..N] are class blocks
        t.children[1..-1].each {
            genClassBlock(it)
        }

        cw.visitEnd()
    }
}
