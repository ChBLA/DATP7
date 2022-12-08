package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class IntegerValueTemplate extends Template{
    public final Integer value;
    public IntegerValueTemplate(Integer value) {
        template = new ST("<val>");
        template.add("val", value);
        this.value = value;
    }
}
