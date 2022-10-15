package org.UcelParser;

import org.UcelParser.CodeGeneration.CodeGenVisitor;
import org.UcelParser.CodeGeneration.templates.Template;
import org.UcelParser.Util.Exception.ErrorsFoundException;
import org.antlr.runtime.tree.ParseTree;
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

        String input = "{\n" +
                        "int i; \n" +
                        "for (i = 0; i < 10; i++) { \n " +
                          "bool b = true; \n " +
                          "bool a = true > 12 && true || b;\n" +
                       "}}";
        new Main(input);
    }


    public Main(String input) {

        CharStream charStream = CharStreams.fromString(input);
        UCELLexer lexer = new UCELLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        UCELParser parser = new UCELParser(tokenStream);

        Logger logger = new Logger(input);
        var referenceVisitor = new ReferenceVisitor(logger);
        var typeCheckerVisitor = new TypeCheckerVisitor(logger);
        var codeGenerationVisitor = new CodeGenVisitor(logger);

        UCELParser.BlockContext block = parser.block();

        try {
            var refTree = runVisitor(referenceVisitor, block, logger);
            var typeTree = runVisitor(typeCheckerVisitor, refTree, logger);
            var generatedCode = runVisitor(codeGenerationVisitor, typeTree, logger);
            System.out.println(generatedCode);
        } catch (ErrorsFoundException ignored) {
        }

        logger.printLogs();
    }

    private ParserRuleContext runVisitor(UCELBaseVisitor visitor, ParserRuleContext tree, Logger logger) throws ErrorsFoundException{
        visitor.visit(tree);
        if (logger.hasErrors())
            throw new ErrorsFoundException(String.format("Found errors in visitor %s", visitor.getClass().toString()));
        return tree;
    }
    private Template runVisitor(CodeGenVisitor visitor, ParserRuleContext tree, Logger logger) throws ErrorsFoundException{
        var output = visitor.visit(tree);
        if (logger.hasErrors())
            throw new ErrorsFoundException(String.format("Found errors in visitor %s", visitor.getClass().toString()));
        return output;
    }
}