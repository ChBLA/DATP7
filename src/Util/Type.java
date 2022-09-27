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

    public Type(TypeEnum type) {
        this.evaluationType = type;
        this.parameters = null;
    }

    public TypeEnum getEvaluationType() {
        return evaluationType;
    }

    public boolean hasParameters() {
        return parameters != null && parameters.length > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) return false;
        Type t = (Type) o;

        return t.getEvaluationType() == this.evaluationType;
    }

    @Override
    public String toString() {
        return "Type: " + evaluationType.toString();
    }

}
