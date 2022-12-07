package org.UcelParser.Util;

import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Value.CompVarValue;

public class TemplateOccurrence extends Occurrence {
    private final UCELParser.PtemplateContext node;
    public TemplateOccurrence(UCELParser.PtemplateContext node, String prefix, NameGenerator[] parameters, CompVarValue value) {
        super(prefix, parameters, value);
        this.node = node;
    }

    public NameGenerator[] getParameters() {
        return this.parameters;
    }

    public void setPrefix(String value) {
        this.prefix = value;
    }

    public UCELParser.PtemplateContext getNode() {
        return node;
    }
}
