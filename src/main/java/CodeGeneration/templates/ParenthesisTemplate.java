package CodeGeneration.templates;

public class ParenthesisTemplate extends Template{

    public ParenthesisTemplate(Template expr) {
        result = String.format("(%s)", expr);
    }

}
