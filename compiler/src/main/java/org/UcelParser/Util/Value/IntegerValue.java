package org.UcelParser.Util.Value;

import org.UcelParser.Util.NameGenerator;

public class IntegerValue implements InterpreterValue {

    private int value;
    public IntegerValue(int v) {
        this.value = v;
    }

    public int getInt() {
        return value;
    }

    @Override
    public String generateName() {
        return toString();
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof IntegerValue && ((IntegerValue) other).getInt() == value;
    }
}
