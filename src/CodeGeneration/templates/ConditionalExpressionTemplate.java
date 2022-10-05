public class ConditionalExpressionTemplate implements Template{
    private final String resultingString;

    public ConditionalExpressionTemplate(Template condition, Template positiveResult, Template negativeResult) {
        resultingString = "%s ? %s : %s".formatted(condition, positiveResult, negativeResult);
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
