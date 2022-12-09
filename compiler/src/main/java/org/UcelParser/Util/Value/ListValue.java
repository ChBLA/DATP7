package org.UcelParser.Util.Value;

import java.util.ArrayList;

public class ListValue implements InterpreterValue {

    private InterpreterValue[] values;
    private int next;
    public ListValue(InterpreterValue[] v) {
        this.values = v;
        this.next = 0;
    }
    public ListValue(int size) {
        this.values = new InterpreterValue[size];
        this.next = 0;
    }

    public InterpreterValue getValue(int i) {
        return values[i];
    }

    public int size() {
        return values.length;
    }

    public void setValue(int i, InterpreterValue value) {
        this.values[i] = value;
    }

    public void setValue(int[] indices, int level, InterpreterValue value) {
        if(level <  indices.length - 1) {
            ((ListValue) values[indices[level]]).setValue(indices, level + 1, value);
        } else if(level == indices.length - 1){
            values[indices[level]] = value;
        } else
            throw new RuntimeException("ListValue setValue out of bounds");
    }

    private boolean hasSpace() {
        return !(next >= this.values.length)
                && ((this.values[next] instanceof ListValue && ((ListValue) this.values[next]).hasSpace()) || this.values[next] == null);
    }

    @Override
    public String generateName() {
        throw new RuntimeException("Not a String value but a list of parameters");
    }

    @Override
    public String generateName(String prefix) {
        return prefix+generateName();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ListValue)) return false;
        ListValue pv = (ListValue) other;
        if(values.length != pv.size()) return false;
        for(int i = 0; i < values.length; i++) {
            if(!values[i].equals(pv.getValue(i))) return false;
        }
        return true;
    }
}
