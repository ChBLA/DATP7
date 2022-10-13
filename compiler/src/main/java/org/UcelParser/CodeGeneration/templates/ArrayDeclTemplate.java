package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ArrayDeclTemplate extends Template {

    public ArrayDeclTemplate(Template expr) {
        template = new ST("[<expr>]");
        template.add("expr", expr);
    }

    public ArrayDeclTemplate() {
        template = new ST("[]");
    }

}
