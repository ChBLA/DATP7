package org.UcelParser.Util.Value;

import org.UcelParser.Util.NameGenerator;

import javax.naming.Name;

public class VariableValue implements InterpreterValue {

    private String prefix, postfix;
    private NameGenerator generator;
    public VariableValue(String prefix, String postfix, NameGenerator generator) {
        this.prefix = prefix;
        this.postfix = postfix;
        this.generator = generator;
    }

    @Override
    public String generateName() {
        return prefix + generator.generateName() + postfix;
    }

    @Override
    public String generateName(String prefix) {
        return generateName();
    }

    @Override
    public String toString() {
        return generateName();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof InterpreterValue && ((InterpreterValue) obj).generateName().equals(this.generateName());
    }
}
