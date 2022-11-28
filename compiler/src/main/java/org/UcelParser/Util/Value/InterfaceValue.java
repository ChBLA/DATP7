package org.UcelParser.Util.Value;

import org.UcelParser.Util.NameGenerator;

public class InterfaceValue implements InterpreterValue {

    private final int param;
    private final int id;
    private final NameGenerator generator;

    public InterfaceValue(int param, int id, NameGenerator generator) {
        this.param = param;
        this.id = id;
        this.generator = generator;
    }

    public int getId() {
        return id;
    }

    public int getParam() {
        return param;
    }

    @Override
    public String generateName() {
        return generator.generateName() + id;
    }

    @Override
    public String generateName(String prefix) {
        return prefix + generateName();
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
