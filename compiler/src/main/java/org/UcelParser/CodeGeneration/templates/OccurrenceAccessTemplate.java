package org.UcelParser.CodeGeneration.templates;

import org.UcelParser.Util.Occurrence;
import org.stringtemplate.v4.ST;

public class OccurrenceAccessTemplate extends Template {
    private final Occurrence occurrence;

    public OccurrenceAccessTemplate(Occurrence occurrence) {
        this.template = new ST("");
        this.occurrence = occurrence;
    }

    public Occurrence getOccurrence() {
        return occurrence;
    }
}
