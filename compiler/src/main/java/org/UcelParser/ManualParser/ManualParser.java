package org.UcelParser.ManualParser;

import org.Ucel.*;
import org.UcelParser.UCELParser_Generated.UCELLexer;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Logging.ErrorLog;
import org.UcelParser.Util.Logging.ILogger;
import org.UcelParser.Util.Logging.Logger;
import org.UcelParser.Util.Logging.SyntaxErrorLog;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualParser {
    private static int counter = 0;
    private ILogger logger;
    private ErrorListener errorListener;

    public ManualParser(ILogger logger) {
        this.logger = logger;
        this.errorListener = new ErrorListener(logger);
    }

    public ManualParser() {
        this(new Logger());
    }

    //region Project
    //TODO: Add template back in when done
    public ParserRuleContext parseProject(IProject project) {
        var node = new UCELParser.ProjectContext(null, -1);

        var children = new ArrayList<ParseTree>();

        children.add(parseProjectDeclaration(node, project.getDeclaration()));


        for (var template : project.getTemplates()) {
            children.add(parseTemplate(node, template));
        }

        children.add(parseProjectSystem(node, project.getSystemDeclarations()));

        // Verification Queries
        children.add(parseQueries(node, project.getVerificationQueries()));

        node.children = children;

        return node;
    }
    //endregion

    //region Project declarations
    public UCELParser.PdeclarationContext parseProjectDeclaration(UCELParser.ProjectContext parent, String declaration) {
        var declParser = generateParser(declaration);
        var declNode = declParser.pdeclaration();
        declNode.parent = parent;
        return declParser.getNumberOfSyntaxErrors() == 0 && isEOF(declParser) ? declNode : null;
    }

    //endregion

    //region Project template

    public UCELParser.PtemplateContext parseTemplate(ParserRuleContext parent, ITemplate template) {
        if(template.getName() == null || template.getName().isEmpty()) {
            return null;
        }

        var node = new UCELParser.PtemplateContext(parent, -1);

        var parametersParser = generateParser(template.getParameters());
        var declarationsParser = generateParser(template.getDeclarations());

        var parameters = parametersParser.parameters();
        parameters = parametersParser.getNumberOfSyntaxErrors() == 0 && isEOF(parametersParser) ? parameters : null;

        var ID = new CommonToken(UCELLexer.ID, template.getName());
        var graph = parseGraph(node, template.getGraph());

        var declarations = declarationsParser.declarations();
        declarations = declarationsParser.getNumberOfSyntaxErrors() == 0 && isEOF(declarationsParser) ? declarations : null;

        node.children = new ArrayList<>();
        node.addChild(ID);
        node.children.add(parameters);
        node.children.add(graph);
        node.children.add(declarations);

        node.parent = parent;

        return node.children.contains(null) ? null : node;
    }

    //endregion

    //region Graph

    public UCELParser.GraphContext parseGraph(ParserRuleContext parent, IGraph graph) {
        Map<ILocation, Integer> locationMap = new HashMap<>();
        UCELParser.GraphContext graphCtx = new UCELParser.GraphContext(parent, -1);
        boolean foundNull = false;
        for(ILocation l : graph.getLocations()) {
            ParserRuleContext location = parseLocation(graphCtx, l);
            foundNull = foundNull || location == null;
            graphCtx.addChild(location);
            if (location != null)
                locationMap.put(l, ((UCELParser.LocationContext) location).id);
        }

        for(IEdge e : graph.getEdges()) {
            ParserRuleContext edge = parseEdge(graphCtx, e, locationMap);
            if(edge == null)
                logger.log(new ErrorLog(graphCtx, "Invalid edge: " + e));
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
        locationNode.addChild(new CommonToken(UCELParser.ID, location.getName() != null ? location.getName() : ""));

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
        return parser.getNumberOfSyntaxErrors() == 0 && isEOF(parser) ? node : null;
    }
    //endregion

    //region Exponential
    public ParserRuleContext parseExponential(ParserRuleContext parent, String input) {
        var parser = generateParser(input);
        var node = parser.exponential();
        node.parent = parent;
        return parser.getNumberOfSyntaxErrors() == 0 && isEOF(parser) ? node : null;
    }
    //endregion
    //endregion

    //region Edges

    public UCELParser.EdgeContext parseEdge(ParserRuleContext parent, IEdge edge, Map<ILocation, Integer> locationMap) {
        var edgeCtx = new UCELParser.EdgeContext(parent, 1);
        edgeCtx.parent = parent;

        edgeCtx.locationStartID = locationMap.get(edge.getLocationStart());
        edgeCtx.locationEndID = locationMap.get(edge.getLocationEnd());
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

        if(!isEOF(Parser))
            logger.log(new ErrorLog(null, "Didn't match end of file"));

        return Parser.getNumberOfSyntaxErrors() == 0 && isEOF(Parser) ? psystemNode : null;
    }
    //endregion
    //endregion

    //region Verification Queries
    public UCELParser.VerificationListContext parseQueries(UCELParser.ProjectContext projectContext, List<IVerificationQuery> queries) {
        var verificationlist = new UCELParser.VerificationListContext(projectContext, -1);
        for(var query: queries) {
            var pQueryNode = parseQuery(query);
            verificationlist.addChild(pQueryNode);
        }
        return verificationlist;
    }
    public UCELParser.PQueryContext parseQuery(IVerificationQuery query) {
        var parser = generateParser(query.getFormula());
        var pQueryNode = parser.pQuery();
        pQueryNode.comment = query.getComment();

        if(!isEOF(parser))
            logger.log(new ErrorLog(null, "Couldn't match entire verification formula"));

        return pQueryNode;
    }

    //endregion

    //region Helper functions
    private UCELParser generateParser(String input) {
        CharStream charStream = CharStreams.fromString(input);
        UCELLexer lexer = new UCELLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        var parser = new UCELParser(tokenStream);
        if (errorListener != null) {
            parser.addErrorListener(ConsoleErrorListener.INSTANCE);
            parser.addErrorListener(errorListener);
        }
        return parser;
    }

    private Boolean isEOF(UCELParser parser) {
        return parser.getCurrentToken().getType() == UCELParser.EOF;
    }

    private int getNextID() {
        return counter++;
    }

    //endregion

}
