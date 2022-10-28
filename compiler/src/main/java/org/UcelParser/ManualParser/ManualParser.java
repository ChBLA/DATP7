package org.UcelParser.ManualParser;

import org.Ucel.IProject;
import org.UcelParser.UCELParser_Generated.UCELLexer;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;

public class ManualParser {

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

    //region Select

    //endregion

    //region Guard

    //endregion

    //region Sync

    //endregion

    //region Update

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


    //endregion

}
