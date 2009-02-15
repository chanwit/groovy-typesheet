package org.codehaus.groovy.typesheet.parser;

import org.antlr.runtime.tree.*;
import org.antlr.runtime.*;

public class CodeGenerator extends Object {

    private CommonTree ast;

    public CodeGenerator(CommonTree ast) {
        this.ast = ast;
    }

    public void generate() {
        genClass(this.ast);
    }

    private void genClass(CommonTree t) {

    }
}
