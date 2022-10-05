public class DeclarationInfo {

    private String identifier;
    private Type type;

    public DeclarationInfo() {
        //Only for tests
    }

    public DeclarationInfo(String identifier) {
        this(identifier, null);
    }

    public DeclarationInfo(String identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    public Type getType() { return this.type; }

    public boolean isCalled(String s) {
        return s.equals(identifier);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof DeclarationInfo)) return false;

        DeclarationInfo o = (DeclarationInfo) other;

        return ((this.type == null && o.getType() == null) || this.type.equals(o.getType())) &&
                o.isCalled(identifier);
    }
}