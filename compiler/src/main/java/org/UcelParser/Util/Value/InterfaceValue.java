package org.UcelParser.Util.Value;

public class InterfaceValue implements InterpreterValue {

    private int param;
    private int id;

    public InterfaceValue(int param, int id) {
        this.param = param;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getParam() {
        return param;
    }

    @Override
    public String generateName() {
        return Integer.toString(id);
    }

    @Override
    public String generateName(String prefix) {
        return prefix+generateName();
    }

    @Override
    public String toString() {
        return generateName();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof InterfaceValue) && id == ((InterfaceValue) other).id;
    }
}
