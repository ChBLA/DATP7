package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class AssignmentTemplate extends Template {
    public AssignmentTemplate(Template left, Template op, Template right) {
        template = new ST("<left> <op> <right>");
        template.add("left", left);
        template.add("right", right);
        template.add("op", op);
    }
}
