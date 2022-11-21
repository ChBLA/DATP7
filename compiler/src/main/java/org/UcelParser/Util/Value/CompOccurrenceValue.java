package org.UcelParser.Util.Value;

import org.UcelParser.Util.ComponentOccurrence;

public class CompOccurrenceValue extends StringValue implements InterpreterValue {

    private ComponentOccurrence occurrence;
    private int[] indices;

    public CompOccurrenceValue(String v, int[] indices, ComponentOccurrence occurrence) {
        super(v);
        this.occurrence = occurrence;
        this.indices = indices;
    }

    public ComponentOccurrence getOccurrence() {
        return occurrence;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof CompOccurrenceValue) &&
                super.equals(other) &&
                occurrence.equals(((CompOccurrenceValue) other).getOccurrence());
    }
}
