// 8

package com.thinking.machines.hr.dl.interfaces.dao;

// ye interface batata hai ki DAO(data access object) class kaisi hogi
import java.util.*;
import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;

public interface EmployeeDAOInterface { // various methods in EmployeeDAO
    public void add(EmployeeDTOInterface employeeDTO) throws DAOException;

    public void update(EmployeeDTOInterface employeeDTO) throws DAOException;

    public void delete(String employeeId) throws DAOException;

    public Set<EmployeeDTOInterface> getAll() throws DAOException;

    public Set<EmployeeDTOInterface> getByDesignationCode(int designationCode) throws DAOException;// returns employees with same designation

    public boolean isDesignationAlloted(int designationCode) throws DAOException; // checks that if this designation is alloted to any employee or not

    public EmployeeDTOInterface getByEmployeeId(String employeeId) throws DAOException;

    public EmployeeDTOInterface getByPANNumber(String panNumber) throws DAOException;

    public EmployeeDTOInterface getByAadharCardNumber(String aadharCardNumber) throws DAOException;

    public boolean employeeIdExists(String employeeId) throws DAOException;

    public boolean panNumberExists(String panNumber) throws DAOException;

    public boolean aadharCardNumberExists(String aadharCardNumber) throws DAOException;

    public int getCount() throws DAOException;

    public int getCountByDesignation(int designationCode) throws DAOException; // returns the count of employees against the given designation
}