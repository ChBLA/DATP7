package org.UcelParser.Util.Value;

import java.util.ArrayList;

public class ListValue implements InterpreterValue {

    private InterpreterValue[] values;
    public ListValue(InterpreterValue[] v) {
        this.values = v;
    }
    public ListValue(int size) {
        this.values = new InterpreterValue[size];
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
