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
        if (!(other instanceof StringValue))
            return false;

        StringValue otherVal = (StringValue)other;

        return this.value.equals(otherVal.value);
    }

    @Override
    public String toString()
    {
        return '"' + value +'"';
    }
}
