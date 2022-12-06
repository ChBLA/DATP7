package org.UcelParser.Util;

import org.UcelParser.Util.Value.CompVarValue;
import org.UcelParser.Util.Value.Occurrence;

public class TemplateOccurrence extends Occurrence {

    public TemplateOccurrence(String prefix, NameGenerator[] parameters, CompVarValue value) {
        super(prefix, parameters, value);
    }
}
