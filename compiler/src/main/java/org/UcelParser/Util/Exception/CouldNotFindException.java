package org.UcelParser.Util.Exception;

import org.UcelParser.Util.DeclarationReference;

public class CouldNotFindException extends Exception {
    public CouldNotFindException(String msg) {
        super(msg);
    }

    public CouldNotFindException(DeclarationReference reference) {
        super(String.format("Could not find reference %s within %s scopes", reference.getDeclarationId(), reference.getRelativeScope()));
    }
}
