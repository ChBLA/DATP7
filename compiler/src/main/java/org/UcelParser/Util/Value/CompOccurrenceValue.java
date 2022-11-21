package org.UcelParser.Util.Value;

import org.UcelParser.Util.ComponentOccurrence;

import java.util.ArrayList;

public class CompOccurrenceValue extends CompVarValue {

    private InterpreterValue[] arguments;
    private ArrayList<InterpreterValue> interfaces;

    public CompOccurrenceValue(CompVarValue var, InterpreterValue[] arguments) {
        this(var.generateName(), var.getIndices(), arguments);
    }

    public CompOccurrenceValue(String s, int[] indices, InterpreterValue[] arguments) {
        super(s, indices);
        this.arguments = arguments;
        interfaces = new ArrayList<>();
    }

    public InterpreterValue[] getArguments() {
        return arguments;
    }

    public ArrayList<InterpreterValue> getInterfaces() {
        return interfaces;
    }

    @Override
    public boolean equals(Object other) {
        if(!((other instanceof CompOccurrenceValue) && super.equals(other))) return false;
        CompOccurrenceValue ocov = (CompOccurrenceValue) other;


        if(arguments.length != ocov.getArguments().length ||
                interfaces.size() != ocov.getInterfaces().size())
            return false;

        for(int i = 0; i < arguments.length; i++)
            if (!arguments[i].equals(ocov.getArguments()[i]))
                return false;

        for(int i = 0; i < interfaces.size(); i++)
            if (!interfaces.get(i).equals(ocov.getInterfaces().get(i)))
                return false;

        return true;

    }
}
