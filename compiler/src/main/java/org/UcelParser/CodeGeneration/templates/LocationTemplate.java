package org.UcelParser.CodeGeneration.templates;

import org.UcelParser.UCELParser_Generated.UCELParser;
import org.stringtemplate.v4.ST;

import java.util.List;

public class LocationTemplate extends Template {
    public Template invariant;
    public Template exponential;
    public UCELParser.LocationContext location;

    public LocationTemplate(Template invariant, Template exponential, UCELParser.LocationContext location) {
        template = new ST("");
        this.invariant = invariant;
        this.exponential = exponential;
        this.location = location;
    }
}
