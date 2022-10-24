package org.UcelParser;

import org.Ucel.IProject;
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


public class Compiler {
    private final ReferenceVisitor referenceVisitor;
    private final TypeCheckerVisitor typeCheckerVisitor;
    private final CodeGenVisitor codeGenVisitor;
    private final Logger logger;

    public Compiler() {
        this(new Logger());
    }

    public Compiler(Logger logger) {
        this.logger = logger;
        this.referenceVisitor = new ReferenceVisitor(logger);
        this.typeCheckerVisitor = new TypeCheckerVisitor(logger);
        this.codeGenVisitor = new CodeGenVisitor(logger);
    }

    public String compile(String input) {
        CharStream charStream = CharStreams.fromString(input);
        UCELLexer lexer = new UCELLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        UCELParser parser = new UCELParser(tokenStream);

        this.logger.setSource(input);

        Template generatedCode = null;

        UCELParser.BlockContext block = parser.block();

        try {
            var refTree = runVisitor(referenceVisitor, block, logger);
            var typeTree = runVisitor(typeCheckerVisitor, refTree, logger);
            generatedCode = runVisitor(codeGenVisitor, typeTree, logger);
            System.out.println(generatedCode);
        } catch (ErrorsFoundException ignored) {
        }

        logger.printLogs();

        return generatedCode != null ? generatedCode.toString() : "";
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