package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ExponentialTemplate extends Template {
    public ExponentialTemplate(Template expr1, Template expr2) {
        this.template = new ST("<expr1> : <expr2>");
        this.template.add("expr1", expr1);
        this.template.add("expr2", expr2);
    }

    public ExponentialTemplate(Template expr1) {
        this.template = new ST("<expr1>");
        this.template.add("expr1", expr1);
    }
}
