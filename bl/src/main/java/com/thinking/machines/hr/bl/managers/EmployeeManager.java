package com.thinking.machines.hr.bl.managers;

import com.thinking.machines.hr.bl.interfaces.managers.*;
import com.thinking.machines.hr.bl.interfaces.pojo.*;
import com.thinking.machines.hr.bl.pojo.*;
import com.thinking.machines.hr.bl.exceptions.*;
import com.thinking.machines.enums.*;
import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.dto.*;
import com.thinking.machines.hr.dl.dao.*;
import java.util.*;
import java.math.*;
import java.text.*;

public class EmployeeManager implements EmployeeManagerInterface {
    // these are our data structures pointers
    private Map<String, EmployeeInterface> employeeIdWiseEmployeesMap;// first data structure Map of ( employeeId
                                                                      // and object) pair, to search fast on the basis
                                                                      // of employeeId

    private Map<String, EmployeeInterface> panNumberWiseEmployeesMap;// second data structure Map of ( panNumber
                                                                     // and object) pair, to search fast on the
                                                                     // basis of panNumber

    private Map<String, EmployeeInterface> aadharCardNumberWiseEmployeesMap;// third data structure Map of (aadharNumber
                                                                            // and object) pair, to search fast on
                                                                            // the basis of aadhar Number

    private Set<EmployeeInterface> employeesSet;// fourth data structure Set of objects for orederd list etc

    private Map<Integer, Set<EmployeeInterface>> designationCodeWiseEmployeesMap; // fifth data structure Map of
                                                                                  // (Designation code and Set of
                                                                                  // objects) pair

    // static pointer initialized to null, this will be used to implement the
    // sigleton pattern, if it is null then it means that object is not created yet
    // and we will create object, it is static so that it can be accessed in static
    // method
    private static EmployeeManager employeeManager = null; // pointer

    // private constructor means that creating its object is not allowed, kyoki ham
    // chahte hai ki iska ek hi object bane agar socho 4 objects ban gye to 4 data
    // sturctures ho jayenge pr hamko chahiye ki poori application ki life cycle m
    // iska ek hi object bane (this is called singleton pattern)
    private EmployeeManager() throws BLException { // private constructor
        populateDataStructure(); // invoking populate data structure method
    }

    // this method is used to implement the sigleton pattern and it is static so
    // that it can be accessed without creating object
    public static EmployeeManagerInterface getEmployeeManager() throws BLException {// this method ensures that
                                                                                    // the object is created only
                                                                                    // once in application life
                                                                                    // cycle
        if (employeeManager == null)// if it is null so that means object is not created yet
            employeeManager = new EmployeeManager();// so create object
        return employeeManager;// return object
    }

