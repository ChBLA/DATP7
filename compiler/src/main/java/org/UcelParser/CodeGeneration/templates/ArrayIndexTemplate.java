package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ArrayIndexTemplate extends Template {

    public ArrayIndexTemplate(Template leftExpr, Template arrayIndexExpr) {
        template = new ST("<leftExpr>[<arrayIndexExpr>]");
        template.add("leftExpr", leftExpr);
        template.add("arrayIndexExpr", arrayIndexExpr);
    }

}
