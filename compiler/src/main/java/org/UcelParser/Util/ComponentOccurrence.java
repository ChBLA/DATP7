package org.UcelParser.Util;

import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Value.InterpreterValue;
import org.UcelParser.Util.Value.StringValue;

import java.util.List;

public class ComponentOccurrence {
    private final UCELParser.CompConContext node;
    private final InterpreterValue[] parameters;
    private final InterpreterValue[] interfaces;
    private String prefix = "";

    public ComponentOccurrence(UCELParser.CompConContext node, InterpreterValue[] parameters) {
        this.node = node;
        this.parameters = parameters;
        this.interfaces = new InterpreterValue[0];
    }

    public UCELParser.CompConContext getNode() {
        return this.node;
    }

    public InterpreterValue[] getParameters() {
        return this.parameters;
    }

    public InterpreterValue[] getInterfaces() {
        return this.interfaces;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
}
