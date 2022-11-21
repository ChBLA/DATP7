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
        return toString();
    }

    @Override
    public String generateName(String prefix) {
        return prefix+generateName();
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof BooleanValue && ((BooleanValue) other).getBool() == value;
    }
}
