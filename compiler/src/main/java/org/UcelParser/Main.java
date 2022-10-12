package org.UcelParser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import org.UcelParser.UCELParser_Generated.*;
import org.UcelParser.Util.*;
import org.UcelParser.ReferenceHandler.ReferenceVisitor;
import org.UcelParser.TypeChecker.TypeCheckerVisitor;
import org.UcelParser.Util.Logging.Logger;


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

        UCELParser.BlockContext block = parser.block();

        referenceVisitor.visit(block);
        Type type = typeCheckerVisitor.visit(block);

        System.out.println(type);
        logger.printLogs();
    }
}