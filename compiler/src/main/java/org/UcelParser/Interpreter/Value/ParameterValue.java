package org.UcelParser.Interpreter.Value;

import org.UcelParser.Interpreter.Value.InterpreterValue;

import java.util.ArrayList;

public class ParameterValue implements InterpreterValue {

    private ArrayList<InterpreterValue> values;
    public ParameterValue(ArrayList<InterpreterValue> v) {
        this.values = v;
    }

    public ArrayList<InterpreterValue> getParameters() {
        return values;
    }

    @Override
    public int getInt() {
        throw new RuntimeException("Not an integer value but a list of parameters");
    }

    @Override
    public boolean getBool() {
        throw new RuntimeException("Not an boolean value but a list of parameters");
    }

    @Override
    public String getString() {
        throw new RuntimeException("Not a String value but a list of parameters");
    }
}
