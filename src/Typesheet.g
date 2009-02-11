grammar Typesheet;

options {
    output=AST;
    ASTLabelType=CommonTree;
    backtrack=true;
    k=3;
}

tokens {
    UNIT;
    ASPECT;
    CLASS;
    CLASS_MATCHER;
    METHOD_BLOCK;
    METHOD_MATCHER;
    VAR_DECL_LIST;
    VAR_DECL;
    VAR_NAME;
    TYPE_NAME;
    DOT= '.' ;
    ADVICE;
    PCD_BLOCK;
    PCD;
}

@parser::header {package org.codehaus.groovy.typesheet.parser;}
@lexer::header  {package org.codehaus.groovy.typesheet.parser;}

compilationUnit
    :
        aspect+

        -> ^(UNIT aspect+)
    ;

aspect
    :
        classPCD WS? '{' WS? (member WS?)+ WS? '}'

        -> ^(ASPECT classPCD member+)
    ;

classPCD
    :
        'class' WS? '(' WS? classPattern WS? ')'

        -> ^(CLASS classPattern)
    ;

classPattern
    :   ('static' WS)? (modifiers WS)? classPatternRest
    ;

classPatternRest
    :   qualifiedPattern
    ;

modifiers
    :   '*'
    |   'public'
    |   'protected'
    |   'private'
    ;

member
    :
        fieldBlock
    ;

fieldBlock
    :   fieldPCD WS? '{' WS? retypeDecl+ WS? '}'
    ;

fieldPCD
    :   'field' WS? '(' WS? bindingList WS? ')'
    ;

methodBlock
    :   methodPCD WS? '{' WS? (methodBlockMember WS?)+ WS? '}'
    ;

methodPCD
    :   'method' WS? '(' WS? methodPattern? WS? ')'
    ;

methodPattern
    :   ('static' WS)? 
        (modifiers WS)? 
        (qualifiedPattern WS)? // return type class name
        (qualifiedPattern '#')? IDENT WS? // method name (fully qualified following by #)
        methodArgs? // 
    ;

methodArgs
    :   '(' WS? methodArgsBindingList? WS? ')'
    ;

methodArgsBindingList
    :   methodArgBinding (WS? ','  WS? methodArgBinding)*
    ;

methodArgBinding
    :   methodPattern
    |   (qualifiedPattern WS)? IDENT
    |   qualifiedPattern
    ;

methodBlockMember
    :   retypeDecl
    |   localBlock
    |   callBlock
    ;

localBlock
    :   localPCD WS? '{' WS? (retypeDecl WS?)+ WS? '}'
    ;

localPCD
    :   'local' WS? '(' WS? (bindingList WS?)+ WS? ')'
    ;
    
callBlock
    :   callPCD WS? '{' WS? (retypeDecl WS?)+ WS?'}'
    ;
        
callPCD 
    :   'call' WS? '(' WS? methodPattern WS? ')'
    ;
        
retypeDecl
    :   (var | varList) WS? '~>' WS? type WS?';'
    ;

var
    :   IDENT
    ;

varList
    :   IDENT (WS? ',' WS? IDENT)+
    ;

type
    :   qualifiedIdentifier WS? array?
    |   primitives WS? array?
    ;

primitives
    :   'int'
    |   'long'
    |   'byte'
    |   'char'
    |   'short'
    |   'boolean'
    |   'double'
    |   'float'
    |   'void'
    |   'def'
    ;

bindingList
    :   IDENT (WS? ',' WS? IDENT)*
    ;

qualifiedPattern
    :   identifierPattern (DOT identifierPattern)* array? ('+')?
    |   primitives array?
    ;
    
array
    :   ('[' WS? ']')+
    ;

identifierPattern
    :   '*'
    |   IDENT
    |   '*'? IDENT '*'?
    ;

qualifiedIdentifier
    :   (   IDENT               ->  IDENT
        )
        (   DOT ident=IDENT     ->  ^(DOT $qualifiedIdentifier $ident)
        )*
    ;

IDENT
    :   JAVA_ID_START (JAVA_ID_PART)*
    ;

fragment
JAVA_ID_START
    :  '\u0024'
    |  '\u0041'..'\u005a'
    |  '\u005f'
    |  '\u0061'..'\u007a'
    |  '\u00c0'..'\u00d6'
    |  '\u00d8'..'\u00f6'
    |  '\u00f8'..'\u00ff'
    |  '\u0100'..'\u1fff'
    |  '\u3040'..'\u318f'
    |  '\u3300'..'\u337f'
    |  '\u3400'..'\u3d2d'
    |  '\u4e00'..'\u9fff'
    |  '\uf900'..'\ufaff'
    ;

fragment
JAVA_ID_PART
    :  JAVA_ID_START
    |  '\u0030'..'\u0039'
    ;

WS
    :   (' '| '\t'| '\r' | '\n' | '\u000C')+
    ;
