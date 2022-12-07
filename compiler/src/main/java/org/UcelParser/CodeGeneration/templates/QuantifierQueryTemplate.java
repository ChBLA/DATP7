package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class QuantifierQueryTemplate extends Template {
    public QuantifierQueryTemplate(String op, Template expr) {
        template = new ST("<op> <expr>");
        template.add("op", op);
        template.add("expr", expr);
    }
}
