package org.UcelParser.CodeGeneration.templates;

public class MarkExpressionTemplate implements Template{
    private final String resultingString;

    public MarkExpressionTemplate(Template expr) {
        resultingString = String.format("%s'", expr.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
