package org.UcelParser.Util.Value;

public class VoidValue implements InterpreterValue {

    public VoidValue() { }


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
        return "void";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof VoidValue;
    }
}