    // this method will populate(feed data into it) our dataStructures
    private void populateDataStructure() throws BLException {

        this.employeeIdWiseEmployeesMap = new HashMap<>(); // creating hashMaps
        this.panNumberWiseEmployeesMap = new HashMap<>();
        this.aadharCardNumberWiseEmployeesMap = new HashMap<>();

        this.employeesSet = new TreeSet<>(); // creating a TreeSet

        this.designationCodeWiseEmployeesMap = new HashMap<>(); // creating hashMap

        try {
            Set<EmployeeDTOInterface> dlEmployees;
            dlEmployees = new EmployeeDAO().getAll(); // this method will create and return the TreeSet of
                                                      // employeeDTOInterface type objects from the DL

            EmployeeInterface employee; // pointer for employee POJO

            DesignationManagerInterface designationManager; // pointer for Designation Manager object (poore data
                                                            // structures of designation in BL)

            designationManager = DesignationManager.getDesignationManager(); // this method will return the object of
                                                                             // DesignationManager by calling the
                                                                             // private constructor if the object is not
                                                                             // created yet or by returning the address
                                                                             // stored in static pointer if it is
                                                                             // already created, the private constructor
                                                                             // will place a call to the
                                                                             // populateDataStructures method which will
                                                                             // populate the data structures of
                                                                             // Designation Manager

            DesignationInterface designation; // pointer for setting designation into our employee POJO

            Set<EmployeeInterface> ets; // set pointer for designationCodeWiseEmployeesMap

            // iterating int the TreeSet returned by getAll method of EmployeeDAO
            for (EmployeeDTOInterface dlEmployee : dlEmployees) {

                employee = new Employee(); // creating POJO

                // setting employee id and name in POJO
                employee.setEmployeeId(dlEmployee.getEmployeeId());
                employee.setName(dlEmployee.getName());

                designation = designationManager.getDesignationByCode(dlEmployee.getDesignationCode()); // this method
                                                                                                        // has
                                                                                                        // designation
                                                                                                        // code as its
                                                                                                        // parameter and
                                                                                                        // we are
                                                                                                        // passing it by
                                                                                                        // calling the
                                                                                                        // getDesignationCode
                                                                                                        // method of
                                                                                                        // EmployeeDTO
                                                                                                        // and this
                                                                                                        // method will
                                                                                                        // return
                                                                                                        // DesignationInterface
                                                                                                        // type object
                                                                                                        // from
                                                                                                        // codeWiseDesignationMap
                                                                                                        // by calling
                                                                                                        // the get()
                                                                                                        // method and
                                                                                                        // passing this
                                                                                                        // code as
                                                                                                        // parameter

                // setting designation in POJO
                employee.setDesignation(designation);

                // setting date of birth in POJO
                employee.setDateOfBirth((Date) dlEmployee.getDateOfBirth().clone());

                // setting gender in POJO
                if (dlEmployee.getGender() == 'M' || dlEmployee.getGender() == 'm')
                    employee.setGender(GENDER.MALE);
                if (dlEmployee.getGender() == 'F' || dlEmployee.getGender() == 'f')
                    employee.setGender(GENDER.FEMALE);

                // setting isIndian, basic salary, pan number and aadhar number in POJO
                employee.setIsIndian(dlEmployee.getIsIndian());
                employee.setBasicSalary(dlEmployee.getBasicSalary());
                employee.setPANNumber(dlEmployee.getPANNumber());
                employee.setAadharCardNumber(dlEmployee.getAadharCardNumber());

                // not adding this POJO to our data structures
                this.employeeIdWiseEmployeesMap.put(employee.getEmployeeId().toUpperCase(), employee);
                this.panNumberWiseEmployeesMap.put(employee.getPANNumber().toUpperCase(), employee);
                this.aadharCardNumberWiseEmployeesMap.put(employee.getAadharCardNumber().toUpperCase(), employee);
                this.employeesSet.add(employee);

                // getting TreeSet of employees against this designation
                ets = this.designationCodeWiseEmployeesMap.get(designation.getCode());
                if (ets == null) { // checking that if a TreeSet of employees already exist against this
                                   // designation
                    ets = new TreeSet<>(); // if not then create a TreeSet
                    ets.add(employee); // add employee
                    this.designationCodeWiseEmployeesMap.put(designation.getCode(), ets); // put this newly created
                                                                                          // TreeSet on Map
                } else {
                    ets.add(employee); // if already exist then add this employee into TreeSet
                }

            }

        } catch (DAOException daoException) { // throwing exception if any by wrapping it into BLException
            BLException blException = new BLException();
            blException.setGenericException(daoException.getMessage());
            throw blException;
        }

    }

