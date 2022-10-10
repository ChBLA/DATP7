import java.util.ArrayList;

public class Type {

    enum TypeEnum {
        intType,
        doubleType,
        boolType,
        charType,
        stringType,
        chanType,
        scalarType,
        structType,
        voidType,
        errorType, invalidType;
    }

    private TypeEnum evaluationType;
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

    public boolean equalsOrIsArrayOf(Type t) {
        if(t.getEvaluationType() != this.evaluationType) return false;

        Type[] tParameters = t.getParameters();
        if((parameters == null || parameters.length == 0) &&
            tParameters == null || tParameters.length == 0) return true;

        if(parameters.length != tParameters.length) return false;
        for(int i = 0; i < parameters.length; i++) {
            if(!parameters[i].equals(tParameters[i])) return false;
        }

        return true;
    }

    public Type deepCopy() {
        return deepCopy(arrayDimensions);
    }

    public Type deepCopy(int newArrayDimensions) {
        Type[] parameterCopies = null;

        if(parameters != null) {
            parameterCopies = new Type[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parameterCopies[i] = parameters[i].deepCopy();
            }
        }

        return new Type(evaluationType, parameterNames, parameters, newArrayDimensions);
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
        return "Type: " + evaluationType.toString();
    }

}
