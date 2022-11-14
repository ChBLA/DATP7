package org.UcelParser.Util.Value;

import org.UcelParser.Util.NameGenerator;

public class IntegerValue implements InterpreterValue {

    private int value;
    public IntegerValue(int v) {
        this.value = v;
    }

    @Override
    public int getInt() {
        return value;
    }

    @Override
    public boolean getBool() {
        throw new RuntimeException("Not a boolean value but an integer");
    }

    @Override
    public String generateName() {
        throw new RuntimeException("Not a String value but an integer");
    }
}
