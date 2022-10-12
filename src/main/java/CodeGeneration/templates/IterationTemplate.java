package CodeGeneration.templates;

import CodeGeneration.templates.Template;

public class IterationTemplate extends Template {

    public IterationTemplate(Template id, Template typeResult, Template stmntResult) {
        result = String.format("for (%s:%s) %s", id, typeResult, stmntResult);
    }

}
