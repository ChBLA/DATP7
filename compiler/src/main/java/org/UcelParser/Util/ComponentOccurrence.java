package org.UcelParser.Util;

import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Value.CompVarValue;

public class ComponentOccurrence extends Occurrence {
    private final NameGenerator[] interfaces;
    private final UCELParser.ComponentContext node;
    public ComponentOccurrence(UCELParser.ComponentContext node, String prefix, NameGenerator[] parameters, NameGenerator[] interfaces, CompVarValue value) {
        super(prefix, parameters, value);
        this.node = node;
        this.interfaces = interfaces;
    }

    public NameGenerator[] getInterfaces() {
        return this.interfaces;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ComponentOccurrence)) return false;
        boolean b = true;
        ComponentOccurrence co = (ComponentOccurrence) other;

        if(parameters.length != co.getParameters().length ||
                interfaces.length != co.getInterfaces().length)
            return false;

        for(int i = 0; i < parameters.length; i++)
            if (!parameters[i].equals(co.getParameters()[i]))
                return false;

        for(int i = 0; i < interfaces.length; i++)
            if (!interfaces[i].equals(co.getInterfaces()[i]))
                return false;

        return true;
    }

    public UCELParser.ComponentContext getNode() {
        return node;
    }
}
