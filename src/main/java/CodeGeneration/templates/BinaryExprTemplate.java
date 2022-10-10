package CodeGeneration.templates;

import java.text.MessageFormat;

public class BinaryExprTemplate implements Template {
    private final String resultingString;
    public BinaryExprTemplate(String left, String right, String op) {
        resultingString = MessageFormat.format("{0} {1} {2}", left, op, right);
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
