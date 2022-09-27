grammar UCEL;

start : declarations system;
system : SYSTEM ID ((COMMA | '<') ID)* END;

component : COMP ID LEFTPAR parameters? RIGHTPAR LEFTCURLYBRACE comp_body RIGHTCURLYBRACE;
comp_body : interfaces? uses? (DECLARATIONS COLON declarations)? contains? links?;

interfaces : INTERFACES COLON interface_stmnt+;
interface_stmnt : ID (ID arrayDecl*  | UNDERSCORE) COMMA (ID arrayDecl* | UNDERSCORE) END;

uses : USES COLON uses_stmnt+;
uses_stmnt : type ID arrayDecl* (AS ID arrayDecl*)? END;

contains : CONTAINS COLON expression (COMMA expression)* END;

links : LINKS COLON link_stmnt+;
link_stmnt : ID arrayDecl* LINK_OP ID arrayDecl* WITH ID END
           | FOR LEFTPAR ID? COLON type? RIGHTPAR link_block
           | IF LEFTPAR expression RIGHTPAR link_block
                (elif LEFTPAR expression RIGHTPAR link_block)*
                (ELSE link_block)?;
link_block : LEFTCURLYBRACE link_stmnt+ RIGHTCURLYBRACE;
elif : ELSE IF;

interface_decl : INTERFACE ID LEFTCURLYBRACE interfaceVarDecl RIGHTCURLYBRACE;
interfaceVarDecl : type ID arrayDecl* (COMMA type ID arrayDecl*)*;

instantiation : ID ( LEFTPAR parameters? RIGHTPAR )? '=' ID LEFTPAR arguments? RIGHTPAR END;
progressDecl  : PROGRESS LEFTCURLYBRACE ( expression? END )* RIGHTCURLYBRACE;


parameters : ( parameter (COMMA parameter)* )?;
parameter  : type? ('&')? ID? arrayDecl*;

declarations  : (variableDecl | typeDecl | function | chanPriority | component | interface_decl | link_stmnt)*;
variableDecl  : type? variableID (COMMA variableID)* END;
variableID    : ID arrayDecl* ('=' initialiser)?;
initialiser   : expression?
              |  LEFTCURLYBRACE initialiser (COMMA initialiser)* RIGHTCURLYBRACE;
typeDecl      : 'typedef' type ID arrayDecl* (COMMA ID arrayDecl*)* END;
type          : prefix? typeId;
prefix        : 'urgent' | 'broadcast' | 'meta' | 'const';
typeId        : ID | 'int' | 'clock' | 'chan' | 'bool' | 'double' | 'string' | 'in' | 'out'
              | 'int' LEFTBRACKET expression? COMMA expression? RIGHTBRACKET
              | 'scalar' LEFTBRACKET expression RIGHTBRACKET
              | 'struct' LEFTCURLYBRACE fieldDecl (fieldDecl)* RIGHTCURLYBRACE;
fieldDecl     : type ID arrayDecl* (COMMA ID arrayDecl*)* END;
arrayDecl     : LEFTBRACKET expression? RIGHTBRACKET
              | LEFTBRACKET type RIGHTBRACKET;

function        : type? ID? LEFTPAR parameters? RIGHTPAR block;
block           : LEFTCURLYBRACE localDeclaration* statement* RIGHTCURLYBRACE;
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

forLoop	        : FOR LEFTPAR assignment? END expression? END expression? RIGHTPAR statement;
iteration       : FOR LEFTPAR ID? COLON type? RIGHTPAR statement;
whileLoop       : WHILE LEFTPAR expression? RIGHTPAR statement;
dowhile         : DO statement WHILE LEFTPAR expression? RIGHTPAR END;
ifstatement     : IF LEFTPAR expression? RIGHTPAR statement ( ELSE statement )?;
returnstatement : RETURN expression? END;

chanPriority : 'chan' 'priority' (chanExpr | 'default') ((COMMA | '<') (chanExpr | 'default'))* END;
chanExpr : ID
           | chanExpr LEFTBRACKET expression RIGHTBRACKET;

expression  : ID                                                #IdExpr
            |  literal                                          #LiteralExpr
            |  expression LEFTBRACKET expression RIGHTBRACKET   #ArrayIndex
            |  expression MARK                                  #MarkExpr
            |  LEFTPAR expression RIGHTPAR                      #Paren
            |  expression '.' ID                                #Access
            |  expression '++'                                  #IncrementPost
            | '++' expression                                   #IncrementPre
            |  expression '--'                                  #DecrementPost
            | '--' expression                                   #DecrementPre
            |  expression LEFTPAR arguments RIGHTPAR            #FuncCall
            |  <assoc=right> unary expression                   #UnaryExpr
            |  expression op=('*' | '/' | '%') expression       #MultDiv
            |  expression op=('+' | '-') expression             #AddSub
            |  expression op=('<<' | '>>') expression           #Bitshift
            |  expression op=('<?' | '>?') expression           #MinMax
            |  expression op=('<' | '<=' | '>=' | '>') expression #RelExpr
            |  expression op=('==' | '!=') expression           #EqExpr
            |  expression '&' expression                        #BitAnd
            |  expression '^' expression                        #BitXor
            |  expression '|' expression                        #BitOr
            |  expression op=('&&' | 'and') expression          #LogAnd
            |  expression op=('||' | 'or' | 'imply') expression #LogOr
            |  expression '?' expression COLON expression       #Conditional
            |  op=('forall' | 'exists' | 'sum') LEFTPAR ID COLON type RIGHTPAR expression #VerificationExpr
            ;

assignment  : <assoc=right> expression assign expression #AssignExpr;

arguments  : (expression ( COMMA expression )*)?;

assign     : '=' | ':=' | '+=' | '-=' | '*=' | '/=' | '%='
           | '|=' | '&=' | '^=' | '<<=' | '>>=';
unary      : '+' | '-' | '!' | 'not';

literal : NAT | boolean | DOUBLE | DEADLOCK;

boolean : 'true' | 'false';

Whitespace : [ \t] + -> channel(HIDDEN)
           ;

Newline : ('\n' | '\r\n' | '\r') -> skip
        ;
BlockComment: '/*' .*? '*/' -> skip;
LineComment: '//' ~ [\r\n]* -> skip;

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

DEADLOCK : 'deadlock';
DOUBLE : NAT '.' [0-9]+;
NAT : '0' | [1-9]([0-9])*;
ID : [a-zA-Z_]([a-zA-Z0-9_])*;

