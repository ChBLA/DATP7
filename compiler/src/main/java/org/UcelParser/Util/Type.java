package org.UcelParser.Util;

public class Type {

    public enum TypePrefixEnum {
        urgent, broadcast, meta, constant, noPrefix;
    }

    public enum TypeEnum {
        intType,
        doubleType,
        boolType,
        charType,
        stringType,
        chanType,
        clockType,
        scalarType,
        structType,
        voidType,
        errorType, invalidType,
        functionType,
    }

    private TypeEnum evaluationType;
    private TypePrefixEnum prefix;
    private Type[] parameters;
    private String[] parameterNames;
    private int arrayDimensions;

    public Type(TypeEnum type) {
        this(type, 0);
    }

    public Type(TypeEnum type, int arrayDimensions) {
        this(type, null, null, arrayDimensions);
    }

    public Type(TypeEnum evaluationType, String[] paramNames, Type[] parameters) {
        this(evaluationType, paramNames, parameters, 0);
    }

    public Type(TypeEnum evaluationType, String[] paramNames, Type[] parameters, int arrayDimensions) {
        this.evaluationType = evaluationType;
        this.parameters = parameters;
        this.parameterNames = paramNames;
        this.arrayDimensions = arrayDimensions;
        this.prefix = TypePrefixEnum.noPrefix;
    }

    public Type(TypeEnum evaluationType, Type[] parameters) {
        this(evaluationType, null, parameters, 0);
    }

    public TypeEnum getEvaluationType() {
        return evaluationType;
    }

    public boolean hasParameters() {
        return parameters != null && parameters.length > 0;
    }

    public int getArrayDimensions() {
        return arrayDimensions;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public Type[] getParameters() {
        return parameters;
    }

    public Type deepCopy() {
        return deepCopy(arrayDimensions);
    }

    public Type deepCopy(TypePrefixEnum prefix) {
        Type t = deepCopy();
        t.setPrefix(prefix);
        return t;
    }

    public TypePrefixEnum getPrefix() {
        return prefix;
    }

    private void setPrefix(TypePrefixEnum prefix) {
        this.prefix = prefix;
    }

    public Type deepCopy(int newArrayDimensions) {
        Type[] parameterCopies = null;

        if(parameters != null) {
            parameterCopies = new Type[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parameterCopies[i] = parameters[i].deepCopy();
            }
        }

        return new Type(evaluationType, parameterNames, parameterCopies, newArrayDimensions);
    }

    public boolean equalsOrIsArrayOf(Type t) {
        if(t.getEvaluationType() != this.evaluationType || t.getPrefix() != prefix) return false;

        Type[] tParameters = t.getParameters();
        if((parameters == null || parameters.length == 0) &&
                tParameters == null || tParameters.length == 0) return true;

        if(parameters.length != tParameters.length) return false;
        for(int i = 0; i < parameters.length; i++) {
            if(!parameters[i].equals(tParameters[i])) return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) return false;
        Type t = (Type) o;

        return equalsOrIsArrayOf(t) &&
                t.getArrayDimensions() == this.arrayDimensions;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder((prefix != TypePrefixEnum.noPrefix ? prefix  + " " : "") + evaluationType.toString());
        if(evaluationType == TypeEnum.structType) {
            s.append("{ ");
            if(parameters != null) {
                for (Type t : parameters)
                    s.append(t);
             } else s.append("_");
            s.append(s +" }");
        } else if (evaluationType == TypeEnum.functionType) {
            s.append( ": (");
            if(parameters != null) {
                for (int i = 1; i < parameters.length; i++)
                    s.append((i > 1 ? ", " : "") + parameters[i]);
                s.append(") -> " + parameters[0]);
            } else {
                s.append("_) -> _");
            }
        }
        return "[]".repeat(arrayDimensions);
    }

}
