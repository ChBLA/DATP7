
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import UCELParser_Generated.*;
import Util.*;
import Util.Logging.*;
import ReferenceHandler.ReferenceVisitor;
import TypeChecker.TypeCheckerVisitor;


public class Main {
    public static void main(String[] args) {
        //UCELBaseListener a = new UCELBaseListener();

        String input = "{ \n " +
                          "bool b = true; \n " +
                          "bool a = true > 12 && true || b;\n" +
                       "}";
        new Main(input);
    }


    public Main(String input) {

        CharStream charStream = CharStreams.fromString(input);
        UCELLexer lexer = new UCELLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        UCELParser parser = new UCELParser(tokenStream);

        Logger logger = new Logger(input);
        ReferenceVisitor referenceVisitor = new ReferenceVisitor(logger);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(logger);

        UCELParser.DeclarationsContext declarations = parser.declarations();

        referenceVisitor.visit(declarations);
        Type type = typeCheckerVisitor.visit(declarations);

        System.out.println(type);
        logger.printLogs();
    }
}