    public void addEmployee(EmployeeInterface employee) throws BLException {

        // creating instance of BLException
        BLException blException = new BLException();

        // extracting properties from parameter object
        String employeeId = employee.getEmployeeId();
        String name = employee.getName();
        DesignationInterface designation = employee.getDesignation();
        int designationCode = 0;
        Date dateOfBirth = employee.getDateOfBirth();
        char gender = employee.getGender();
        boolean isIndian = employee.getIsIndian();
        BigDecimal basicSalary = employee.getBasicSalary();
        String panNumber = employee.getPANNumber();
        String aadharCardNumber = employee.getAadharCardNumber();

        // validations
        if (employeeId != null) {
            employeeId = employeeId.trim();
            if (employeeId.length() > 0) { // emplyeeId should not be set we will generate it
                blException.addException("employeeId", "Employee Id. should be nil/emplty");
            }
        }

        // validating name
        if (name == null) {
            blException.addException("name", "Name required");
        } else {
            name = name.trim();
            if (name.length() == 0)
                blException.addException("name", "Name required");
        }

        // validating designation
        DesignationManagerInterface designationManager; // creating a DesignationManagerInterface type pointer
        designationManager = DesignationManager.getDesignationManager(); // this will create and return the object of
                                                                         // DesignationManager if it is not created yet
                                                                         // by calling its constructor and then the
                                                                         // constructor will place a call to the
                                                                         // populateDataStructure method which will
                                                                         // populate the data structures of
                                                                         // DesignationManager
                                                                         // (codeWiseDesignationMap,titleWiseDesignationMap
                                                                         // and designationSet) by calling the getAll
                                                                         // method of DesignationDAO class of Data Layer
                                                                         // which will return the set of designations
                                                                         // and then cloning this set into the data
                                                                         // Structures

        // validating designation and adding exceptions
        if (designation == null) {
            blException.addException("designation", "Designation required");
        } else {
            designationCode = designation.getCode(); // getting the designation code
            if (designationManager.designationCodeExists(designationCode) == false) { // calling the
                                                                                      // designationCodeExists method of
                                                                                      // DesignationManager which will
                                                                                      // return true after checking that
                                                                                      // the designation code exists by
                                                                                      // calling the containskey method
                                                                                      // of codeWiseDesignationMap and
                                                                                      // passing designation code as
                                                                                      // argument, so if the map
                                                                                      // contains the code then the
                                                                                      // containsKey method will return
                                                                                      // true that means the designation
                                                                                      // exists and then the
                                                                                      // designationCodeExists method
                                                                                      // will return true otherwise it
                                                                                      // will return false

                blException.addException("designation", "Invalid designation"); // adding exception if exists
            }
        }

        // validating date of birth
        if (dateOfBirth == null) {
            blException.addException("dateOfBirth", "Date of birth required");
        }

        // validating gender
        if (gender == ' ') {
            blException.addException("gender", "Gender required");
        }

        // validating basicSalary
        if (basicSalary == null) {
            blException.addException("basicSalary", "Basic salary required");
        } else {
            if (basicSalary.signum() == -1) {
                blException.addException("basicSalary", "Basic salary cannot be negative");
            }
        }

        // validating panNumber
        if (panNumber == null) {
            blException.addException("panNumber", "PAN Number required");
        } else {
            panNumber = panNumber.trim();
            if (panNumber.length() == 0) {
                blException.addException("panNumber", "PAN Number required");
            }
        }
        if (panNumber != null && panNumber.length() > 0) { // checking if panNumer already exists
            if (this.panNumberWiseEmployeesMap.containsKey(panNumber.toUpperCase())) {
                blException.addException("panNumber", "PAN Number " + panNumber + " exists");
            }
        }

        // validating aadhar Number
        if (aadharCardNumber == null) {
            blException.addException("aadharCardNumber", "Aadhar-Card Number required");
        } else {
            aadharCardNumber = aadharCardNumber.trim();
            if (aadharCardNumber.length() == 0) {
                blException.addException("aadharCardNumber", "Aadhar-Card Number required");
            }
        }
        if (aadharCardNumber != null && aadharCardNumber.length() > 0) { // checking if aadhar number already exists
            if (this.aadharCardNumberWiseEmployeesMap.containsKey(aadharCardNumber.toUpperCase())) {
                blException.addException("aadharCardNumber", "Aadhar-Card Number " + aadharCardNumber + " exists");
            }
        }

        // raising exception if any
        if (blException.hasExceptions()) {
            throw blException;
        }

        try {
            // { data layer ka kaam
            EmployeeDAOInterface employeeDAO;
            employeeDAO = new EmployeeDAO(); // creating object of employeeDAO

            EmployeeDTOInterface dlEmployee;
            dlEmployee = new EmployeeDTO(); // creating object of employeeDTO

            // setting values into our object(DTO)
            dlEmployee.setName(name);
            dlEmployee.setDesignationCode(designation.getCode());
            dlEmployee.setDateOfBirth((Date) dateOfBirth.clone());
            dlEmployee.setGender((gender == 'M') ? GENDER.MALE : GENDER.FEMALE);
            dlEmployee.setBasicSalary(basicSalary);
            dlEmployee.setIsIndian(isIndian);
            dlEmployee.setPANNumber(panNumber);
            dlEmployee.setAadharCardNumber(aadharCardNumber);

            // adding our object (DTO)
            employeeDAO.add(dlEmployee);
            // }

            // for presentation layer
            employee.setEmployeeId(dlEmployee.getEmployeeId()); // setting employeeId in our parameter object

            EmployeeInterface dsEmployee = new Employee(); // creating POJO

            // setting employeeId and name in POJO
            dsEmployee.setEmployeeId(employee.getEmployeeId());
            dsEmployee.setName(name);

            // setting designation in POJO
            dsEmployee.setDesignation(

                    // here typecasting the interface pointer(designationManager) to
                    // DesignationManager class type pointer because interface ka pointer
                    // getDSDesignationByCode method ko nhi pehchanta hai kyoki ye interface m
                    // declared nhi hai ye method hamne baadme internal use k liye
                    // DesignationManager class m add kri thi
                    ((DesignationManager) designationManager).getDSDesignationByCode(designation.getCode()));
            // hamne ye method isliye call kri hai kyoki hmko apne hi data structure mai
            // apne hi designation ka clone nhi rakhna tha ab kyoki ye method actual object
            // ka address return krti hai naaki koi clone banake deti hai to isse hamari ye
            // problem solve ho gyi

            //

            //

            // setting gender, date of birth, basic salary, isIndian, pan number and aadhar
            // number in POJO
            dsEmployee.setGender((gender == 'M' || gender == 'm') ? GENDER.MALE : GENDER.FEMALE);
            dsEmployee.setDateOfBirth((Date) dateOfBirth.clone()); // calling the clone method of object class which
                                                                   // will clone date of birth and return the cloned
                                                                   // object its return type is of Object so we need to
                                                                   // typecast it into Date
            dsEmployee.setBasicSalary(basicSalary);
            dsEmployee.setIsIndian(isIndian);
            dsEmployee.setPANNumber(panNumber);
            dsEmployee.setAadharCardNumber(aadharCardNumber);

            // adding POJO to our data structures
            this.employeesSet.add(dsEmployee);
            this.employeeIdWiseEmployeesMap.put(dsEmployee.getEmployeeId().toUpperCase(), dsEmployee);
            this.panNumberWiseEmployeesMap.put(panNumber.toUpperCase(), dsEmployee);
            this.aadharCardNumberWiseEmployeesMap.put(aadharCardNumber.toUpperCase(), dsEmployee);

            // creating pointer
            Set<EmployeeInterface> ets;
            ets = this.designationCodeWiseEmployeesMap.get(dsEmployee.getDesignation().getCode()); // getting the
                                                                                                   // TreeSet of
                                                                                                   // employees aginst
                                                                                                   // this designation

            if (ets == null) { // if it is null then it means no employee is alloted this designation yet
                ets = new TreeSet<>(); // creating TreeSet
                ets.add(dsEmployee); // adding this employee into TreeSet
                this.designationCodeWiseEmployeesMap.put(dsEmployee.getDesignation().getCode(), ets); // putting this
                                                                                                      // TreeSet in Map
                                                                                                      // agianst
                                                                                                      // designation
                                                                                                      // code
            } else {
                ets.add(dsEmployee); // if TreeSet exist then add this employee
            }

        } catch (DAOException daoException) {
            blException.setGenericException("daoException.getMessage()"); // throwing generic exception
            throw blException;
        }

    }

