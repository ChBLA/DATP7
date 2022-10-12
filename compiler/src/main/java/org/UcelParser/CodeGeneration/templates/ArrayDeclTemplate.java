package org.UcelParser.CodeGeneration.templates;

public class ArrayDeclTemplate extends Template {

    public ArrayDeclTemplate(Template expr) {
        result = String.format("[%s]", expr);
    }

    public ArrayDeclTemplate() {
        result = "[]";
    }

}
