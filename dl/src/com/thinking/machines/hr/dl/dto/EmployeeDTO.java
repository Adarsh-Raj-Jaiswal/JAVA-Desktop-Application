// 7

package com.thinking.machines.hr.dl.dto;

import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.enums.*;
import java.math.*;
import java.util.*;

// ye DTO class hai isse Employee entity ka data transfer object banega
public class EmployeeDTO implements EmployeeDTOInterface {
    // employee Id is our primary key
    private String employeeId;
    private String name;
    private int designationCode;
    private Date dateOfBirth;
    private char gender;
    private boolean isIndian;
    private BigDecimal basicSalary;
    private String panNumber; // pan number is unique
    private String aadharCardNumber; // aadhar number is unique

    public EmployeeDTO() { // constructor
        this.employeeId = "";
        this.name = "";
        this.designationCode = 0;
        this.dateOfBirth = null;
        this.gender = ' ';
        this.isIndian = false;
        this.basicSalary = null;
        this.panNumber = "";
        this.aadharCardNumber = "";
    }

    // { -> these methods are declared in EmployeeDTOInterface
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDesignationCode(int code) {
        this.designationCode = code;
    }

    public int getDesignationCode() {
        return this.designationCode;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setGender(GENDER gender) {
        if (gender == GENDER.MALE)
            this.gender = 'M';
        else
            this.gender = 'F';
    }

    public char getGender() {
        return this.gender;
    }

    public void setIsIndian(boolean isIndian) {
        this.isIndian = isIndian;
    }

    public boolean getIsIndian() {
        return this.isIndian;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getBasicSalary() {
        return this.basicSalary;
    }

    public void setPANNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getPANNumber() {
        return this.panNumber;
    }

    public void setAadharCardNumber(String aadharCardNumber) {
        this.aadharCardNumber = aadharCardNumber;
    }

    public String getAadharCardNumber() {
        return this.aadharCardNumber;
    }
    // }

    // equals method of Object class
    public boolean equals(Object other) {
        if (!(other instanceof EmployeeDTOInterface))// if it is not of type EmployeeDTOInterface return false
            return false;
        EmployeeDTOInterface employeeDTO = (EmployeeDTOInterface) other;
        return this.employeeId.equalsIgnoreCase(employeeDTO.getEmployeeId()); // comparing using employeeId as primary key
    }

    // we have to overrite the compareTo functions because it is declared in
    // Comparable interface which is implemented by EmployeeDTOInterface which we
    // are implementing
    public int compareTo(EmployeeDTOInterface other) {
        return this.employeeId.compareToIgnoreCase(other.getEmployeeId());
    }

    // hashcode method of Object class
    public int hashCode() {
        return this.employeeId.toUpperCase().hashCode();
    }
}