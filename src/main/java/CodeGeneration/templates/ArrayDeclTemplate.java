package CodeGeneration.templates;

import CodeGeneration.templates.Template;

public class ArrayDeclTemplate implements Template {
    private final String resultingString;

    public ArrayDeclTemplate(Template expr) {
        resultingString = String.format("[%s]", expr.getOutput());
    }

    public ArrayDeclTemplate() {
        resultingString = "[]";
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
