package org.UcelParser.Util.Value;

import org.UcelParser.Util.NameGenerator;

import javax.naming.Name;

public class PointerValue implements InterpreterValue {
    private final String name;
    private final NameGenerator generator;

    public PointerValue(String name, NameGenerator generator) {
        this.name = name;
        this.generator = generator;
    }


    @Override
    public String generateName() {
        return String.format("%s_%s", generator.generateName(), name);
    }

    @Override
    public String generateName(String componentPrefix) {
        return String.format("%s%s_%s", componentPrefix, generator.generateName(), name);
    }
}
