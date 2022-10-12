package CodeGeneration.templates;

import CodeGeneration.templates.Template;

import java.text.MessageFormat;

public class ArrayIndexTemplate extends Template {

    public ArrayIndexTemplate(Template leftExpr, Template arrayIndexExpr) {
        result = String.format("%s[%s]", leftExpr, arrayIndexExpr);
    }

}
