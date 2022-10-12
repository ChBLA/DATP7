package CodeGeneration.templates;

public class MarkExpressionTemplate extends Template{

    public MarkExpressionTemplate(Template expr) {
        result = String.format("%s'", expr);
    }

}
