package org.UcelParser.ManualParser;

import org.Ucel.IEdge;
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


    //endregion

    //region Project template

    //region Graph


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
