package org.UcelParser;

import org.Ucel.*;
import org.UcelParser.CodeGeneration.CodeGenVisitor;
import org.UcelParser.CodeGeneration.ProjectCodeLinker;
import org.UcelParser.CodeGeneration.templates.ProjectTemplate;
import org.UcelParser.CodeGeneration.templates.Template;
import org.UcelParser.ManualParser.ManualParser;
import org.UcelParser.Util.Exception.ErrorsFoundException;
import org.UcelParser.Util.Logging.ILogger;
import org.UcelParser.Util.UniquePrefixGenerator;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import org.UcelParser.UCELParser_Generated.*;
import org.UcelParser.ReferenceHandler.ReferenceVisitor;
import org.UcelParser.TypeChecker.TypeCheckerVisitor;
import org.UcelParser.Util.Logging.Logger;
import org.antlr.v4.runtime.tree.ParseTree;


public class Compiler {
    private final ReferenceVisitor referenceVisitor;
    private final TypeCheckerVisitor typeCheckerVisitor;
    private final CodeGenVisitor codeGenVisitor;
    private final ILogger logger;

    public Compiler() {
        this(new Logger(false));
    }

    public Compiler(ILogger logger) {
        this.logger = logger;
        this.referenceVisitor = new ReferenceVisitor(logger);
        this.typeCheckerVisitor = new TypeCheckerVisitor(logger);
        this.codeGenVisitor = new CodeGenVisitor(logger);
    }

    public IProject compileProject(IProject project) {
        UniquePrefixGenerator.resetCounter();
        ManualParser manualParser = new ManualParser();
        ParserRuleContext tree = manualParser.parseProject(project);
        logger.setSource("");//TODO inject sources
        try {
            var refTree = runVisitor(referenceVisitor, tree, logger);
            var typeTree = runVisitor(typeCheckerVisitor, refTree, logger);
            var generatedCode = runVisitor(codeGenVisitor, typeTree, logger);
            var outputProject = new ProjectCodeLinker().generateUppaalProject((ProjectTemplate) generatedCode);
//            System.out.println(generatedCode);
            return outputProject;
        }
        catch (ErrorsFoundException e) {
            logger.printLogs();
            throw new RuntimeException(e);
        }

//        logger.printLogs();

//        return project;
//        return generateDummyProject();
    }

    private IProject generateDummyProject() {
        Project project = new Project();
        project.setDeclaration("// Declarations");

        org.Ucel.Template template = new org.Ucel.Template();
        template.setName("DummyTemplate");
        template.setDeclarations("// Template Declaration");
        Graph graph = new Graph();
        Location initNode = new Location();
        initNode.setInitial(true);
        Location otherNode = new Location();
        otherNode.setPosX(30);
        otherNode.setPosY(40);
        graph.addLocation(initNode);
        graph.addLocation(otherNode);
        Edge edge = new Edge();
        edge.setLocationStart(initNode);
        edge.setLocationEnd(otherNode);
        graph.addEdge(edge);
        template.setGraph(graph);
        project.putTemplate(template);

        project.setSystemDeclarations("// System Declarations");

        return project;
    }

    public String compile(String input) {
        UniquePrefixGenerator.resetCounter();
        CharStream charStream = CharStreams.fromString(input);
        UCELLexer lexer = new UCELLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        UCELParser parser = new UCELParser(tokenStream);

        this.logger.setSource(input);

        Template generatedCode = null;

        var tree = parser.start();

        try {
            var refTree = runVisitor(referenceVisitor, tree, logger);
            var typeTree = runVisitor(typeCheckerVisitor, refTree, logger);
            generatedCode = runVisitor(codeGenVisitor, typeTree, logger);
            System.out.println(generatedCode);
        } catch (ErrorsFoundException ignored) {
        }

        logger.printLogs();

        return generatedCode != null ? generatedCode.toString() : "";
    }

    private ParserRuleContext runVisitor(UCELBaseVisitor visitor, ParserRuleContext tree, ILogger logger) throws ErrorsFoundException{
        visitor.visit(tree);
        if (logger.hasErrors())
            throw new ErrorsFoundException(String.format("Found errors in visitor %s", visitor.getClass().toString()));
        return tree;
    }
    private Template runVisitor(CodeGenVisitor visitor, ParserRuleContext tree, ILogger logger) throws ErrorsFoundException{
        var output = visitor.visit(tree);
        if (logger.hasErrors())
            throw new ErrorsFoundException(String.format("Found errors in visitor %s", visitor.getClass().toString()));
        return output;
    }
}