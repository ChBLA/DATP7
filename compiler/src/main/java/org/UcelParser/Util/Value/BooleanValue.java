package org.UcelParser.Util.Value;

public class BooleanValue implements InterpreterValue {

    private boolean value;
    public BooleanValue(boolean v) {
        this.value = v;
    }

    @Override
    public int getInt() {
        throw new RuntimeException("Not an integer value but a boolean");
    }

    @Override
    public boolean getBool() {
        return value;
    }

    @Override
    public String generateName() {
        throw new RuntimeException("Not a String value but an boolean");
    }
}
