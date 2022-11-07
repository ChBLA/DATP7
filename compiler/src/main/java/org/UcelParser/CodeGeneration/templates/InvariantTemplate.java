package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class InvariantTemplate extends Template {
    public InvariantTemplate(Template expr) {
        this.template = expr.template;
    }
}

