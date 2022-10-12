package org.UcelParser.CodeGeneration.templates;

public class TypeIDScalarTemplate extends Template {

    public TypeIDScalarTemplate(Template expr) {
        result = String.format("scalar[%s]", expr);
    }


}
