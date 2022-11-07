package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

public class SelectTemplate extends Template {
    public SelectTemplate(List<String> ids, List<Template> types) {
        if (ids.size() == 1) {
            createSingleSelectString(ids, types);
        }
        else {
            createMultipleSelectsString(ids, types);
        }
    }

    private void createSingleSelectString(List<String> ids, List<Template> types) {
        template = new ST("<ids; separator=\", \"> : <types; separator=\", \">");
        template.add("ids", ids);
        template.add("types", types);
    }

    // TODO: Can maybe be done just using ST formatting but this is easier for now
    private void createMultipleSelectsString(List<String> ids, List<Template> types) {
        List<String> combined = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            String combinedString = String.format("%s : %s", ids.get(i), types.get(i));
            combined.add(combinedString);
        }

        template = new ST("<combined; separator=\", \">");
        template.add("combined", combined);
    }
}
