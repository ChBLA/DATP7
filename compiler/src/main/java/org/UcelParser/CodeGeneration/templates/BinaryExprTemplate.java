package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class BinaryExprTemplate extends Template {
    public BinaryExprTemplate(Template left, Template right, String op) {
        template = new ST("<left> <op> <right>");
        template.add("left", left);
        template.add("right", right);
        template.add("op", op);
    }
}
