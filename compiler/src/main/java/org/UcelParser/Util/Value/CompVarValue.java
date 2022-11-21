package org.UcelParser.Util.Value;

public class CompVarValue extends StringValue {

    private int[] indices;

    public CompVarValue(String v, int[] indices) {
        super(v);
        this.indices = indices;
    }

    public int[] getIndices() {
        return this.indices;
    }
}
