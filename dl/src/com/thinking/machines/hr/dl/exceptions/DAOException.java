// 1

package com.thinking.machines.hr.dl.exceptions;

// ye exception class hai for handling exceptions at data layer
public class DAOException extends Exception { // checked exceptions (coz we are inheriting Exception class) means
                                              // programmer have to hadle the exception in try catch block or throw it
                                              // by declaring methods with throws keyword
    public DAOException(String message) { // constructor
        super(message);
    }
}