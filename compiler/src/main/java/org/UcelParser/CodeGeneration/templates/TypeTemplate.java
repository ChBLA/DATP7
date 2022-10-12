package org.UcelParser.CodeGeneration.templates;

public class TypeTemplate implements Template {
    private final String resultingString;

    public TypeTemplate(Template prefix, Template typeID) {
        if (prefix.getOutput().equals("")) {
            resultingString = typeID.getOutput();
        }
        else {
            resultingString = String.format("%s %s", prefix.getOutput(), typeID.getOutput());
        }
    }

    public TypeTemplate(Template typeID) {
        this(new ManualTemplate(""), typeID);
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
