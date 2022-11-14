package org.UcelParser.Util.Value;

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
    public String generateName() {
        throw new RuntimeException("Not a String value but a list of parameters");
    }
}
