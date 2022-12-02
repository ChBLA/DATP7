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

    public void setNext(InterpreterValue value) {
        if (next >= this.values.length)
            throw new RuntimeException("Cannot set value at " + next + " for array of size " + this.values.length);
        if (this.values[next] != null)
            throw new RuntimeException("Array value at " + next + " is not empty");
        if (this.values[next] instanceof ListValue) {
            if (((ListValue) this.values[next]).hasSpace()) {
                ((ListValue) this.values[next]).setNext(value);
            } else {
                next++;
                this.setNext(value);
            }
        } else {
            this.values[next++] = value;
        }
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
