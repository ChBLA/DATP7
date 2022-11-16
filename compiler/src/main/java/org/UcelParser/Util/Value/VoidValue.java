package org.UcelParser.Util.Value;

public class VoidValue implements InterpreterValue {

    public VoidValue() { }


    @Override
    public String generateName() {
        return toString();
    }

    @Override
    public String toString() {
        return "void";
    }
    
}
