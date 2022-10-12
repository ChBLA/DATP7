package org.UcelParser.CodeGeneration.templates;

public class AssignmentTemplate extends Template {
    public AssignmentTemplate(Template left, Template right) {
        result = String.format("%s = %s", left, right);
    }
}
