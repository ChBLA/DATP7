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

    public void add(Variable v) {
        variables.add(v);
    }

    public Scope getParent() {
        return parent;
    }

    public int[] find(String s, boolean isVarID) {
        for (int i = 0; i < variables.size(); i++) {
            if(variables.get(i).is(s)) {
                return new int[]{0, i};
            }
        }

        if(parent != null && !(isComponent && isVarID)) {
            int[] result = parent.find(s, isVarID);
            result[0]++;
            return result;
        } else {
            return null;
        }
    }

    public Variable get(int relativeScope, int variable) throws Exception {
        if(relativeScope > 0) {
            if (parent == null) {
                throw new Exception("Scope has no parent");
            }
            return parent.get(relativeScope - 1, variable);
        } else {
            return variables.get(variable);
        }
    }

}