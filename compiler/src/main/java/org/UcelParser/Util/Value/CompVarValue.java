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

    @Override
    public boolean equals(Object other) {
        if((other == null || !(other instanceof CompVarValue && super.equals(other)))) return false;
        CompVarValue v = (CompVarValue) other;
        if(v.getIndices().length != indices.length) return false;

        for(int i = 0; i < indices.length; i++) {
            if(indices[i] != v.getIndices()[i]) return false;
        }

        return true;
    }

}
