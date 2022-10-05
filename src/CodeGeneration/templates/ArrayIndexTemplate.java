import java.text.MessageFormat;

public class ArrayIndexTemplate implements Template{
    private final String resultingString;

    public ArrayIndexTemplate(Template leftExpr, Template arrayIndexExpr) {
        resultingString = MessageFormat.format("{0}[{1}]", leftExpr.getOutput(), arrayIndexExpr.getOutput());
    }
    @Override
    public String getOutput() {
        return toString();
    }

    @Override
    public String toString() {
        return resultingString;
    }
}
