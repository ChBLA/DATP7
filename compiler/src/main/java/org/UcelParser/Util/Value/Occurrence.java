package org.UcelParser.Util.Value;

import org.UcelParser.Util.NameGenerator;
import org.UcelParser.Util.TemplateOccurrence;

import java.util.ArrayList;

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
