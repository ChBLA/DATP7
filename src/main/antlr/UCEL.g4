grammar UCEL;

@header {
    package UCELParser_Generated;

    import Util.Scope;
    import Util.DeclarationReference;
}

start locals [Scope scope]
    : declarations statement* link_stmnt* system;
system : SYSTEM ID ((COMMA | '<') ID)* END;

component locals [Scope scope]
    : COMP ID LEFTPAR parameters? RIGHTPAR LEFTCURLYBRACE comp_body RIGHTCURLYBRACE;
comp_body locals [Scope scope]
    : interfaces? (DECLARATIONS COLON declarations)? links?;

interfaces : INTERFACES COLON interface_stmnt+;
interface_stmnt : (IN | OUT) ID ID (COMMA (IN | OUT) ID ID)* END;

links : LINKS COLON link_stmnt+;
link_stmnt : arrayDeclID LINK_OP arrayDeclID WITH ID END
           | FOR LEFTPAR ID? COLON type? RIGHTPAR link_block
           | IF LEFTPAR expression RIGHTPAR link_block
                (elif LEFTPAR expression RIGHTPAR link_block)*
                (ELSE link_block)?;
link_block : LEFTCURLYBRACE link_stmnt+ RIGHTCURLYBRACE;
elif : ELSE IF;

interface_decl : INTERFACE ID LEFTCURLYBRACE interfaceVarDecl RIGHTCURLYBRACE;
interfaceVarDecl : type arrayDeclID (COMMA type arrayDeclID)*;

instantiation : ID ( LEFTPAR parameters? RIGHTPAR )? '=' ID LEFTPAR arguments? RIGHTPAR END;
progressDecl  : PROGRESS LEFTCURLYBRACE ( expression? END )* RIGHTCURLYBRACE;


parameters : ( parameter (COMMA parameter)* )?;
parameter  : type? REF? ('&')? ID? arrayDecl*;

declarations  : (variableDecl | typeDecl | function | chanPriority | component | interface_decl)*;
variableDecl  : type? variableID (COMMA variableID)* END;

variableID locals [DeclarationReference reference]
              : ID arrayDecl* ('=' initialiser)?;
initialiser   : expression?
              |  LEFTCURLYBRACE initialiser (COMMA initialiser)* RIGHTCURLYBRACE;
typeDecl : 'typedef' type arrayDeclID (COMMA arrayDeclID)* END;

arrayDeclID locals [DeclarationReference reference]
              : ID arrayDecl*;

type          : prefix? typeId;
prefix        : 'urgent' | 'broadcast' | 'meta' | 'const';

typeId locals [DeclarationReference reference]
              : ID                                                                               #TypeIDID
              | op=('int' | 'clock' | 'chan' | 'bool' | 'double' | 'string' | 'in' | 'out')      #TypeIDType
              | 'int' LEFTBRACKET expression? COMMA expression? RIGHTBRACKET                     #TypeIDInt
              | 'scalar' LEFTBRACKET expression RIGHTBRACKET                                     #TypeIDScalar
              | 'struct' LEFTCURLYBRACE fieldDecl (fieldDecl)* RIGHTCURLYBRACE                   #TypeIDStruct
              ;

fieldDecl     : type ID arrayDecl* (COMMA ID arrayDecl*)* END;
arrayDecl     : LEFTBRACKET expression? RIGHTBRACKET
              | LEFTBRACKET type RIGHTBRACKET;

function locals [Scope scope]
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
                | returnstatement;

forLoop	        : FOR LEFTPAR assignment? END expression END expression? RIGHTPAR statement;
iteration locals [DeclarationReference reference]
                : FOR LEFTPAR ID? COLON type? RIGHTPAR statement;
whileLoop       : WHILE LEFTPAR expression RIGHTPAR statement;
dowhile         : DO statement WHILE LEFTPAR expression RIGHTPAR END;
ifstatement     : IF LEFTPAR expression RIGHTPAR statement ( ELSE statement )?;
returnstatement : RETURN expression? END;

chanPriority : 'chan' 'priority' (chanExpr | 'default') ((COMMA | '<') (chanExpr | 'default'))* END;
chanExpr : ID
           | chanExpr LEFTBRACKET expression RIGHTBRACKET;

expression locals [DeclarationReference reference]
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

arguments  : ((expression | REF ID) ( COMMA (expression | REF ID))*)?;

assign     : '=' | ':=' | '+=' | '-=' | '*=' | '/=' | '%='
           | '|=' | '&=' | '^=' | '<<=' | '>>=';
unary      : PLUS | MINUS | NEG | NOT;

literal : NAT | boolean | DOUBLE | DEADLOCK;

boolean : TRUE | FALSE;

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
LINKS : 'links';
LINK_OP : '->' | '<-' | '<->';
WITH : 'with';
ELSE : 'else';

TRUE: 'true';
FALSE: 'false';
DEADLOCK : 'deadlock';
DOUBLE : NAT '.' [0-9]+;
NAT : '0' | [1-9]([0-9])*;
ID : [a-zA-Z_]([a-zA-Z0-9_])*;

