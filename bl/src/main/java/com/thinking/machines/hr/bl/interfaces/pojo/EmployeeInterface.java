package com.thinking.machines.hr.bl.interfaces.pojo;

import com.thinking.machines.enums.*;
import java.math.*;
import java.util.*;

// this is same as EmployeeDTOInterface
public interface EmployeeInterface extends Comparable<EmployeeInterface>, java.io.Serializable {
    public void setEmployeeId(String EmployeeId);

    public String getEmployeeId();

    public void setName(String name);

    public String getName();

    public void setDesignation(DesignationInterface designation);

    public DesignationInterface getDesignation();

    public void setDateOfBirth(Date dateOfBirth);

    public Date getDateOfBirth();

    public void setGender(GENDER gender);

    public char getGender();

    public void setIsIndian(boolean isIndian);

    public boolean getIsIndian();

    public void setBasicSalary(BigDecimal basicSalary);

    public BigDecimal getBasicSalary();

    public void setPANNumber(String panNumber);

    public String getPANNumber();

    public void setAadharCardNumber(String aadharCardNumber);

    public String getAadharCardNumber();
}