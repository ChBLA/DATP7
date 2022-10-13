package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class TypeIDScalarTemplate extends Template {

    public TypeIDScalarTemplate(Template expr) {
        template = new ST("scalar[<expr>]");
        template.add("expr", expr);
    }


}
