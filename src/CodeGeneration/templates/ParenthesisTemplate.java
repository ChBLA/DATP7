public class ParenthesisTemplate implements Template{
    private final String resultingString;

    public ParenthesisTemplate(Template expr) {
        resultingString = "(%s)".formatted(expr.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
