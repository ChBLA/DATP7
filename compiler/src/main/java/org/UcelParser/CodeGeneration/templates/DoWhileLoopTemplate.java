package org.UcelParser.CodeGeneration.templates;

public class DoWhileLoopTemplate extends Template {

    public DoWhileLoopTemplate(Template expr, Template stmnt) {
        result = String.format("do %s while (%s);", stmnt, expr);
    }

}
