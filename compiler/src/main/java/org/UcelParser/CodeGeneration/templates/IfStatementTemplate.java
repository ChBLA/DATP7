package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class IfStatementTemplate extends Template{
    public IfStatementTemplate(Template expr, Template stmnt1, Template stmnt2) {
//        result = String.format("if (%s) %s else %s", expr, stmnt1, stmnt2);
        template = new ST("if (<expr>) <stmnt1> else <stmnt2>");
        template.add("expr", expr);
        template.add("stmnt1", stmnt1);
        template.add("stmnt2", stmnt2);
    }

    public IfStatementTemplate(Template expr, Template stmnt1) {
//        result = String.format("if (%s) %s", expr, stmnt1);
        template = new ST("if (<expr>) <stmnt1>");
        template.add("expr", expr);
        template.add("stmnt1", stmnt1);
    }

}
