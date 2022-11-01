grammar UCEL;

@header {
    package org.UcelParser.UCELParser_Generated;

    import org.UcelParser.Util.Scope;
    import org.UcelParser.Util.DeclarationReference;
    import org.UcelParser.Util.DeclarationInfo;
    import org.UcelParser.Util.FuncCallOccurrence;
}

project : pdeclaration ptemplate* psystem;

pdeclaration locals [Scope scope]
    : declarations;
ptemplate locals [Scope scope]
    : ID parameters graph declarations;
psystem locals [Scope scope]
    : declarations build? system;

graph : location* edge*;

location
    locals [Boolean isInitial, Boolean isUrgent, Boolean isCommitted, Integer posX,
            Integer posY, String comments, String testCodeEnter, String testCodeExit, Integer id]
    : ID? invariant exponential;
exponential : expression COLON expression;
invariant : expression;

edge locals [Scope scope, Integer locationStartID, Integer locationEndID, String comments, String testCode]
    : select guard sync update;
select : ID COLON type (COMMA ID COLON type)*;
guard : expression;
sync : expression (NEG | QUESTIONMARK);
update : (expression (COMMA expression)*)?;

start locals [Scope scope]
    : declarations statement* system;
    //TODO: Add ? after expression to make the tests run :D
system : SYSTEM expression ((COMMA | '<') expression)* END;

component locals [Scope scope]
    : COMP ID LEFTPAR parameters? RIGHTPAR COLON LEFTPAR parameters? RIGHTPAR LEFTCURLYBRACE compBody RIGHTCURLYBRACE;
compBody locals [Scope scope]
    : declarations? build?;


build : BUILD COLON LEFTCURLYBRACE buildDecl* buildStmnt+ RIGHTCURLYBRACE;
buildStmnt : LINK expression expression END                    #LinkStatement
           | FOR LEFTPAR ID? COLON type? RIGHTPAR buildBlock   #BuildIteration
           | IF LEFTPAR expression RIGHTPAR buildStmnt ( ELSE buildStmnt )?  #BuildIf
           | ID (LEFTBRACKET expression RIGHTBRACKET)* '=' ID LEFTPAR arguments RIGHTPAR END  #CompCon
           ;
buildBlock : LEFTCURLYBRACE buildStmnt+ RIGHTCURLYBRACE;
buildDecl : ID ID (arrayDecl)* END;

interfaceDecl : INTERFACE ID LEFTCURLYBRACE interfaceVarDecl RIGHTCURLYBRACE;
interfaceVarDecl : type arrayDeclID (COMMA type arrayDeclID)*;

instantiation locals [Scope scope, DeclarationReference instantiatedReference, DeclarationReference constructorReference]
    : ID ( LEFTPAR parameters? RIGHTPAR )? '=' ID LEFTPAR arguments? RIGHTPAR END;
progressDecl  : PROGRESS LEFTCURLYBRACE ( expression? END )* RIGHTCURLYBRACE;


parameters : ( parameter (COMMA parameter)* )?;
parameter  locals [DeclarationReference reference]
              : type REF? (BITAND)? ID arrayDecl*;

declarations  : (variableDecl | typeDecl | function | chanPriority | instantiation | component | interfaceDecl)+;
variableDecl  : type variableID (COMMA variableID)* END;

variableID locals [DeclarationReference reference]
              : ID arrayDecl* ('=' initialiser)?;
initialiser   : expression?
              |  LEFTCURLYBRACE initialiser (COMMA initialiser)* RIGHTCURLYBRACE;

typeDecl locals [List<DeclarationReference> references]
              : 'typedef' type arrayDeclID (COMMA arrayDeclID)* END;

arrayDeclID : ID arrayDecl*;

type          : prefix? typeId;
prefix        : 'urgent' | 'broadcast' | 'meta' | 'const';

typeId locals [DeclarationReference reference]
              : ID                                                                               #TypeIDID
              | op=('int' | 'clock' | 'chan' | 'bool' | 'double' | 'string' | 'in' | 'out')      #TypeIDType
              | 'int' LEFTBRACKET expression? COMMA expression? RIGHTBRACKET                     #TypeIDInt
              | 'scalar' LEFTBRACKET expression RIGHTBRACKET                                     #TypeIDScalar
              | 'struct' LEFTCURLYBRACE fieldDecl (fieldDecl)* RIGHTCURLYBRACE                   #TypeIDStruct
              ;

fieldDecl     : type arrayDeclID (COMMA arrayDeclID)* END;
arrayDecl     : LEFTBRACKET expression? RIGHTBRACKET
              | LEFTBRACKET type RIGHTBRACKET;

function locals [Scope scope, List<FuncCallOccurrence> occurrences, DeclarationReference reference]
    : type? ID? LEFTPAR parameters? RIGHTPAR block;
block locals [Scope scope]
    : LEFTCURLYBRACE localDeclaration* statement* RIGHTCURLYBRACE;
