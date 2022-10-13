package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class UnaryExprTemplate extends Template {

    public UnaryExprTemplate(Template left, Template right) {
//        result = String.format("%s%s", left, right);
        template = new ST("<left><right>");
        template.add("left", left);
        template.add("right", right);
    }


}
