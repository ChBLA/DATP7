package CodeGeneration.templates;

public class ReturnStatementTemplate extends Template{

    public ReturnStatementTemplate(Template expr) {
        result = String.format("return %s;", expr);
    }

    public ReturnStatementTemplate() {
        result = "return;";
    }

}