    public void updateEmployee(EmployeeInterface employee) throws BLException {

        // creating instance of BLException
        BLException blException = new BLException();

        // extracting properties from parameter object
        String employeeId = employee.getEmployeeId();
        String name = employee.getName();
        DesignationInterface designation = employee.getDesignation();
        int designationCode = 0;
        Date dateOfBirth = employee.getDateOfBirth();
        char gender = employee.getGender();
        boolean isIndian = employee.getIsIndian();
        BigDecimal basicSalary = employee.getBasicSalary();
        String panNumber = employee.getPANNumber();
        String aadharCardNumber = employee.getAadharCardNumber();

        // validating employee Id
        if (employeeId == null) {
            blException.addException("employeeId", "Employee Id. required");
        } else {
            employeeId = employeeId.trim();
            if (employeeId.length() == 0) {
                blException.addException("employeeId", "Employee Id. required");
            } else {
                if (this.employeeIdWiseEmployeesMap.containsKey(employeeId.toUpperCase()) == false) { // employee id
                                                                                                      // should exist to
                                                                                                      // be updated
                    blException.addException("employeeId", "Invalid employee Id." + employeeId);
                    throw blException;
                }
            }
        }

        // validating name
        if (name == null) {
            blException.addException("name", "Name required");
        } else {
            name = name.trim();
            if (name.length() == 0)
                blException.addException("name", "Name required");
        }

        // validating designation
        DesignationManagerInterface designationManager;
        designationManager = DesignationManager.getDesignationManager();
        if (designation == null) {
            blException.addException("designation", "Designation required");
        } else {
            designationCode = designation.getCode();
            if (designationManager.designationCodeExists(designationCode) == false) { // given designation should exist
                blException.addException("designation", "Invalid designation");
            }
        }

        // validating date of birth
        if (dateOfBirth == null) {
            blException.addException("dateOfBirth", "Date of birth required");
        }

        // validating gender
        if (gender == ' ') {
            blException.addException("gender", "Gender required");
        }

        // validating basic salary
        if (basicSalary == null) {
            blException.addException("basicSalary", "Basic salary required");
        } else {
            if (basicSalary.signum() == -1) {
                blException.addException("basicSalary", "Basic salary cannot be negative");
            }
        }

        // validating pan number
        if (panNumber == null) {
            blException.addException("panNumber", "PAN Number required");
        } else {
            panNumber = panNumber.trim();
            if (panNumber.length() == 0) {
                blException.addException("panNumber", "PAN Number required");
            }
        }

        // validating aadhar number
        if (aadharCardNumber == null) {
            blException.addException("aadharCardNumber", "Aadhar-Card Number required");
        } else {
            aadharCardNumber = aadharCardNumber.trim();
            if (aadharCardNumber.length() == 0) {
                blException.addException("aadharCardNumber", "Aadhar-Card Number required");
            }
        }

        // checking that the pan number already exist against any other employee
        if (panNumber != null && panNumber.length() > 0) {
            EmployeeInterface ee = this.panNumberWiseEmployeesMap.get(panNumber.toUpperCase());
            if (ee != null && ee.getEmployeeId().equalsIgnoreCase(employeeId) == false) {
                blException.addException("panNumber", "PAN Number " + panNumber + " exists");
            }
        }

        // checking that the aadhar number already exist against any other employee
        if (aadharCardNumber != null && aadharCardNumber.length() > 0) {
            EmployeeInterface ee = this.aadharCardNumberWiseEmployeesMap.get(aadharCardNumber.toUpperCase());
            if (ee != null && ee.getEmployeeId().equalsIgnoreCase(employeeId) == false) {
                blException.addException("aadharCardNumber", "Aadhar-Card Number " + aadharCardNumber + " exists");
            }
        }

        // throwing exception if any
        if (blException.hasExceptions()) {
            throw blException;
        }
        try {

            EmployeeInterface dsEmployee;
            dsEmployee = employeeIdWiseEmployeesMap.get(employeeId.toUpperCase()); // getting the employee object to be
                                                                                   // updated from our data structure

            // taking pan number, aadhar number and designation code from this object so
            // that after udating these values we will be able to remove them from the data
            // structures based on their old values
            String oldPANNumber = dsEmployee.getPANNumber();
            String oldAadharCardNumber = dsEmployee.getAadharCardNumber();
            int oldDesignationCode = dsEmployee.getDesignation().getCode();

            // { updation on data layer
            EmployeeDAOInterface employeeDAO;
            employeeDAO = new EmployeeDAO(); // creating the object of EmployeeDAO

            EmployeeDTOInterface dlEmployee;
            dlEmployee = new EmployeeDTO(); // creating the object of EmployeeDTO

            // feeding this newly created object with updated properties
            dlEmployee.setEmployeeId(dsEmployee.getEmployeeId());
            dlEmployee.setName(name);
            dlEmployee.setDesignationCode(designation.getCode());
            dlEmployee.setDateOfBirth((Date) dateOfBirth.clone());
            dlEmployee.setGender((gender == 'M') ? GENDER.MALE : GENDER.FEMALE);
            dlEmployee.setBasicSalary(basicSalary);
            dlEmployee.setIsIndian(isIndian);
            dlEmployee.setPANNumber(panNumber);
            dlEmployee.setAadharCardNumber(aadharCardNumber);

            // calling the update method of employeeDAO and passing this newly creted
            // updated object
            employeeDAO.update(dlEmployee);
            // }

            // updating the data structure object
            dsEmployee.setName(name);
            dsEmployee.setDesignation(
                    ((DesignationManager) designationManager).getDSDesignationByCode(designation.getCode()));
            dsEmployee.setGender((gender == 'M') ? GENDER.MALE : GENDER.FEMALE);
            dsEmployee.setBasicSalary(basicSalary);
            dsEmployee.setDateOfBirth((Date) dateOfBirth.clone());
            dsEmployee.setIsIndian(isIndian);
            dsEmployee.setPANNumber(panNumber);
            dsEmployee.setAadharCardNumber(aadharCardNumber);

            // removing old object from our data structures
            this.employeesSet.remove(dsEmployee);
            this.employeeIdWiseEmployeesMap.remove(employeeId.toUpperCase());
            this.panNumberWiseEmployeesMap.remove(oldPANNumber.toUpperCase());
            this.aadharCardNumberWiseEmployeesMap.remove(oldAadharCardNumber.toUpperCase());

            // adding updated object to our data structures
            this.employeesSet.add(dsEmployee);
            this.employeeIdWiseEmployeesMap.put(dsEmployee.getEmployeeId().toUpperCase(), dsEmployee);
            this.panNumberWiseEmployeesMap.put(panNumber.toUpperCase(), dsEmployee);
            this.aadharCardNumberWiseEmployeesMap.put(aadharCardNumber.toUpperCase(), dsEmployee);

            // if designation has changed then we need to update the
            // designationCodeWiseEmployeesMap data structure
            if (oldDesignationCode != dsEmployee.getDesignation().getCode()) {

                Set<EmployeeInterface> ets; // set pointer

                // removing the employee from TreeSet against old designation code
                ets = this.designationCodeWiseEmployeesMap.get(oldDesignationCode);
                ets.remove(dsEmployee);

                // adding the employee with new designation code
                ets = this.designationCodeWiseEmployeesMap.get(dsEmployee.getDesignation().getCode());
                if (ets == null) { // if any TreeSet doesn't exist against this designation code then create and
                                   // add this employee
                    ets = new TreeSet<>();
                    ets.add(dsEmployee);
                    this.designationCodeWiseEmployeesMap.put(dsEmployee.getDesignation().getCode(), ets); // putting
                                                                                                          // this
                                                                                                          // TreeSet
                                                                                                          // against
                                                                                                          // designation
                                                                                                          // code
                } else {
                    ets.add(dsEmployee); // adding the employee to existing TreeSet
                }
            }

        } catch (DAOException daoException) {
            blException.setGenericException("daoException.getMessage()");
            throw blException;
        }
    }

