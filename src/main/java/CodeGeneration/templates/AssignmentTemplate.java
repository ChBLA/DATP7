package CodeGeneration.templates;

import CodeGeneration.templates.Template;

import java.text.MessageFormat;

public class AssignmentTemplate extends Template {
    public AssignmentTemplate(Template left, Template right) {
        result = String.format("%s = %s", left, right);
    }
}
