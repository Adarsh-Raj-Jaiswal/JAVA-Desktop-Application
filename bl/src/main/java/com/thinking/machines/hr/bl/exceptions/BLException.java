package com.thinking.machines.hr.bl.exceptions;

import java.util.*;

// this is the exception class to handle the exceptions at BL level
public class BLException extends Exception { // inheriting Exception class e.i checked exceptions -> have to handle

    // map of property name and exception
    private Map<String, String> exceptions;

    // variable for generic exception
    private String genericException;

    // constructor
    public BLException() {
        genericException = null; // also by defalut it is null
        exceptions = new HashMap<>();
    }

    // setting a generic exception
    public void setGenericException(String genericException) {
        this.genericException = genericException;
    }

    // getting generic exception, if null return empty string
    public String getGenericException() {
        if (this.genericException == null)
            return "";
        return this.genericException;
    }

    // adding exceptions into our map against a property
    public void addException(String property, String exception) {
        this.exceptions.put(property, exception);
    }

    // getting exception from our map against a property
    public String getException(String property) {
        return this.exceptions.get(property); // it will return null if not found
    }

    // removing the exception against the property
    public void removeException(String property) {
        this.exceptions.remove(property);
    }

    // this will return the count of total exceptions raised
    public int getExceptionsCount() {
        if (this.genericException != null)
            return this.exceptions.size() + 1; // count of exceptions inside the map plus a generic exception
        return this.exceptions.size(); // if no generic exception
    }

    // checking that is there any exception against this property
    public boolean hasException(String property) {
        return this.exceptions.containsKey(property);
    }

    // checking that is there any generic exception
    public boolean hasGenericException() {
        return this.genericException != null;
    }

    // checking that is there any exceptions
    public boolean hasExceptions() {
        return this.getExceptionsCount() > 0;
    }

    // this will return the list of properties which have exceptions
    public List<String> getProperties() {
        List<String> properties = new ArrayList<>(); // creating a list
        this.exceptions.forEach((key, value) -> { // looping through the map
            properties.add(key); // adding key(property) from map into our list
        });
        return properties; // returning list
    }

    // agr koi bani banai getMessage method (java.lang.Throwable.getMessage) call
    // kre to isiliye apn usko likh dete hai
    // returns generic exception
    public String getMessage() {
        if (this.genericException == null)
            return "";
        return this.genericException;
    }

}// class ends