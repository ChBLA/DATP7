package CodeGeneration.templates;

import CodeGeneration.templates.Template;

public class IterationTemplate implements Template {
    private final String resultingString;

    public IterationTemplate(Template id, Template typeResult, Template stmntResult) {
        resultingString = String.format("for (%s:%s) %s", id.getOutput(), typeResult.getOutput(), stmntResult.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
