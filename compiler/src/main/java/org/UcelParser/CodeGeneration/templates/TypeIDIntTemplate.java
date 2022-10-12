package org.UcelParser.CodeGeneration.templates;

public class TypeIDIntTemplate extends Template {

    public TypeIDIntTemplate(Template expr1, Template expr2) {
        result = String.format("int[%s,%s]", expr1, expr2);
    }


}
