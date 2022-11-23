package org.UcelParser.Util.Value;

import org.UcelParser.Util.ComponentOccurrence;
import org.UcelParser.Util.Type;

import java.util.ArrayList;

public class CompOccurrenceValue implements InterpreterValue {

    private InterpreterValue[] arguments, interfaces;


    public CompOccurrenceValue(InterpreterValue[] arguments, Type compType) {
        this.arguments = arguments;
        interfaces = new InterpreterValue[compType.getParameters().length - arguments.length - 2];
    }

    @Override
    public String generateName() {
        return "comp occurrence";
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

    public InterpreterValue[] getInterfaces() {
        return interfaces;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof CompOccurrenceValue)) return false;
        CompOccurrenceValue ocov = (CompOccurrenceValue) other;


        if(arguments.length != ocov.getArguments().length ||
                interfaces.length != ocov.getInterfaces().length)
            return false;

        for(int i = 0; i < arguments.length; i++)
            if (!arguments[i].equals(ocov.getArguments()[i]))
                return false;

        for(int i = 0; i < interfaces.length; i++)
            if (!interfaces[i].equals(ocov.getInterfaces()[i]))
                return false;

        return true;

    }
}
