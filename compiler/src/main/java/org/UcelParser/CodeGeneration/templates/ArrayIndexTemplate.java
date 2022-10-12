package org.UcelParser.CodeGeneration.templates;

public class ArrayIndexTemplate extends Template {

    public ArrayIndexTemplate(Template leftExpr, Template arrayIndexExpr) {
        result = String.format("%s[%s]", leftExpr, arrayIndexExpr);
    }

}
