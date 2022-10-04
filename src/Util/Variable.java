public class Variable {

    private String identifier;
    private Type type;

    public Variable() {
        //Only for tests
    }

    public Variable(String identifier) {
        this(identifier, null);
    }

    public Variable(String identifier, Type type) {
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
}