    public void removeEmployee(String employeeId) throws BLException {

        // validating employee Id
        if (employeeId == null) {
            BLException blException = new BLException();
            blException.addException("employeeId", "Employee Id. required");
            throw blException;
        } else {
            employeeId = employeeId.trim();
            if (employeeId.length() == 0) {
                BLException blException = new BLException();
                blException.addException("employeeId", "Employee Id. required");
                throw blException;
            } else {
                if (this.employeeIdWiseEmployeesMap.containsKey(employeeId.toUpperCase()) == false) { // employee Id
                                                                                                      // should exist
                    BLException blException = new BLException();
                    blException.addException("employeeId", "Invalid employee Id." + employeeId);
                    throw blException;
                }
            }
        }

        try {
            EmployeeInterface dsEmployee;
            dsEmployee = employeeIdWiseEmployeesMap.get(employeeId.toUpperCase()); // getting the object to be removed

            EmployeeDAOInterface employeeDAO;
            employeeDAO = new EmployeeDAO(); // creating object of EmployeeDAO

            employeeDAO.delete(dsEmployee.getEmployeeId());// calling the delete method of employeeDAO and deleting this
                                                           // employee from data layer

            // removing employee from dataStructure
            this.employeesSet.remove(dsEmployee);
            this.employeeIdWiseEmployeesMap.remove(dsEmployee.getEmployeeId().toUpperCase());
            this.panNumberWiseEmployeesMap.remove(dsEmployee.getPANNumber().toUpperCase());
            this.aadharCardNumberWiseEmployeesMap.remove(dsEmployee.getAadharCardNumber().toUpperCase());

            // removing this employee from designationCodeWiseEmployeesMap
            Set<EmployeeInterface> ets;
            ets = this.designationCodeWiseEmployeesMap.get(dsEmployee.getDesignation().getCode());
            ets.remove(dsEmployee);

        } catch (DAOException daoException) {
            BLException blException = new BLException();
            blException.setGenericException("daoException.getMessage()");
            throw blException;
        }
    }

