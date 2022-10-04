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

    public TableReference add(Variable v) {
        variables.add(v);
        return new TableReference(0, variables.size() - 1);
    }

    public Scope getParent() {
        return parent;
    }

    public boolean isUnique(String s, boolean isVarID) {
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).isCalled(s)) {
                return false;
            }
        }

        if (parent != null && !(isComponent && isVarID)) {
            return parent.isUnique(s, isVarID);
        }

        return true;
    }

    public TableReference find(String s, boolean isVarID) throws Exception {
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).isCalled(s)) {
                return new TableReference(0, i);
            }
        }

        if (parent != null && !(isComponent && isVarID)) {
            TableReference result = parent.find(s, isVarID);
            result.incrementScopeLevel();
            return result;
        } else {
            throw new Exception("Identifier Not Found In Scope");
        }
    }

    public Variable get(TableReference tableReference) throws Exception {
        if (tableReference.getRelativeScope() > 0) {
            if (parent == null) {
                throw new Exception("Scope has no parent");
            }
            return parent.get(tableReference.moveOutOfScope());
        } else {
            return variables.get(tableReference.getVariable());
        }
    }

}