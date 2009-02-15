package org.codehaus.groovy.typesheet.parser;

import java.util.*;
import org.objectweb.asm.*;

import org.antlr.runtime.tree.*;
import org.antlr.runtime.*;

import static org.codehaus.groovy.typesheet.parser.TypesheetParser.*;

public class CodeGenerator implements Opcodes {

    private CommonTree ast;

    private ClassWriter cw = new ClassWriter(0);
    private FieldVisitor fv;
    private MethodVisitor mv;
    private AnnotationVisitor av0;

    private String className;
    private String internalClassName;

    public CodeGenerator(CommonTree ast) {
        this.ast = ast;
    }

    public byte[] generate() {
        genClass(this.ast);
        return cw.toByteArray();
    }

    /**
    *   Traversal throu ^(. A B) to construct a fully string
    **/
    private String getQualifiedIdent(CommonTree t) {
        if(t.getToken().getType() != DOT) throw new RuntimeException("not dot");
        CommonTree c0 = (CommonTree)t.getChild(0);
        String s = c0.toString();
        if(c0.getToken().getType() == DOT) {
            s = getQualifiedIdent(c0);
        }
        CommonTree c1 = (CommonTree)t.getChild(1);
        return s + "." + c1.toString();
    }

    private String getInternalName(String s) {
        return s.replace(".", "/");
    }

    private void genClass(CommonTree t) {
        if(t==null) throw new RuntimeException("error");
        if(t.getToken().getType() !=  UNIT) throw new RuntimeException("not unit");

        // System.out.println("token: " + t.getToken());
        // System.out.println("token: " + ((CommonTree)t.getChild(0)).getToken());

        // child(0) is the name node
        className = getQualifiedIdent((CommonTree)t.getChild(0));
        internalClassName = getInternalName(className);

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,
            internalClassName, null,
            "java/lang/Object", null);

        // chile(1..N) are class blocks
        for(int i=1; i<t.getChildCount(); i++) {
            genClassBlock((CommonTree)t.getChild(i));
        }

        cw.visitEnd();
    }
}
