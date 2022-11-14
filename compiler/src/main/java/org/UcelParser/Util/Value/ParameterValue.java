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

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ParameterValue)) return false;
        ParameterValue pv = (ParameterValue) other;
        if(values.size() != pv.getParameters().size()) return false;
        for(int i = 0; i < values.size(); i++) {
            if(!values.get(i).equals(pv.getParameters().get(i))) return false;
        }
        return true;
    }
}