    public EmployeeInterface getEmployeeByEmployeeId(String employeeId) throws BLException {

        // getting the object from the data structure on the basis of employee Id
        EmployeeInterface dsEmployee = this.employeeIdWiseEmployeesMap.get(employeeId.trim().toUpperCase());
        if (dsEmployee == null) { // if not found if will return null which means employee Id is invalid
            BLException blException = new BLException();
            blException.addException("employeeId", "Invalid employee Id. : " + employeeId);
            throw blException;
        }

        EmployeeInterface employee = new Employee(); // creating new Employee object(POJO)

        // setting values into this object (cloning)
        employee.setEmployeeId(dsEmployee.getEmployeeId());
        employee.setName(dsEmployee.getName());

        DesignationInterface designation = new Designation();
        designation.setCode(dsEmployee.getDesignation().getCode());
        designation.setTitle(dsEmployee.getDesignation().getTitle());
        employee.setDesignation(designation);

        employee.setDateOfBirth((Date) dsEmployee.getDateOfBirth().clone());
        employee.setGender((dsEmployee.getGender() == 'M') ? GENDER.MALE : GENDER.FEMALE);
        employee.setIsIndian(dsEmployee.getIsIndian());
        employee.setBasicSalary(dsEmployee.getBasicSalary());
        employee.setPANNumber(dsEmployee.getPANNumber());
        employee.setAadharCardNumber(dsEmployee.getAadharCardNumber());

        // returning the object
        return employee;
    }

