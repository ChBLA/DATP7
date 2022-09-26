import java.util.ArrayList;

public class Scope {

    private Scope parent;
    private ArrayList<Variable> variables;
    private boolean isComponent;

    public Scope(Scope parent, boolean isComponent) {
        this.parent = parent;
        this.variables = new ArrayList<>();
        this.isComponent = isComponent;
    }

    public Scope(Scope parent, boolean isComponent, ArrayList<Variable> variables) {
        this.parent = parent;
        this.variables = variables;
        this.isComponent = isComponent;
    }

    public void add(Variable v) {
        variables.add(v);
    }

    public Scope getParent() {
        return parent;
    }

    public TableReference find(String s, boolean isVarID) {
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).is(s)) {
                return new TableReference(0, i);
            }
        }

        if (parent != null && !(isComponent && isVarID)) {
            TableReference result = parent.find(s, isVarID);
            result.incrementScopeLevel();
            return result;
        } else {
            return null;
        }
    }

    public Variable get(TableReference tableReference) throws Exception {
        if (tableReference.relativeScope > 0) {
            if (parent == null) {
                throw new Exception("Scope has no parent");
            }
            return parent.get(new TableReference(tableReference.relativeScope - 1, tableReference.variable));
        } else {
            return variables.get(tableReference.variable);
        }
    }

}