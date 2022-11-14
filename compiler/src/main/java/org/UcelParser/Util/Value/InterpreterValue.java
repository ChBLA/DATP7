package org.UcelParser.Util.Value;

import org.UcelParser.Util.NameGenerator;

public interface InterpreterValue extends NameGenerator {
    int getInt();

    boolean getBool();
}
