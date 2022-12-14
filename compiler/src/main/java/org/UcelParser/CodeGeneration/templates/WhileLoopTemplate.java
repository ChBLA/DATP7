package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class WhileLoopTemplate extends Template{

    public WhileLoopTemplate(Template expr, Template stmnt) {
        template = new ST("while (<expr>) <stmnt>");
        template.add("expr", expr);
        template.add("stmnt", stmnt);
    }
}
