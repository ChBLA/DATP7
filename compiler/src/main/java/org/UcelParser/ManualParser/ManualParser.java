package org.UcelParser.ManualParser;

import org.Ucel.IEdge;
import org.Ucel.IGraph;
import org.Ucel.ILocation;
import org.Ucel.IProject;
import org.UcelParser.UCELParser_Generated.UCELLexer;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;

public class ManualParser {
    private static int counter = 0;

    public ManualParser() {

    }

    //region Project
//    public ParseTree parseProject(IProject project) {
//        var node = new UCELParser.ProjectContext(null, -1);
//
//        var children = new ArrayList<ParseTree>();
//
//        children.add(parseProjectDeclaration(node, project.getDeclaration()));
//        for (var template : project.getTemplates()) {
//            children.add(parseProjectTemplate(node, template));
//        }
//        children.add(parseProjectSystem(node, project.getSystemDeclarations()));
//
//        node.children = children;
//
//        return node;
//    }

    //region Project declarations
    public UCELParser.PdeclarationContext parseProjectDeclaration(UCELParser.ProjectContext parent, String declaration) {
        var declParser = generateParser(declaration);
        var declNode = declParser.pdeclaration();
        declNode.parent = parent;
        return declParser.getNumberOfSyntaxErrors() == 0 && isEOF(declParser) ? declNode : null;
    }

    //endregion

    //region Project template

    //region Graph

    public UCELParser.GraphContext parserGraph(ParserRuleContext parent, IGraph graph) {
        UCELParser.GraphContext graphCtx = new UCELParser.GraphContext(parent, -1);
        boolean foundNull = false;
        for(ILocation l : graph.getLocations()) {
            ParserRuleContext location = parseLocation(graphCtx, l);
            foundNull = foundNull || location == null;
            graphCtx.addChild(location);
        }

        for(IEdge e : graph.getEdges()) {
            ParserRuleContext edge = parseEdge(graphCtx, e);
            foundNull = foundNull || edge == null;
            graphCtx.addChild(edge);
        }

        return foundNull ? null : graphCtx;
    }

    //region Locations
    public ParserRuleContext parseLocation(ParserRuleContext parent, ILocation location) {
        var locationNode = new UCELParser.LocationContext(parent, -1);

        var invariantNode = parseInvariant(locationNode, location.getInvariant());
        var exponentialNode = parseExponential(locationNode, location.getRateOfExponential());

        if (invariantNode == null || exponentialNode == null)
            return null;

        locationNode.children = new ArrayList<>() {{ add(invariantNode); add(exponentialNode); }};

        if (!location.getName().isEmpty())
            locationNode.addChild(new CommonToken(UCELParser.ID, location.getName()));

        locationNode.isCommitted = location.getCommitted();
        locationNode.isInitial = location.getInitial();
        locationNode.isUrgent = location.getUrgent();
        locationNode.posX = location.getPosX();
        locationNode.posY = location.getPosY();
        locationNode.comments = location.getComments();
        locationNode.testCodeEnter = location.getTestCodeOnEnter();
        locationNode.testCodeExit = location.getTestCodeOnExit();

        locationNode.id = getNextID();

        return locationNode;
    }

    //region Invariant
    public ParserRuleContext parseInvariant(ParserRuleContext parent, String input) {
        var parser = generateParser(input);
        var node = parser.invariant();
        node.parent = parent;
        return parser.getNumberOfSyntaxErrors() > 0 ? null : node;
    }
    //endregion

    //region Exponential
    public ParserRuleContext parseExponential(ParserRuleContext parent, String input) {
        var parser = generateParser(input);
        var node = parser.exponential();
        node.parent = parent;
        return parser.getNumberOfSyntaxErrors() > 0 ? null : node;
    }
    //endregion
    //endregion

    //region Edges

    public UCELParser.EdgeContext parseEdge(ParserRuleContext parent, IEdge edge) {
        var edgeCtx = new UCELParser.EdgeContext(parent, 1);
        edgeCtx.parent = parent;

        edgeCtx.locationStartID = Integer.parseInt(edge.getLocationStart().getName());
        edgeCtx.locationEndID = Integer.parseInt(edge.getLocationEnd().getName());
        edgeCtx.comments = edge.getComment();
        edgeCtx.testCode = edge.getTestCode();

        var select = parseSelect(edgeCtx, edge.getSelect());
        var guard = parseGuard(edgeCtx, edge.getGuard());
        var sync = parseSync(edgeCtx, edge.getSync());
        var update = parseUpdate(edgeCtx, edge.getUpdate());

        edgeCtx.children = new ArrayList<>();
        edgeCtx.children.add(select);
        edgeCtx.children.add(guard);
        edgeCtx.children.add(sync);
        edgeCtx.children.add(update);

        return edgeCtx.children.contains(null) ? null : edgeCtx;
    }

    //region Select

    public ParserRuleContext parseSelect(ParserRuleContext parent, String input) {
        var parser = generateParser(input);
        var tree = parser.select();
        tree.parent = parent;
        return (parser.getNumberOfSyntaxErrors() == 0 && isEOF(parser)) ? tree : null;
    }

    //endregion

    //region Guard
    public ParserRuleContext parseGuard(ParserRuleContext parent, String input) {
        var parser = generateParser(input);
        var tree = parser.guard();
        tree.setParent(parent);
        return (parser.getNumberOfSyntaxErrors() == 0 && isEOF(parser)) ? tree : null;
    }
    //endregion

    //region Sync
    public ParserRuleContext parseSync(ParserRuleContext parent, String input) {
        var parser = generateParser(input);
        var tree = parser.sync();
        tree.setParent(parent);
        return (parser.getNumberOfSyntaxErrors() == 0 && isEOF(parser)) ? tree : null;
    }
    //endregion

    //region Update

    public UCELParser.UpdateContext parseUpdate(ParserRuleContext parent, String update) {
        UCELParser parser = generateParser(update);
        UCELParser.UpdateContext updateNode = parser.update();
        updateNode.parent = parent;
        return parser.getNumberOfSyntaxErrors() == 0 && isEOF(parser) ? updateNode : null;
    }

    //endregion
    //endregion
    //endregion
    //endregion

    //region Project system
        public UCELParser.PsystemContext parseProjectSystem(UCELParser.ProjectContext parent, String psystem) {
        var Parser = generateParser(psystem);
        var psystemNode = Parser.psystem();
        psystemNode.parent = parent;
        return Parser.getNumberOfSyntaxErrors() == 0 && isEOF(Parser) ? psystemNode : null;
        }
    //endregion
    //endregion

    //region Helper functions
    private UCELParser generateParser(String input) {
        CharStream charStream = CharStreams.fromString(input);
        UCELLexer lexer = new UCELLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        return new UCELParser(tokenStream);
    }

    private Boolean isEOF(UCELParser parser) {
        return parser.getCurrentToken().getType() == UCELParser.EOF;
    }

    private int getNextID() {
        return counter++;
    }

    //endregion

}
