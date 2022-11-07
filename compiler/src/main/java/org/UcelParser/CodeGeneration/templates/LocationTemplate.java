package org.UcelParser.CodeGeneration.templates;

import org.UcelParser.UCELParser_Generated.UCELParser;
import org.stringtemplate.v4.ST;

public class LocationTemplate extends Template {
    public Template invariant;
    public Template exponential;
    public UCELParser.LocationContext location;
    public String ID;

    public LocationTemplate(Template invariant, Template exponential, UCELParser.LocationContext location, String ID) {
        template = new ST("");
        this.invariant = invariant;
        this.exponential = exponential;
        this.location = location;
        this.ID = ID;
    }


}
