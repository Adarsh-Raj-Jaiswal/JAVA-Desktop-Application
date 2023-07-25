// 2

package com.thinking.machines.hr.dl.interfaces.dto;
// ye interface batata hai ki Designation ka DTO(data transfer object) kaisa hoga 
public interface DesignationDTOInterface extends Comparable<DesignationDTOInterface>, java.io.Serializable {// comparable -> you have to write the compareTo method // serializable is a markup interface, markup interface -> no methods have been declared in it
    public void setCode(int code);

    public int getCode();

    public void setTitle(String title);

    public String getTitle();
}