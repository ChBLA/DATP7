package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ImplicationQueryTemplate extends Template {
    public ImplicationQueryTemplate(Template left, Template right) {
        template = new ST("<left> --> <right>");
        template.add("left", left);
        template.add("right", right);
    }
}
