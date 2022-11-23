package org.UcelParser.Util;

public class Type {

    //region Properties
    protected TypeEnum evaluationType;
    protected TypePrefixEnum prefix;
    protected Boolean isInstance;
    protected int arrayDimensions;
    //endregion

    public Boolean getInstance() {
        return isInstance;
    }

    public void setInstance(Boolean instance) {
        isInstance = instance;
    }

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
        processType, templateType,
        voidType,
        errorType, invalidType,
        functionType,
        interfaceType,
        componentType,

        seperatorType
    }

    //region Constructors
    public Type(TypeEnum type) {
        this(type, 0);
    }

    public Type(TypeEnum evaluationType, int arrayDimensions) {
        this(evaluationType, arrayDimensions, TypePrefixEnum.noPrefix);
    }

    public Type(TypeEnum type, TypePrefixEnum prefix) {
        this(type, 0, prefix);
    }

    public Type(TypeEnum type, int arrayDimensions, TypePrefixEnum prefix) {
        this.evaluationType = type;
        this.arrayDimensions = arrayDimensions;
        this.prefix = prefix;
    }
    //endregion

    public TypeEnum getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(TypeEnum type) {
        evaluationType = type;
    }

    public int getArrayDimensions() {
        return arrayDimensions;
    }

    public TypePrefixEnum getPrefix() {
        return prefix;
    }

    protected void setPrefix(TypePrefixEnum prefix) {
        this.prefix = prefix;
    }

    public boolean equalsOrIsArrayOf(Type t) {
        return t.getEvaluationType() == this.evaluationType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) return false;
        Type t = (Type) o;

        return equalsOrIsArrayOf(t) &&
                t.getArrayDimensions() == this.arrayDimensions;
    }

    public boolean isSameBaseType(Type type) {
        return this.getEvaluationType() == type.getEvaluationType();
    }

    @Override
    public String toString() {
        return (prefix != TypePrefixEnum.noPrefix ? prefix + " " : "") + evaluationType.toString() + "[]".repeat(arrayDimensions);
    }

}
