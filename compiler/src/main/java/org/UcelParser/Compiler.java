package org.UcelParser;

import org.Ucel.*;
import org.UcelParser.CodeGeneration.CodeGenVisitor;
import org.UcelParser.CodeGeneration.ProjectCodeLinker;
import org.UcelParser.CodeGeneration.templates.ProjectTemplate;
import org.UcelParser.CodeGeneration.templates.Template;
import org.UcelParser.Interpreter.InterpreterVisitor;
import org.UcelParser.ManualParser.ManualParser;
import org.UcelParser.Util.Exception.ErrorsFoundException;
import org.UcelParser.Util.Logging.*;
import org.UcelParser.Util.UniquePrefixGenerator;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import org.UcelParser.UCELParser_Generated.*;
import org.UcelParser.ReferenceHandler.ReferenceVisitor;
import org.UcelParser.TypeChecker.TypeCheckerVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;


public class Compiler {
    private final ReferenceVisitor referenceVisitor;
    private final TypeCheckerVisitor typeCheckerVisitor;
    private final InterpreterVisitor interpreterVisitor;
    private final CodeGenVisitor codeGenVisitor;
    private final ILogger logger;
    public ArrayList<Log> getLogs() {
        return logger.getLogs();
    }

    public Compiler() {
        this(new Logger(false));
    }

    public Compiler(ILogger logger) {
        this.logger = logger;
        this.referenceVisitor = new ReferenceVisitor(logger);
        this.typeCheckerVisitor = new TypeCheckerVisitor(logger);
        this.codeGenVisitor = new CodeGenVisitor(logger);
        this.interpreterVisitor = new InterpreterVisitor(logger);
    }

    public IProject compileProject(IProject project) throws ErrorsFoundException {
        UniquePrefixGenerator.resetCounter();
        logger.setSource("");//TODO inject sources

        var tree = runVisitor(new ManualParser(logger), project, logger);
        var refTree = runVisitor(referenceVisitor, tree, logger);
        var typeTree = runVisitor(typeCheckerVisitor, refTree, logger);
        var interpreterTree = runVisitor(interpreterVisitor, typeTree, logger);
        var generatedCode = runVisitor(codeGenVisitor, interpreterTree, logger);
        var outputProject = new ProjectCodeLinker().generateUppaalProject((ProjectTemplate) generatedCode);
        return outputProject;
    }

    public void benchmarkProject(IProject project) throws ErrorsFoundException {
        UniquePrefixGenerator.resetCounter();
        logger.setSource("");//TODO inject sources

        long divider = 1000;
        int testCount = 10;
        int warmUpCount = 3;

        ArrayList<double[]> timesForAll = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            timesForAll.add(new double[testCount]);
        }

        ArrayList<Long> times = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            if (i < warmUpCount) {
                var tree = runVisitor(new ManualParser(logger), project, logger);
                var refTree = runVisitor(referenceVisitor, tree, logger);
                var typeTree = runVisitor(typeCheckerVisitor, refTree, logger);
                var interpreterTree = runVisitor(interpreterVisitor, typeTree, logger);
                var generatedCode = runVisitor(codeGenVisitor, interpreterTree, logger);
                var outputProject = new ProjectCodeLinker().generateUppaalProject((ProjectTemplate) generatedCode);
            } else {
                var startTime = System.nanoTime();
                var tree = runVisitor(new ManualParser(logger), project, logger);
                timesForAll.get(0)[i - warmUpCount] = (double) (System.nanoTime() - startTime) / divider;
                startTime = System.nanoTime();
                var refTree = runVisitor(referenceVisitor, tree, logger);
                timesForAll.get(1)[i - warmUpCount] = (double) (System.nanoTime() - startTime) / divider;
                startTime = System.nanoTime();
                var typeTree = runVisitor(typeCheckerVisitor, refTree, logger);
                timesForAll.get(2)[i - warmUpCount] = (double) (System.nanoTime() - startTime) / divider;
                startTime = System.nanoTime();
                var interpreterTree = runVisitor(interpreterVisitor, typeTree, logger);
                timesForAll.get(3)[i - warmUpCount] = (double) (System.nanoTime() - startTime) / divider;
                startTime = System.nanoTime();
                var generatedCode = runVisitor(codeGenVisitor, interpreterTree, logger);
                timesForAll.get(4)[i - warmUpCount] = (double) (System.nanoTime() - startTime) / divider;
                startTime = System.nanoTime();
                var outputProject = new ProjectCodeLinker().generateUppaalProject((ProjectTemplate) generatedCode);
                timesForAll.get(5)[i - warmUpCount] = (double) (System.nanoTime() - startTime) / divider;
            }
        }

        logger.log(new BenchMarkLog(timesForAll));

    }

    public void checkProject(IProject project) throws ErrorsFoundException {
        UniquePrefixGenerator.resetCounter();
        logger.setSource("");//TODO inject sources

        var tree = runVisitor(new ManualParser(logger), project, logger);
        var refTree = runVisitor(referenceVisitor, tree, logger);
        var typeTree = runVisitor(typeCheckerVisitor, refTree, logger);
        var interpreterTree = runVisitor(interpreterVisitor, typeTree, logger);
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

    private ParserRuleContext runVisitor(ManualParser visitor, IProject project, ILogger logger) throws ErrorsFoundException{
        ParserRuleContext tree = visitor.parseProject(project);

        if (logger.hasErrors())
            throw new ErrorsFoundException("Found errors in visitor ManualParser", logger.getLogs());

        return tree;
    }
    private ParserRuleContext runVisitor(UCELBaseVisitor visitor, ParserRuleContext tree, ILogger logger) throws ErrorsFoundException{
        visitor.visit(tree);
        if (logger.hasErrors())
            throw new ErrorsFoundException(String.format("Found errors in visitor %s", visitor.getClass().toString()), logger.getLogs());
        return tree;
    }
    private Template runVisitor(CodeGenVisitor visitor, ParserRuleContext tree, ILogger logger) throws ErrorsFoundException{
        var output = visitor.visit(tree);
        if (logger.hasErrors())
            throw new ErrorsFoundException(String.format("Found errors in visitor %s", visitor.getClass().toString()), logger.getLogs());
        return output;
    }
}