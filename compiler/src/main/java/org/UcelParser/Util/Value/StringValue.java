package org.UcelParser.Util.Value;

public class StringValue implements InterpreterValue {

    private String value;
    public StringValue(String v) {
        this.value = v;
    }

    @Override
    public String generateName() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StringValue && ((StringValue) other).equals(value);
    }
}
