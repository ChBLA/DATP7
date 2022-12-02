package org.UcelParser.Util.Exception;

import org.UcelParser.Util.Scope;

public class NoParentScopeException extends Exception {
    public NoParentScopeException(Scope scope) {
        super("Scope: " + scope.toString() + " has no parent");
    }
}
