package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

public class VerificationListTemplate extends Template {
    public final List<PQueryTemplate> queries;
    public VerificationListTemplate(List<PQueryTemplate> queries) {
        template = new ST("");
        this.queries = queries != null ? queries : new ArrayList<>();
    }
}
