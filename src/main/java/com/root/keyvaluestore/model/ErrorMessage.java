package com.root.keyvaluestore.model;

/*
 * POJO for error message. This error message will be used to notify client what went wrong
 */
public class ErrorMessage {
    
    private int errorCode;
    
    private String errorMessage;
    
    public ErrorMessage() {
        
    }
        
    public ErrorMessage(final int errorCode, final String errorMessage) {
        super();
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
