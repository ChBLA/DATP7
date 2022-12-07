package org.UcelParser.Util;

import org.UcelParser.Util.Value.CompVarValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Occurrence {
    protected ArrayList<Occurrence> children;
    protected CompVarValue compVarValue;
    protected final NameGenerator[] parameters;
    protected String prefix = "";

    public Occurrence(String prefix, NameGenerator[] parameters, CompVarValue value) {
        this.compVarValue = value;
        this.children = new ArrayList<>();
        this.prefix = prefix;
        this.parameters = parameters;
    }

    public CompVarValue getCompVarValue() {
        return compVarValue;
    }

    public ArrayList<Occurrence> getChildren() {
        return children;
    }

    public void addChild(Occurrence occurrence) {
        children.add(occurrence);
    }

    public NameGenerator[] getParameters() {
        return this.parameters;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public Occurrence findChildOccurrence(String name, List<Integer> indices) {
        for (var child : this.children) {
            var value = child.getCompVarValue();
            if (value.toString().equals(name)) {
                boolean matchingIndices = indices.size() == value.getIndices().length;
                for (int i = 0; i < indices.size() && matchingIndices; i++) {
                    if (indices.get(i) != value.getIndices()[i])
                        matchingIndices = false;
                }
                if (matchingIndices)
                    return child;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TemplateOccurrence)) return false;
        boolean b = true;
        TemplateOccurrence co = (TemplateOccurrence) other;

        if(parameters.length != co.getParameters().length)
            return false;

        for(int i = 0; i < parameters.length; i++)
            if (!parameters[i].equals(co.getParameters()[i]))
                return false;

        return true;
    }

}
