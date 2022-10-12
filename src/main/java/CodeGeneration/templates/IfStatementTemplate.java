package CodeGeneration.templates;

public class IfStatementTemplate extends Template{
    public IfStatementTemplate(Template expr, Template stmnt1, Template stmnt2) {
        result = String.format("if (%s) %s else %s", expr, stmnt1, stmnt2);
    }

    public IfStatementTemplate(Template expr, Template stmnt1) {
        result = String.format("if (%s) %s", expr, stmnt1);
    }

}
