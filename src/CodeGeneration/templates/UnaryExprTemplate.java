import java.text.MessageFormat;

public class UnaryExprTemplate implements Template {
    private final String resultingString;

    public UnaryExprTemplate(Template expr, Template operator) {
        resultingString = MessageFormat.format("{1}{0}", expr.getOutput(), operator.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
