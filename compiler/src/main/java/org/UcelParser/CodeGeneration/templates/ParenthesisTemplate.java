package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ParenthesisTemplate extends Template{

    public ParenthesisTemplate(Template expr) {
        template = new ST("(<expr>)");
        template.add("expr", expr);
    }

}
