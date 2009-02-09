grammar GTS;

options {
  output=AST;
  ASTLabelType=CommonTree;
  backtrack=true;
  k=4;
}

tokens {
  UNIT;
  ASPECT;
  CLASS_MATCHER;
  METHOD_BLOCK;
  METHOD_MATCHER;
  VAR_DECL_LIST;
  VAR_DECL;
  VAR_NAME;
  TYPE_NAME;
  
  ADVICE;
  PCD_BLOCK;
  PCD;
}

@parser::header {package org.codehaus.gts;}
@lexer::header  {package org.codehaus.gts;}

compilationUnit
  : aspect+
  
    -> ^(UNIT aspect+)
  ;
  
aspect
  : classMatcher '{' methodBlock+ '}'
    
    -> ^(ASPECT classMatcher methodBlock+)
  ;
  
classMatcher
	: 	Identifier 
	
  		-> ^(CLASS_MATCHER Identifier)
	;
  
methodBlock
	: 	methodMatcher '{' advice? '}'
	
		-> ^(METHOD_BLOCK methodMatcher ^(ADVICE advice?))
	;
	
advice
	:	(varDecl | pointcutBlock)+
	;	
  
methodMatcher
  	:	Identifier
  		
  		-> ^(METHOD_MATCHER Identifier)
  	;  
  			
pointcutBlock
	:	pointCutExpr '=>' varDeclList ';'
	
		-> ^(PCD_BLOCK pointCutExpr varDeclList)
	;	
	
pointCutExpr
	:	Identifier	
	
		-> ^(PCD Identifier)
	;	
		
varDeclList
	:	varDecl (','! varDecl)*	
	;		
					
varDecl
	:	varName ':' typeName ';'
		
		-> ^(VAR_DECL varName typeName)
	;	
	
varName	
	:	Identifier
	
	;	
	
typeName
	:	Identifier
		
	;	
  
Identifier
  : 'A'..'Z' | 'a'..'z' 
  ;

WS
  :   (' '| '\t'| '\r' | '\n' | '\u000C') {$channel=HIDDEN;}
  ;
