package org.UcelParser.Util;

import org.UcelParser.Util.Value.CompVarValue;

public class TemplateOccurrence extends Occurrence {

    public TemplateOccurrence(String prefix, NameGenerator[] parameters, CompVarValue value) {
        super(prefix, parameters, value);
    }

    public NameGenerator[] getParameters() {
        return this.parameters;
    }

    public void setPrefix(String value) {
        this.prefix = value;
    }

}
