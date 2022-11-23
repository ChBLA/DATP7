package org.UcelParser.Util;

import java.util.ArrayList;

public class TypeFactory {
    private final ArrayList<String> namedComponents = new ArrayList<>();
    private final ArrayList<String> namedTemplates = new ArrayList<>();
    private final ArrayList<String> namedFunctions = new ArrayList<>();
    private final ArrayList<String> namedIntegers = new ArrayList<>();
    private final ArrayList<String> namedStructs = new ArrayList<>();
    private final ArrayList<String> namedProcesses = new ArrayList<>();
    private final ArrayList<String> namedInterfaces = new ArrayList<>();

    public Type createInstanceType(Type type) {
        assert !(type instanceof NamedType);
        assert !type.getInstance();

        Type result = new Type(type.getEvaluationType(), type.getArrayDimensions(), type.getPrefix());
        result.setInstance(true);
        return result;
    }

    public NamedType createInstanceNamedType(NamedType baseType) {
        // TypeEnum evaluationType, String[] paramNames, Type[] parameters, int arrayDimensions
        assert !baseType.getInstance();

        // Copy arrays:
        Type[] paramCopy = null;
        String[] paramNamesCopy = null;

        if (baseType.getParameters() == null) {
            paramCopy = new Type[baseType.getParameters().length];
            for (int i = 0; i < baseType.getParameters().length; i++)
                paramCopy[i] = this.copy(baseType.getParameters()[i]);
        }

        if (baseType.getParameterNames() == null) {
            paramNamesCopy = new String[baseType.getParameterNames().length];
            for (int i = 0; i < baseType.getParameterNames().length; i++)
                paramNamesCopy[i] = baseType.getParameterNames()[i];
        }

        NamedType result = new NamedType(baseType.getEvaluationType(), paramNamesCopy, paramCopy, baseType.getArrayDimensions());
        result.setInstance(true);
        return result;
    }

    public Type createArrayInstanceType() {
        return null;
    }

    public Type createNamedType() {
        return null;
    }

    public Type copy(Type type) {
        return type;
    }

    public NamedType copy(NamedType type) {
        return type;
    }

    private void enterNewNamedType(Type.TypeEnum type, String name) {
        switch (type) {
            case intType -> namedIntegers.add(name);
            case componentType -> namedComponents.add(name);
            case processType -> namedProcesses.add(name);
            case templateType -> namedTemplates.add(name);
            case functionType -> namedFunctions.add(name);
            case structType -> namedStructs.add(name);
            case interfaceType -> namedInterfaces.add(name);
            default -> throw new RuntimeException("Invalid named type");
        }
    }

}
