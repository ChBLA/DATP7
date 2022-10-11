import java.text.MessageFormat;

public class ArrayIndexTemplate implements Template{
    private final String resultingString;

    public ArrayIndexTemplate(Template leftExpr, Template arrayIndexExpr) {
        resultingString = String.format("%s[%s]", leftExpr.getOutput(), arrayIndexExpr.getOutput());
    }
    @Override
    public String getOutput() {
        return resultingString;
    }
}
