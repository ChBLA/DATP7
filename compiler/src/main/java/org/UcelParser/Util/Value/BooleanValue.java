package org.UcelParser.Util.Value;

public class BooleanValue implements InterpreterValue {

    private boolean value;
    public BooleanValue(boolean v) {
        this.value = v;
    }

    public boolean getBool() {
        return value;
    }

    @Override
    public String generateName() {
        throw new RuntimeException("Not a String value but an boolean");
    }
}
