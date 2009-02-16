package org.codehaus.groovy.typesheet.parser

import java.util.*

import org.antlr.runtime.tree.*
import org.antlr.runtime.*

import org.codehaus.groovy.asm.*

import static org.codehaus.groovy.typesheet.parser.TypesheetParser.*

public class NewCodeGenerator {

    private CommonTree ast

    private ClassBuilder cb = new ClassBuilder()

    private String className
    private String internalClassName

    NewCodeGenerator(CommonTree ast) {
        this.ast = ast
    }

    public byte[] generate() {
        genClass(this.ast)
        return cb.cw.toByteArray()
    }

    def guard(t, type, msg) {
        if(t?.token?.type != type) throw new RuntimeException(msg)
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
        return "${s}.${c1.toString()}"
    }

    def genMember(CommonTree t) {
        // guard(t, MEMBER, "not member")

    }

    def genClassPCD(CommonTree t) {
        guard(t, CLASS_PCD, "not class pcd")


    }

    def genClassBlock(CommonTree t) {
        guard(t, CLASS, "not class block")

        cb.public_method (void, "function_1") {

        }

        genClassPCD(t.children[0])
        t.children[1..-1].each {
            genMember(it)
        }
    }

    def getInternalName(s) {
        return s.replace(".", "/")
    }

    def genClass(CommonTree t) {
        guard(t, UNIT, "not unit")

        // children[0] is the name node
        className = getQualifiedIdent(t.children[0])
        internalClassName = getInternalName(className)

        cb.public_class(name: internalClassName) {
            // set class file version to Java 1.5
            version 1.5

            // create the default constructor
            public_constructor() {
                aload 0
                invokespecial Object.class, "<init>", "()V"
                _return
            }

            t.children[1..-1].each { classBlock ->
                genClassBlock(classBlock)
            }

        }
    }
}
