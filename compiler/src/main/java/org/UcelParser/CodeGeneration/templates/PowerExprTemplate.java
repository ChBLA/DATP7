package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class PowerExprTemplate extends Template {
    public PowerExprTemplate(Template left, Template right) {
        //TODO: Implement this method properly, this is just a placeholder / idea

        ST funcCall = new ST("pow(<left>, <right>)");
        ST funcDef = new ST(
                "double pow(double base, double exponent) { " +
                "   double acc = base;" +
                "   for (i = abs(exponent); i > 1; i--) {" +
                "       acc *= base;" +
                "   }" +
                "return exponent == 0 ? 1 : acc;}"
        );

    }
}

