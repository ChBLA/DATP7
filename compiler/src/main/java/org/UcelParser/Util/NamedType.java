package org.UcelParser.Util;

public class NamedType extends Type {

    //region Properties
    private Type[] parameters;
    private String[] parameterNames;
    private String name;

    //endregion

    //region Constructors
    public NamedType(TypeEnum type) {
        this(type, 0);
    }

    public NamedType(TypeEnum type, int arrayDimensions) {
        this(type, null, null, arrayDimensions);
    }

    public NamedType(TypeEnum evaluationType, String[] paramNames, Type[] parameters) {
        this(evaluationType, paramNames, parameters, 0);
    }

    public NamedType(TypeEnum evaluationType, Type[] parameters) {
        this(evaluationType, null, parameters);
    }

    public NamedType(TypeEnum evaluationType, String[] paramNames, Type[] parameters, int arrayDimensions) {
        super(evaluationType, arrayDimensions);
        this.parameterNames = paramNames;
        this.parameters = parameters;
    }

    public NamedType(TypeEnum type, TypePrefixEnum prefix) {
        this(type);
        setPrefix(prefix);
    }

    //endregion

    //region Getter / Setters
    public String[] getParameterNames() {
        return parameterNames;
    }

    public Type[] getParameters() {
        return parameters;
    }

    public TypeEnum getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(TypeEnum type) {
        evaluationType = type;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    //endregion

    //region Public methods
    public boolean hasParameters() {
        return parameters != null && parameters.length > 0;
    }

    public boolean equalsOrIsArrayOf(Type t) {
        if (!(t instanceof NamedType))
            return false;
        NamedType nt = (NamedType) t;

        if(t.getEvaluationType() != this.evaluationType)
            return false;

        Type[] tParameters = nt.getParameters();
        var anyParams = parameters == null || parameters.length == 0;
        var anyTParams = tParameters == null || tParameters.length == 0;
        if(anyParams && anyTParams)
            return true;

        if(anyParams != anyTParams || parameters.length != tParameters.length) return false;
        for(int i = 0; i < parameters.length; i++) {
            if(!parameters[i].equals(tParameters[i])) return false;
        }

        return true;
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
        return s+"[]".repeat(arrayDimensions);
    }
    //endregion

    //region Private methods

    //endregion
}
