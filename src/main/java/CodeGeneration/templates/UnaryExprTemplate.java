package CodeGeneration.templates;

import java.text.MessageFormat;

public class UnaryExprTemplate extends Template {

    public UnaryExprTemplate(Template left, Template right) {
        result = String.format("%s%s", left, right);
    }

}
