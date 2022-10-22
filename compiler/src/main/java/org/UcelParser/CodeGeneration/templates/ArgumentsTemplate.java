package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class ArgumentsTemplate extends Template {
    public ArgumentsTemplate(List<Template> exprs) {
        template = new ST("<exprs; separator=\", \">");
        template.add("exprs", exprs);
    }
}
