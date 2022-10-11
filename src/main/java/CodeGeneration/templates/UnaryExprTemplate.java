package CodeGeneration.templates;

import java.text.MessageFormat;

public class UnaryExprTemplate implements Template {
    private final String resultingString;

    public UnaryExprTemplate(Template left, Template right) {
        resultingString = String.format("%s%s", left.getOutput(), right.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
