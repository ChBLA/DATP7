package org.UcelParser.CodeGeneration.templates;

public class VerificationTemplate extends Template{

    public VerificationTemplate(String op, String id, Template type, Template expr) {
        result = String.format("%s (%s:%s) %s", op, id, type, expr);
    }

}
