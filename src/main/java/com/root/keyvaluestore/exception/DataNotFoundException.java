package com.root.keyvaluestore.exception;

/*
 * Exception for 404(NOT FOUND) status code
 */
public class DataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8610515627242678848L;
    
    public DataNotFoundException(String message) {
        super(message);
    }

}
