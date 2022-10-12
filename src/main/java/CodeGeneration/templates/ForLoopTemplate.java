package CodeGeneration.templates;

import CodeGeneration.templates.Template;

public class ForLoopTemplate extends Template {

    public ForLoopTemplate(Template assign, Template expr1, Template expr2, Template stmnt) {
        result = String.format("for (%s;%s;%s) %s", assign, expr1, expr2, stmnt);
    }

}
