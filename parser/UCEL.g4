grammar UCEL;

start : declarations system;
system : SYSTEM ID ((COMMA | '<') ID)* END;

instantiation : ID ( LEFTPAR parameters? RIGHTPAR )? '=' ID LEFTPAR arguments? RIGHTPAR END;
progressDecl  : PROGRESS LEFTCURLYBRACE ( expression? END )* RIGHTCURLYBRACE;


parameters : ( parameter (COMMA parameter)* )?;
parameter  : type? ('&')? ID? arrayDecl*;

declarations  : (variableDecl | typeDecl | function | chanPriority)*;
variableDecl  : type? variableID (COMMA variableID)* END;
variableID    : ID arrayDecl* ('=' initialiser)?;
initialiser   : expression?
              |  LEFTCURLYBRACE initialiser (COMMA initialiser)* RIGHTCURLYBRACE;
typeDecl      : 'typedef' type ID arrayDecl* (COMMA ID arrayDecl*)* END;
type          : prefix typeId;
prefix        : 'urgent' | 'broadcast' | 'meta' | 'const';
typeId        : ID | 'int' | 'clock' | 'chan' | 'bool' | 'double' | 'string'
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
                | END
                | expression? END
                | forLoop
                | iteration
                | whileLoop
                | dowhile
                | ifstatement
                | returnstatement;

forLoop	        : FOR LEFTPAR expression? END expression? END expression? RIGHTPAR statement;
iteration       : FOR LEFTPAR ID? COLON type? RIGHTPAR statement;
whileLoop       : WHILE LEFTPAR expression? RIGHTPAR statement;
dowhile         : DO statement WHILE LEFTPAR expression? RIGHTPAR END;
ifstatement     : IF LEFTPAR expression? RIGHTPAR statement ( 'else' statement )?;
returnstatement : RETURN expression? END;

chanPriority : 'chan' 'priority' (chanExpr | 'default') ((COMMA | '<') (chanExpr | 'default'))* END;
chanExpr : ID
           | chanExpr LEFTBRACKET expression RIGHTBRACKET;


expression  : ID
            |  NAT
            |  expression LEFTBRACKET expression RIGHTBRACKET
            |  expression MARK
            |  LEFTPAR expression RIGHTPAR
            |  expression '++' | '++' expression
            |  expression '--' | '--' expression
            |  expression assign expression
            |  unary expression
            |  expression binary expression
            |  expression '?' expression COLON expression
            |  expression '.' ID
            |  expression LEFTPAR arguments RIGHTPAR
            |  'forall' LEFTPAR ID COLON type RIGHTPAR expression
            |  'exists' LEFTPAR ID COLON type RIGHTPAR expression
            |  'sum' LEFTPAR ID COLON type RIGHTPAR expression
            |  'deadlock' | 'true' | 'false';

arguments  : (expression ( COMMA expression )*)?;

assign     : '=' | ':=' | '+=' | '-=' | '*=' | '/=' | '%='
           | '|=' | '&=' | '^=' | '<<=' | '>>=';
unary      : '+' | '-' | '!' | 'not';
binary     : '<' | '<=' | '==' | '!=' | '>=' | '>'
           |  '+' | '-' | '*' | '/' | '%' | '&'
           |  '|' | '^' | '<<' | '>>' | '&&' | '||'
           |  '<?' | '>?' | 'or' | 'and' | 'imply';

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

NAT : [1-9]([0-9])*;
ID : [a-zA-Z_]([a-zA-Z0-9_])*;