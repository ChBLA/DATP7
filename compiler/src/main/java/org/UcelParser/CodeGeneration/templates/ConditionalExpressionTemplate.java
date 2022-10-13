package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ConditionalExpressionTemplate extends Template {
    public ConditionalExpressionTemplate(Template condition, Template positiveResult, Template negativeResult) {
        template = new ST("<condition> ? <positiveResult> : <negativeResult>");
        template.add("condition", condition);
        template.add("positiveResult", positiveResult);
        template.add("negativeResult", negativeResult);
    }
}
