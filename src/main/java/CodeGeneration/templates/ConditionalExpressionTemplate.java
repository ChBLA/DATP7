package CodeGeneration.templates;

import CodeGeneration.templates.Template;

public class ConditionalExpressionTemplate extends Template {
    public ConditionalExpressionTemplate(Template condition, Template positiveResult, Template negativeResult) {
        result = String.format("%s ? %s : %s", condition, positiveResult, negativeResult);
    }
}
