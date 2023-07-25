// 3

package com.thinking.machines.hr.dl.dto;

import com.thinking.machines.hr.dl.interfaces.dto.*;

// ye DTO class hai isse designation ka data transfer object banega
public class DesignationDTO implements DesignationDTOInterface {
    // code and title both are unique and code is our primary key
    private int code;
    private String title;

    public DesignationDTO() { // constructor
        this.code = 0;
        this.title = "";
    }

    // { -> these methods are declared in DesignationDTOInterface
    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setTitle(java.lang.String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
    // }

    // equals method of Object class
    public boolean equals(Object other) {
        if (!(other instanceof DesignationDTOInterface)) // if it is not of type DesignationDTOInterface return false
            return false;
        DesignationDTOInterface designationDTO;
        designationDTO = (DesignationDTOInterface) other;
        return this.code == designationDTO.getCode(); // comparing using code as primary key
    }

    // we have to overrite the compareTo functions because it is declared in
    // Comparable interface which is implemented by DesignationDTOInterface which we
    // are implementing
    public int compareTo(DesignationDTOInterface designationDTO) {
        return this.code - designationDTO.getCode();
    }

    // hashCode method of Object class
    public int hashCode() {
        return this.code;
    }
    // we are writing the equals and hashCode methods because the collection classes
    // uses them and we need data structures from the collections into our project
}