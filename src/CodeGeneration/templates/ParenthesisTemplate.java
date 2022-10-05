public class ParenthesisTemplate implements Template{
    private final String resultingString;

    public ParenthesisTemplate(Template expr) {
        resultingString = String.format("(%s)", expr.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
