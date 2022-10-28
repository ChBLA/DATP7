package org.UcelParser.ManualParser;

import org.Ucel.IEdge;
import org.Ucel.IGraph;
import org.Ucel.ILocation;
import org.Ucel.IProject;
import org.UcelParser.UCELParser_Generated.UCELLexer;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

public class ManualParser {

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
        return null;
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
        return null;
    }

    //region Select

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
        public UCELParser.SystemContext parseProjectSystem(UCELParser.ProjectContext parent, String system) {
        var systemParser = generateParser(system);
        var systemNode = systemParser.system();
        systemNode.parent = parent;
        return systemParser.getNumberOfSyntaxErrors() == 0 && isEOF(systemParser) ? systemNode : null;
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

    //endregion

}