    public EmployeeInterface getEmployeeByPANNumber(String panNumber) throws BLException {

        // getting the object from the data structure on the basis of pan number
        EmployeeInterface dsEmployee = this.panNumberWiseEmployeesMap.get(panNumber.trim().toUpperCase());
        if (dsEmployee == null) { // if not found if will return null which means pan number is invalid
            BLException blException = new BLException();
            blException.addException("panNumber", "Invalid PAN number : " + panNumber);
            throw blException;
        }

        EmployeeInterface employee = new Employee(); // creating new Employee object(POJO)

        // setting values into this object (cloning)
        employee.setEmployeeId(dsEmployee.getEmployeeId());
        employee.setName(dsEmployee.getName());

        DesignationInterface designation = new Designation();
        designation.setCode(dsEmployee.getDesignation().getCode());
        designation.setTitle(dsEmployee.getDesignation().getTitle());
        employee.setDesignation(designation);

        employee.setDateOfBirth((Date) dsEmployee.getDateOfBirth().clone());
        employee.setGender((dsEmployee.getGender() == 'M') ? GENDER.MALE : GENDER.FEMALE);
        employee.setIsIndian(dsEmployee.getIsIndian());
        employee.setBasicSalary(dsEmployee.getBasicSalary());
        employee.setPANNumber(dsEmployee.getPANNumber());
        employee.setAadharCardNumber(dsEmployee.getAadharCardNumber());

        // returning the object
        return employee;
    }

    public EmployeeInterface getEmployeeByAadharCardNumber(String aadharCardNumber) throws BLException {

        // getting the object from the data structure on the basis of aadhar number
        EmployeeInterface dsEmployee = this.panNumberWiseEmployeesMap.get(aadharCardNumber.trim().toUpperCase());
        if (dsEmployee == null) { // if not found if will return null which means aadhar number is invalid
            BLException blException = new BLException();
            blException.addException("aadharCardNumber", "Invalid Aadhar-Card number : " + aadharCardNumber);
            throw blException;
        }

        EmployeeInterface employee = new Employee(); // creating new Employee object(POJO)

        // setting values into this object (cloning)
        employee.setEmployeeId(dsEmployee.getEmployeeId());
        employee.setName(dsEmployee.getName());

        DesignationInterface designation = new Designation();
        designation.setCode(dsEmployee.getDesignation().getCode());
        designation.setTitle(dsEmployee.getDesignation().getTitle());
        employee.setDesignation(designation);

        employee.setDateOfBirth((Date) dsEmployee.getDateOfBirth().clone());
        employee.setGender((dsEmployee.getGender() == 'M') ? GENDER.MALE : GENDER.FEMALE);
        employee.setIsIndian(dsEmployee.getIsIndian());
        employee.setBasicSalary(dsEmployee.getBasicSalary());
        employee.setPANNumber(dsEmployee.getPANNumber());
        employee.setAadharCardNumber(dsEmployee.getAadharCardNumber());

        // returning the object
        return employee;
    }

    // return the total number of employees
    public int getEmployeeCount() {
        return this.employeesSet.size();
    }

    // returns true if employee exists with given employeeId
    public boolean employeeIdExists(String employeeId) {
        return this.employeeIdWiseEmployeesMap.containsKey(employeeId);
    }

