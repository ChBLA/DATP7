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
        this.evaluationType = type;
        this.parameters = null;
        this.parameterNames = null;
        this.arrayDimensions = arrayDimensions;
    }

    public Type(TypeEnum evaluationType, String[] paramNames, Type[] parameters) {
        this.evaluationType = evaluationType;
        this.parameters = parameters;
        this.parameterNames = paramNames;
    }

    public Type(TypeEnum evaluationType, Type[] parameters) {
        this(evaluationType, null, parameters);
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) return false;
        Type t = (Type) o;

        return t.getEvaluationType() == this.evaluationType &&
                t.getArrayDimensions() == this.arrayDimensions;
    }

    @Override
    public String toString() {
        return "Type: " + evaluationType.toString();
    }

}
