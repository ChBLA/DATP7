package CodeGeneration.templates;

import CodeGeneration.templates.Template;

import java.text.MessageFormat;

public class LiteralTemplate extends Template {

    public LiteralTemplate(String literal) {
        result = String.format("%s", literal);
    }


}
