package org.Ucel;

public class VerificationQuery implements IVerificationQuery {
    private String formula;
    @Override
    public String getFormula() {
        return formula;
    }

    public void setFormula(String exprString) {
        formula = exprString;
    }

    private String comment;
    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
