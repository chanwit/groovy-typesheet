package org.codehaus.groovy.typesheet.parser;

import org.antlr.runtime.tree.*;
import org.antlr.runtime.*;

public class Main {

static final TreeAdaptor adaptor = new CommonTreeAdaptor() {
	public Object create(Token payload) {
		return new CommonTree(payload);
	}
};

    public static CommonTree getAst(String filename) throws Throwable {
        ANTLRFileStream fs = new ANTLRFileStream(filename);
		TypesheetLexer lex = new TypesheetLexer(fs);
		TokenRewriteStream tokens = new TokenRewriteStream(lex);
		TypesheetParser grammar = new TypesheetParser(tokens);
		grammar.setTreeAdaptor(adaptor);
		TypesheetParser.compilationUnit_return ret = grammar.compilationUnit();
		CommonTree tree = (CommonTree)ret.getTree();        
		return tree;
    }

	public static void main(String[] args) throws Throwable {
	    CommonTree ast = getAst(args[0]);
		//printTree(ast, 0);		
		NewCodeGenerator cg = new NewCodeGenerator(ast);

		System.out.println(cg.generate());
	}
	
	public static void printTree(CommonTree t, int indent) {
		if ( t != null ) {
			StringBuffer sb = new StringBuffer(indent);
			for ( int i = 0; i < indent; i++ )
				sb = sb.append("   ");
			for ( int i = 0; i < t.getChildCount(); i++ ) {
				System.out.println(sb.toString() + t.getChild(i).toString());
				printTree((CommonTree)t.getChild(i), indent+1);
			}
		}
	}
	
}
