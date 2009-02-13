grammar Typesheet;

options {
    output=AST;
    ASTLabelType=CommonTree;
    backtrack=true;
    k=5;
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
    RETYPE;
    CASE;
    DEFAULT_CASE;
    TYPE;
    PRIMITIVE;
    ARRAY;
    PATTERN;
    PAT_OPTION;
    MATCH_SUBCLASS;

    MATCH_PCD;
    MATCH_BLOCK;

    METHOD_PCD;
    METHOD_BLOCK;
    ARGS;
    ARG;

    CALL_PCD;
    CALL_BLOCK;

    MODIFIERS;
    STATIC = 'static';
}

@parser::header {package org.codehaus.groovy.typesheet.parser;}
@lexer::header  {package org.codehaus.groovy.typesheet.parser;}

compilationUnit
    :
        classBlock+

        -> ^(UNIT classBlock+)
    ;

classBlock
    :
        classPCD WS? '{' WS? (member WS?)+ WS? '}'

        -> ^(CLASS classPCD member+)
    ;

classPCD
    :
        'class' WS? '(' WS? classPattern WS? ')'

        -> ^(CLASS_MATCHER classPattern)
    ;

classPattern
    :   ('static' WS!)? (modifiers WS!)? classPatternRest
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

        -> ^(METHOD_PCD methodPattern?)
    ;

// for method definition
methodPattern
    :   (STATIC WS)?
        (modifiers WS)?
        methodReturnTypePattern? // return type class name
        (qualifiedPattern '#')? IDENT WS? // method name (fully qualified following by #)
        methodArgs?

        -> STATIC? modifiers? methodReturnTypePattern? qualifiedPattern? IDENT methodArgs?
    ;

// for calling () is required
methodCallPattern
    :   (STATIC WS)?
        (modifiers WS)?
        methodReturnTypePattern? // return type class name
        (qualifiedPattern '#')? IDENT WS? // method name (fully qualified following by #)
        '(' WS? methodArgsBindingList? WS? ')'

        -> STATIC? modifiers? methodReturnTypePattern? qualifiedPattern? IDENT methodArgsBindingList?
    ;


methodReturnTypePattern
    :   qualifiedPattern WS!
    ;

methodArgs
    :   '(' WS? methodArgsBindingList? WS? ')'

        -> methodArgsBindingList?
    ;

methodArgsBindingList
    :   methodArgBinding (WS!? ','!  WS!? methodArgBinding)*
    ;

methodArgBinding
    :   IDENT                           -> ^(ARG IDENT)
    |   qualifiedPattern                -> ^(ARG qualifiedPattern)
    |   qualifiedPattern WS IDENT       -> ^(ARG qualifiedPattern IDENT)    
    // |   methodCallPattern               -> ^(ARG methodCallPattern)
    ;

methodBlockMember
    :   argsBlock
    |   localBlock
    |   callBlock
    |   callPCD WS? '&&' WS? matchBlock
    ;

localBlock
    :   localPCD WS? '{' WS? (retypeDecl WS?)+ WS? '}'
    ;

localPCD
    :   'local' WS? '(' WS? (bindingList WS?)+ WS? ')'
    ;
    
argsBlock
    :   localPCD WS? '{' WS? (retypeDecl WS?)+ WS? '}'
    ;

argsPCD
    :   'args' WS? '(' WS? (bindingList WS?)+ WS? ')'
    ;
    
callBlock
    :   callPCD WS? '{' WS? (retypeDecl WS?)+ WS? '}'

        -> ^(CALL_BLOCK callPCD retypeDecl+ )
    ;

callPCD
    :   'call' WS? '(' WS? methodPattern WS? ')'

        -> ^(CALL_PCD methodPattern)
    ;

matchBlock
    :   matchPCD WS? '{' WS? matchBlockBody WS? '}'

        -> ^(MATCH_BLOCK matchPCD matchBlockBody)
    ;

matchPCD
    :   'match' WS? '(' WS? (bindingList WS?)+ WS? ')'

        -> ^(MATCH_PCD bindingList+)
    ;

matchBlockBody
    :   (caseStmt WS!?)+
        (defaultStmt WS!?)?
    ;

caseStmt
    :   'case' WS? ':' WS? callBlock

        -> ^(CASE callBlock)
    ;

defaultStmt
    :   'default' WS? ':' WS? '{' WS? (retypeDecl WS?)+ WS? '}'

        -> ^(DEFAULT_CASE retypeDecl+)
    ;

retypeDecl
    :   varList WS? '~>' WS? type WS? ';'

        -> ^(RETYPE varList type)
    ;

var
    :   IDENT
    ;

varList
    :   IDENT (WS? ',' WS? IDENT)*
    ;

type
    :   qualifiedIdentifier WS? array?  ->  ^(TYPE qualifiedIdentifier array?)
    |   primitives array?               ->  ^(PRIMITIVE primitives array?)
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
    |   'def'       -> 'void'
    ;

bindingList
    :   IDENT (WS!? ',' WS!? IDENT)*
    ;

qualifiedPattern
    :   qualifiedPatternHead array? subclass?
        -> ^(PATTERN qualifiedPatternHead ^(PAT_OPTION array? subclass?))

    |   primitives array?
        -> ^(PRIMITIVE primitives array?)
    ;

qualifiedPatternHead
    :   identifierPattern (DOT identifierPattern)*
    ;

subclass
    :   '+'

        -> MATCH_SUBCLASS
    ;

array
    :   ('[' WS? ']')+

        -> ARRAY
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
