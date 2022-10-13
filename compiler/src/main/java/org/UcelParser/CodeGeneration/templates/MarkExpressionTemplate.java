package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class MarkExpressionTemplate extends Template{

    public MarkExpressionTemplate(Template expr) {
//        result = String.format("%s'", expr);
        template = new ST("<expr>'");
        template.add("expr", expr);
    }

}
