package org.UcelParser.Util.Value;

public class StringValue implements InterpreterValue {

    private String value;
    public StringValue(String v) {
        this.value = v;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }
    @Override
    public String generateName() {
        return value;
    }

    @Override
    public String generateName(String prefix) {
        return prefix+generateName();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StringValue && ((StringValue) other).generateName().equals(value);
    }
}
