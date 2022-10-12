package org.UcelParser.CodeGeneration.templates;

public class ConditionalExpressionTemplate implements Template {
    private final String resultingString;

    public ConditionalExpressionTemplate(Template condition, Template positiveResult, Template negativeResult) {
        resultingString = String.format("%s ? %s : %s", condition, positiveResult, negativeResult);
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
