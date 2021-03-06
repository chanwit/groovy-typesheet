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
    CLASS_PCD;
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
    OPT;
    MATCH_SUBCLASS;

    MATCH_PCD;
    MATCH_BLOCK;

    METHOD_PCD;
    METHOD_BLOCK;
    ARGS;
    ARG;

    CALL_PCD;
    CALL_BLOCK;
    
    LOCAL_PCD;
    LOCAL_BLOCK;
    
    ARGS_PCD;
    ARGS_BLOCK;

    MODIFIERS;
    STATIC = 'static';
    
    FIELD_BLOCK;
    FIELD_PCD;
    
    AND;
}

@parser::header {package org.codehaus.groovy.typesheet.parser;}
@lexer::header  {package org.codehaus.groovy.typesheet.parser;}

compilationUnit
    :
        'typesheet' WS? qualifiedIdentifier WS? '{' WS? (classBlock WS?)+ WS? '}'

        -> ^(UNIT qualifiedIdentifier classBlock+)
    ;

classBlock
    :
        classPCD WS? '{' WS? (member WS?)+ WS? '}'

        -> ^(CLASS classPCD member+)
    ;

classPCD
    :
        'class' WS? '(' WS? classPattern WS? ')'

        -> ^(CLASS_PCD classPattern)
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
    :   fieldBlock
    |   methodBlock
    ;

fieldBlock
    :   fieldPCD WS? '{' WS? retypeDecl+ WS? '}'
    
        -> ^(FIELD_BLOCK fieldPCD retypeDecl+)
    ;

fieldPCD
    :   'field' WS? '(' WS? bindingList WS? ')'
    
        -> ^(FIELD_PCD bindingList)
    ;

methodBlock
    :   methodPCD WS? '{' WS? (methodBlockMember WS?)+ WS? '}'
        -> ^(METHOD_BLOCK methodPCD methodBlockMember+)
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
        methodArgTypes?

        -> STATIC? modifiers? methodReturnTypePattern? qualifiedPattern? IDENT methodArgTypes?
    ;
    
methodArgTypes
    :   '(' WS? qualifiedPatternList? WS? ')'
    
        -> qualifiedPatternList?
    ;
    
qualifiedPatternList
    :   qualifiedPattern (WS!? ','! WS!? qualifiedPattern)*
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
    ;

methodBlockMember
    :   argsBlock
    |   argsPCD WS? '&&' WS? matchBlock                         -> ^(AND argsPCD matchBlock)
    |   localBlock
    |   callBlock
    |   callPCD WS? '&&' WS? argsBlock                          -> ^(AND callPCD argsBlock)
    |   callPCD WS? '&&' WS? argsPCD WS? '&&' WS? matchBlock    -> ^(AND callPCD argsPCD matchBlock)
    ;

localBlock
    :   localPCD WS? '{' WS? (retypeDecl WS?)+ WS? '}'
    
        -> ^(LOCAL_BLOCK localPCD retypeDecl+)
    ;

localPCD
    :   'local' WS? '(' WS? (bindingList WS?)+ WS? ')'
    
        -> ^(LOCAL_PCD bindingList+)
    ;
    
argsBlock
    :   argsPCD WS? '{' WS? (retypeDecl WS?)+ WS? '}'
    
        -> ^(ARGS_BLOCK argsPCD retypeDecl+)
    ;

argsPCD
    :   'args' WS? '(' WS? (bindingList WS?)+ WS? ')'
    
        -> ^(ARGS_PCD bindingList+)
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
    :   'case' WS? callPCD WS? ':' WS? '{' WS? (retypeDecl WS?)+ WS? '}'

        -> ^(CASE callPCD retypeDecl+)
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
    :   IDENT (WS!? ','! WS!? IDENT)*
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
    :   IDENT (WS!? ','! WS!? IDENT)*
    ;

qualifiedPattern
    :   qualifiedPatternHead array? subclass?
        -> ^(PATTERN qualifiedPatternHead ^(OPT array? subclass?))

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
