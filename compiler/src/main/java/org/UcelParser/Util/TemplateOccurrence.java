package org.UcelParser.Util;

public class TemplateOccurrence {
    private final NameGenerator[] parameters;
    private String prefix = "";

    public TemplateOccurrence(String prefix, NameGenerator[] parameters) {
        this.prefix = prefix;
        this.parameters = parameters;
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