    // returns true if employee exists with given pan number
    public boolean employeePANNumberExists(String panNumber) {
        return this.panNumberWiseEmployeesMap.containsKey(panNumber);
    }

    // returns true if employee exists with given aadhar number
    public boolean employeeAadharCardNumberExists(String aadharCardNumber) {
        return this.aadharCardNumberWiseEmployeesMap.containsKey(aadharCardNumber);
    }

    public Set<EmployeeInterface> getEmployees() {

        Set<EmployeeInterface> employees = new TreeSet<>(); // creating a new TreeSet of EmployeeInterface type objects

        EmployeeInterface employee;
        DesignationInterface designation;

        for (EmployeeInterface dsEmployee : this.employeesSet) { // iterating through our empoyeesSet data structure

            employee = new Employee(); // creating new object

            // cloning the new object
            employee.setEmployeeId(dsEmployee.getEmployeeId());
            employee.setName(dsEmployee.getName());

            designation = new Designation();
            designation.setCode(dsEmployee.getDesignation().getCode());
            designation.setTitle(dsEmployee.getDesignation().getTitle());
            employee.setDesignation(designation);

            employee.setDateOfBirth((Date) dsEmployee.getDateOfBirth().clone());
            employee.setGender((dsEmployee.getGender() == 'M') ? GENDER.MALE : GENDER.FEMALE);
            employee.setIsIndian(dsEmployee.getIsIndian());
            employee.setBasicSalary(dsEmployee.getBasicSalary());
            employee.setPANNumber(dsEmployee.getPANNumber());
            employee.setAadharCardNumber(dsEmployee.getAadharCardNumber());

            // adding our new object
            employees.add(employee);
        }

        // returning the TreeSet
        return employees;
    }

    public Set<EmployeeInterface> getEmployeesByDesignationCode(int designationCode) throws BLException {

        // creating designation manager
        DesignationManagerInterface designationManager;
        designationManager = DesignationManager.getDesignationManager();

        // validating designation code
        if (designationManager.designationCodeExists(designationCode) == false) { // designation should exist
            BLException blException = new BLException();
            blException.setGenericException("Invalid designation code " + designationCode);
            throw blException;
        }

        // creating TreeSet
        Set<EmployeeInterface> employees = new TreeSet<>();

        Set<EmployeeInterface> ets;
        ets = this.designationCodeWiseEmployeesMap.get(designationCode);
        if (ets == null) {
            return employees; // returning empty TreeSet if there are no employees against this designation
                              // code
        }

        EmployeeInterface employee;
        DesignationInterface designation;

        // iterating in the TreeSet of employees against designation code from
        // designationCodeWiseEmployeesMap
        for (EmployeeInterface dsEmployee : ets) {

            employee = new Employee(); // creating employee object

            // setting values into object
            employee.setEmployeeId(dsEmployee.getEmployeeId());
            employee.setName(dsEmployee.getName());

            designation = new Designation();
            designation.setCode(dsEmployee.getDesignation().getCode());
            designation.setTitle(dsEmployee.getDesignation().getTitle());
            employee.setDesignation(designation);

            employee.setDateOfBirth((Date) dsEmployee.getDateOfBirth().clone());
            employee.setGender((dsEmployee.getGender() == 'M') ? GENDER.MALE : GENDER.FEMALE);
            employee.setIsIndian(dsEmployee.getIsIndian());
            employee.setBasicSalary(dsEmployee.getBasicSalary());
            employee.setPANNumber(dsEmployee.getPANNumber());
            employee.setAadharCardNumber(dsEmployee.getAadharCardNumber());

            employees.add(employee); // adding this employee to TreeSet
        }

        return employees; // returning TreeSet
    }

    public int getEmployeeCountByDesignationCode(int designationCode) throws BLException {
        Set<EmployeeInterface> ets; // creating pointer
        ets = this.designationCodeWiseEmployeesMap.get(designationCode);
        if (ets == null) // if it is null then it means that this designation is not alloted to any
                         // employee
            return 0;
        return ets.size(); // returning size of the TreeSet
    }

    public boolean designationAlloted(int designationCode) throws BLException {
        return this.designationCodeWiseEmployeesMap.containsKey(designationCode); // if it contains this designation
                                                                                  // code as key then it means that this
                                                                                  // designation is alloted to any
                                                                                  // employee
    }

}