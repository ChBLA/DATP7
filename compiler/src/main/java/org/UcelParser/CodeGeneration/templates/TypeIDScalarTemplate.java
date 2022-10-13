package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class TypeIDScalarTemplate extends Template {

    public TypeIDScalarTemplate(Template expr) {
//        result = String.format("scalar[%s]", expr);
        template = new ST("scalar[<expr>]");
        template.add("expr", expr);
    }


}