localDeclaration  : typeDecl | variableDecl;
statement       : block
                | assignment END
                | expression? END
                | forLoop
                | iteration
                | whileLoop
                | dowhile
                | ifstatement
                | returnStatement;

forLoop	        : FOR LEFTPAR assignment? END expression END expression? RIGHTPAR statement;
iteration locals [DeclarationReference reference]
                : FOR LEFTPAR ID? COLON type RIGHTPAR statement;
whileLoop       : WHILE LEFTPAR expression RIGHTPAR statement;
dowhile         : DO statement WHILE LEFTPAR expression RIGHTPAR END;
ifstatement     : IF LEFTPAR expression RIGHTPAR statement ( ELSE statement )?;
returnStatement : RETURN expression? END;

chanPriority : 'chan' 'priority' (chanExpr | 'default') ((COMMA | '<') (chanExpr | 'default'))* END;
chanExpr locals [DeclarationReference reference]
            : ID
            | chanExpr LEFTBRACKET expression RIGHTBRACKET;

expression locals [DeclarationReference reference, DeclarationInfo originDefinition]
            :  literal                                          #LiteralExpr
            |  ID                                               #IdExpr
            |  expression LEFTBRACKET expression RIGHTBRACKET   #ArrayIndex
            |  expression MARK                                  #MarkExpr
            |  LEFTPAR expression RIGHTPAR                      #Paren
            |  expression '.' ID                                #StructAccess
            |  expression INCREMENT                             #IncrementPost
            |  INCREMENT expression                             #IncrementPre
            |  expression DECREMENT                             #DecrementPost
            |  DECREMENT expression                             #DecrementPre
            |  ID LEFTPAR arguments RIGHTPAR                    #FuncCall
            |  <assoc=right> unary expression                   #UnaryExpr
            |  expression op=('*' | '/' | '%') expression       #MultDiv
            |  expression op=('+' | '-') expression             #AddSub
            |  expression op=('<<' | '>>') expression           #Bitshift
            |  expression op=('<?' | '>?') expression           #MinMax
            |  expression op=('<' | '<=' | '>=' | '>') expression #RelExpr
            |  expression op=('==' | '!=') expression           #EqExpr
            |  expression BITAND expression                     #BitAnd
            |  expression BITXOR expression                     #BitXor
            |  expression BITOR expression                      #BitOr
            |  expression op=('&&' | 'and') expression          #LogAnd
            |  expression op=('||' | 'or' | 'imply') expression #LogOr
            |  expression QUESTIONMARK expression COLON expression       #Conditional
            |  verification                                     #VerificationExpr
            ;

verification locals [Scope scope, DeclarationReference reference] : op=('forall' | 'exists' | 'sum') LEFTPAR ID COLON type RIGHTPAR expression;

assignment  : <assoc=right> expression assign expression #AssignExpr;

arguments  : ((expression | REF ID) (COMMA (expression | REF ID))*)?;

assign     : '=' | ':=' | '+=' | '-=' | '*=' | '/=' | '%='
           | '|=' | '&=' | '^=' | '<<=' | '>>=';
unary      : PLUS | MINUS | NEG | NOT;

literal : NAT | bool | DOUBLE | DEADLOCK;

bool : TRUE | FALSE;

Whitespace : [ \t] + -> channel(HIDDEN)
           ;

Newline : ('\n' | '\r\n' | '\r') -> skip
        ;
BlockComment: '/*' .*? '*/' -> skip;
LineComment: '//' ~ [\r\n]* -> skip;

IN : 'in';
OUT : 'out';
REF : 'ref';
FOR : 'for';
WHILE : 'while';
DO : 'do';
IF : 'if';
RETURN : 'return';
END : ';';
SYSTEM : 'system';
PROGRESS : 'progress';
LEFTPAR : '(';
RIGHTPAR : ')';
LEFTBRACKET : '[';
RIGHTBRACKET : ']';
LEFTCURLYBRACE : '{';
RIGHTCURLYBRACE : '}';
COLON : ':';
COMMA : ',';
MARK : '\'';
PLUS : '+';
MINUS : '-';
NEG : '!';
NOT : 'not';
BITAND : '&';
BITXOR : '^';
BITOR : '|';
QUESTIONMARK : '?';
INCREMENT : '++';
DECREMENT : '--';

COMP : 'comp';
DECLARATIONS : 'declarations';
INTERFACES : 'interfaces';
INTERFACE : 'interface';
UNDERSCORE : '_';
USES : 'uses';
AS : 'as';
CONTAINS : 'contains';
LINK : 'link';
BUILD : 'build';
WITH : 'with';
ELSE : 'else';

TRUE: 'true';
FALSE: 'false';
DEADLOCK : 'deadlock';
DOUBLE : NAT '.' [0-9]+;
NAT : '0' | [1-9]([0-9])*;
ID : [a-zA-Z_]([a-zA-Z0-9_])*;

