import java.util.ArrayList;

public class Type {

    enum TypeEnum {
        intType,
        doubleType,
        charType,
        stringType,
        chanType,
        scalarType,
        structType,
        voidType,
        errorType
    }

    private TypeEnum evaluationType;
    private Type[] parameters;
    private int arrayDimensions;

    public Type(TypeEnum type) {
        this(type, 0);
    }

    public Type(TypeEnum type, int arrayDimensions) {
        this.evaluationType = type;
        this.parameters = null;
        this.arrayDimensions = arrayDimensions;
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
