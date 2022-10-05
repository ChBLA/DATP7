import java.text.MessageFormat;

public class LiteralTemplate implements Template {

    private final String resultingString;
    public LiteralTemplate(String literal) {
        resultingString = MessageFormat.format("{0}", literal);
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
