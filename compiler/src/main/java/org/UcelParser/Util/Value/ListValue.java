package org.UcelParser.Util.Value;

import java.util.ArrayList;

public class ListValue implements InterpreterValue {

    private ArrayList<InterpreterValue> values;
    public ListValue(ArrayList<InterpreterValue> v) {
        this.values = v;
    }

    public ArrayList<InterpreterValue> getValues() {
        return values;
    }

    @Override
    public String generateName() {
        throw new RuntimeException("Not a String value but a list of parameters");
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ListValue)) return false;
        ListValue pv = (ListValue) other;
        if(values.size() != pv.getValues().size()) return false;
        for(int i = 0; i < values.size(); i++) {
            if(!values.get(i).equals(pv.getValues().get(i))) return false;
        }
        return true;
    }
}
