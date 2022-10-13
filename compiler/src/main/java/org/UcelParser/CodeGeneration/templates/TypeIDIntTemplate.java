package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class TypeIDIntTemplate extends Template {

    public TypeIDIntTemplate(Template expr1, Template expr2) {
        template = new ST("int[<expr1>,<expr2>]");
        template.add("expr1", expr1);
        template.add("expr2", expr2);
    }


}
