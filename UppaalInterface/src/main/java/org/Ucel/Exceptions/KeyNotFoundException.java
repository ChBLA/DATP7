package org.Ucel.Exceptions;

public class KeyNotFoundException extends RuntimeException {
    KeyNotFoundException() {
        super();
    }
    public KeyNotFoundException(String msg) {
        super(msg);
    }
}
