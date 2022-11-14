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
        throw new RuntimeException("Not a String value but an integer");
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof IntegerValue && ((IntegerValue) other).getInt() == value;
    }
}
