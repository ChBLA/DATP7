package org.UcelParser.CodeGeneration.templates;

public class WhileLoopTemplate extends Template{

    public WhileLoopTemplate(Template expr, Template stmnt) {
        result = String.format("while (%s) %s", expr, stmnt);
    }


}
