package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ArrayDeclTemplate extends Template {

    public ArrayDeclTemplate(Template expr) {
//        result = String.format("[%s]", expr);
        this.template = new ST("[<expr>]");
        this.template.add("expr", expr);
    }

    public ArrayDeclTemplate() {
//        result = "[]";
        template = new ST("[]");
    }

}
