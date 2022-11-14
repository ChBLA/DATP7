package org.UcelParser.Interpreter.Value;

import org.UcelParser.Interpreter.Value.InterpreterValue;

public class StringValue implements InterpreterValue {

    private String value;
    public StringValue(String v) {
        this.value = v;
    }

    @Override
    public int getInt() {
        throw new RuntimeException("Not an integer value but an String");
    }

    @Override
    public boolean getBool() {
        throw new RuntimeException("Not a boolean value but a String");
    }

    @Override
    public String getString() {
        return value;
    }
}
