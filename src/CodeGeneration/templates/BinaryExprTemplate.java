import java.text.MessageFormat;

public class BinaryExprTemplate implements Template {
    private final String resultingString;
    public BinaryExprTemplate(Template left, Template right, String op) {
        resultingString = String.format("%s %s %s", left.getOutput(), op, right.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
