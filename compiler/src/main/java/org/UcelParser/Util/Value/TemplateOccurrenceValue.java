package org.UcelParser.Util.Value;

import org.UcelParser.Util.Type;

public class TemplateOccurrenceValue implements InterpreterValue {

    private InterpreterValue[] arguments;

    private String prefix;

    private CompVarValue compVarValue;

    public TemplateOccurrenceValue(String prefix, InterpreterValue[] arguments, CompVarValue value) {
        this.prefix = prefix;
        this.arguments = arguments;
        this.compVarValue = value;
    }

    public CompVarValue getCompVarValue() {
        return compVarValue;
    }

    @Override
    public String generateName() {
        return prefix;
    }

    @Override
    public String generateName(String prefix) {
        return prefix + generateName();
    }

    @Override
    public String toString() {
        return generateName();
    }

    public InterpreterValue[] getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TemplateOccurrenceValue)) return false;
        TemplateOccurrenceValue ocov = (TemplateOccurrenceValue) other;

        for(int i = 0; i < arguments.length; i++)
            if (!arguments[i].equals(ocov.getArguments()[i]))
                return false;

        return true;

    }
}
