package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class BlockTemplate extends Template {

    public BlockTemplate(List<Template> localDecls, List<Template> statements) {
        template = new ST("{\n<localDecls; separator=\"\n\">\n<statements; separator=\"\">}");
        template.add("localDecls", localDecls);
        template.add("statements", statements);
    }
}
