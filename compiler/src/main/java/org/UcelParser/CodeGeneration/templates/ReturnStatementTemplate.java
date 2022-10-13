package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ReturnStatementTemplate extends Template{

    public ReturnStatementTemplate(Template expr) {
//        result = String.format("return %s;", expr);
        template = new ST("return <expr>;");
        template.add("expr", expr);
    }

    public ReturnStatementTemplate() {
//        result = "return;";
        template = new ST("return;");
    }

}
