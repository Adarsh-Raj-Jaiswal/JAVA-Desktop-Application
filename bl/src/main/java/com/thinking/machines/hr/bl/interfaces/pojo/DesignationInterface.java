package com.thinking.machines.hr.bl.interfaces.pojo;

// this is the interface for designation class of BL, this is same as designationDTOInterface
public interface DesignationInterface extends java.io.Serializable, Comparable<DesignationInterface> {
    public int getCode();

    public void setCode(int code);

    public void setTitle(String title);

    public String getTitle();
}