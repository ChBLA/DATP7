package org.Ucel;

public class VerificationExpression implements IVerificationExpression {
    private String expressionString;
    @Override
    public String getExpressionString() {
        return expressionString;
    }

    public void setExpressionString(String exprString) {
        expressionString = exprString;
    }
}
