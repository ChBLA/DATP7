package CodeGeneration.templates;

import CodeGeneration.templates.Template;

public class DoWhileLoopTemplate extends Template {

    public DoWhileLoopTemplate(Template expr, Template stmnt) {
        result = String.format("do %s while (%s);", stmnt, expr);
    }

}
