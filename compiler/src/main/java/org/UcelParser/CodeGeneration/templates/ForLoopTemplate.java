package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ForLoopTemplate extends Template {

    public ForLoopTemplate(Template assign, Template expr1, Template expr2, Template stmnt) {
//        result = String.format("for (%s;%s;%s) %s", assign, expr1, expr2, stmnt);
        template = new ST("for (<assign>;<expr1>;<expr2>) <stmnt>");
        template.add("assign", assign);
        template.add("expr1", expr1);
        template.add("expr2", expr2);
        template.add("stmnt", stmnt);
    }

}
