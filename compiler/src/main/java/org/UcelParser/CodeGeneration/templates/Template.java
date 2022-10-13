package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.Map;

public abstract class Template {
    public ST template;

    public Template replaceValue(String key, Object value) {
        template.remove(key);
        template.add(key, value);
        return this;
    }
    public String toString() {
        return template.render();
    }
}
