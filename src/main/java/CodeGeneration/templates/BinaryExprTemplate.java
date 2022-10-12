package CodeGeneration.templates;

import java.text.MessageFormat;

public class BinaryExprTemplate extends Template {
    public BinaryExprTemplate(Template left, Template right, String op) {
        result = String.format("%s %s %s", left, op, right);
    }
}
