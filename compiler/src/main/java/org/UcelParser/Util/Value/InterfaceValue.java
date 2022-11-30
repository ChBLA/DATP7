package org.UcelParser.Util.Value;

import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.NameGenerator;

public class InterfaceValue implements InterpreterValue {

    private final UCELParser.InterfaceDeclContext interfaceNode;
    private final int id;
    private final NameGenerator generator;

    public InterfaceValue(UCELParser.InterfaceDeclContext node, int id, NameGenerator generator) {
        this.interfaceNode = node;
        this.id = id;
        this.generator = generator;
    }

    public int getId() {
        return id;
    }
    public UCELParser.InterfaceDeclContext getInterfaceNode() {
        return interfaceNode;
    }
    @Override
    public String generateName() {
        return generator.generateName() + id;
    }

    @Override
    public String generateName(String prefix) {
        return prefix + generateName();
    }

    @Override
    public String toString() {
        return generateName();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof InterfaceValue) && id == ((InterfaceValue) other).id;
    }


}
