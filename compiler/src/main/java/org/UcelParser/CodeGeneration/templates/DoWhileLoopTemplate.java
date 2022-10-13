package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class DoWhileLoopTemplate extends Template {

    public DoWhileLoopTemplate(Template expr, Template stmnt) {
//        result = String.format("do %s while (%s);", stmnt, expr);
        template = new ST("do <stmnt> while (<expr>);");
        template.add("expr", expr);
        template.add("stmnt", stmnt);
    }

}
