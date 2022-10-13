package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class BlockTemplate extends Template {

    public BlockTemplate(List<Template> localDecls, List<Template> statements) {
//        var builder = new StringBuilder();
//        builder.append("{\n");
//        for (var decl : localDecls) {
//            builder.append(String.format("%s\n", decl));
//        }
//        for (var stmnt : statements) {
//            builder.append(String.format("%s", stmnt));
//        }
//        builder.append("}");
//        result = builder.toString();

        ST template = new ST("{\n<localDecls; separator=\"\n\">\n<statements; separator=\"\">}");
        template.add("localDecls", localDecls);
        template.add("statements", statements);

        this.template = template;

    }
}
