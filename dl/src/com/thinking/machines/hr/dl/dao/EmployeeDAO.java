// 9

package com.thinking.machines.hr.dl.dao;

import java.io.*;
import java.util.*;
import java.math.*;
import java.text.*;
import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.dto.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.enums.*;

public class EmployeeDAO implements EmployeeDAOInterface {
    private static final String FILE_NAME = "employee.data";

    public void add(EmployeeDTOInterface employeeDTO) throws DAOException {
        /*
         * 1) check duplicacy(panNumber,aadharCardNumber) -> if exist raise
         * exception also check if designationCode is correct
         * 2) generate employeeId -> we will generate a unique employeeId user can't choose it on
         * its own
         * 3) add record to file
         * 4) set generated employeeId into the object
         */

        // validations
        if (employeeDTO == null)
            throw new DAOException("Employee is null");

        String employeeId;
        String name;
        int designationCode;
        Date dateOfBirth;
        char gender;
        boolean isIndian;
        BigDecimal basicSalary;
        String panNumber;
        String aadharCardNumber;

        // validating name
        name = employeeDTO.getName();
        if (name == null || name.trim().length() == 0)
            throw new DAOException("Employee Name is null");
        name = name.trim();

        // validating designationCode
        designationCode = employeeDTO.getDesignationCode();
        DesignationDAOInterface designationDAO = new DesignationDAO();
        if (designationCode <= 0 || !designationDAO.codeExists(designationCode)) // checking that the code exist or not
            throw new DAOException("Invalid Designation code");

        // validating Date of Birth
        dateOfBirth = employeeDTO.getDateOfBirth();
        if (dateOfBirth == null)
            throw new DAOException("Date of birth is null");

        gender = employeeDTO.getGender();
        if (gender == ' ')
            throw new DAOException("Gender not set to Male/Female");

        isIndian = employeeDTO.getIsIndian();

        // validating basic salary
        basicSalary = employeeDTO.getBasicSalary();
        if (basicSalary == null)
            throw new DAOException("Basic Salary is null");
        if (basicSalary.signum() == -1) // this method returns -1 if basicSalary is negative
            throw new DAOException("Basic Salary is negative");

        // validating pan number
        panNumber = employeeDTO.getPANNumber();
        if (panNumber == null || panNumber.trim().length() == 0)
            throw new DAOException("Employee PanNumber is null");
        panNumber = panNumber.trim();

        // validating aadhar number
        aadharCardNumber = employeeDTO.getAadharCardNumber();
        if (aadharCardNumber == null || aadharCardNumber.trim().length() == 0)
            throw new DAOException("Employee AadharCardNumber is null");
        aadharCardNumber = aadharCardNumber.trim();

        try {
            File file = new File(FILE_NAME);
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            int lastGeneratedEmployeeId = 10000000;
            int recordCount = 0;
            String lastGeneratedEmployeeIdString, recordCountString;

            // if file is empty then we will write the header
            if (randomAccessFile.length() == 0) {
                lastGeneratedEmployeeIdString = String.format("%-10s", "10000000");// 10s -> this will put spaces upto
                                                                                   // 10
                randomAccessFile.writeBytes(lastGeneratedEmployeeIdString + "\n");
                recordCountString = String.format("%-10s", "0");
                randomAccessFile.writeBytes(recordCountString + "\n");
                // here %-10s is equivalent to using while loop and then appending spaces after
                // employee id and record count

            } else { // if it is not then we will read the header
                lastGeneratedEmployeeIdString = randomAccessFile.readLine().trim();
                recordCountString = randomAccessFile.readLine().trim();
                lastGeneratedEmployeeId = Integer.parseInt(lastGeneratedEmployeeIdString);
                recordCount = Integer.parseInt(recordCountString);
            }

            // checking for pan number and aadhar number already exist
            boolean panNumberExists = false, aadharCardNumberExists = false;
            String fEmployeeId, fPanNumber, fAadharCardNumber;
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                for (int x = 1; x <= 7; x++)// panNumber and aadhar number are 8th and 9th feild in our file, so we are
                                            // reading first 7 feilds to reach to them using a for loop
                {
                    randomAccessFile.readLine();
                }
                // now reading pan and aadhar
                fPanNumber = randomAccessFile.readLine();
                fAadharCardNumber = randomAccessFile.readLine();

                // checking pan number
                if (panNumberExists == false && fPanNumber.equalsIgnoreCase(panNumber)) {
                    panNumberExists = true;
                }

                // checking aadhar number
                if (aadharCardNumberExists == false && fAadharCardNumber.equalsIgnoreCase(aadharCardNumber)) {
                    aadharCardNumberExists = true;
                }

                // if any of one already exist then break
                if (panNumberExists && aadharCardNumberExists)
                    break;
            }

            // throwing exceptions if they exist
            if (panNumberExists && aadharCardNumberExists) {
                randomAccessFile.close();
                throw new DAOException(
                        "PAN number (" + panNumber + ") and Aadhar card number (" + aadharCardNumber + ") exists");
            }
            if (panNumberExists) {
                randomAccessFile.close();
                throw new DAOException("PAN number (" + panNumber + ") exists");
            }
            if (aadharCardNumberExists) {
                randomAccessFile.close();
                throw new DAOException("Aadhar card number (" + aadharCardNumber + ") exists");
            }

            // if we are here then it means aadhar and pan dosen't exist and all feilds are
            // validated

            lastGeneratedEmployeeId++; // incrementing the employee id
            employeeId = "A" + lastGeneratedEmployeeId; // lastGeneratedEmployeeId will be
                                                        // converted into string and
            // then will concatenated with A
            recordCount++; // incrementing record count

            // writing emplyeeId, name and designationCode
            randomAccessFile.writeBytes(employeeId + "\n");
            randomAccessFile.writeBytes(name + "\n");
            randomAccessFile.writeBytes(designationCode + "\n");

            // writing Date of Birth
            SimpleDateFormat simpleDateFormat;
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy"); // this will create object of SimpleDateFormat with
                                                                   // format as dd/MM/yyyy
            randomAccessFile.writeBytes(simpleDateFormat.format(dateOfBirth) + "\n"); // .format() method accepts Date
                                                                                      // type ojbect and will return the
                                                                                      // string in the format specified
                                                                                      // above

            // writing gender and isIndian
            randomAccessFile.writeBytes(gender + "\n");
            randomAccessFile.writeBytes(isIndian + "\n");

            // writing basicSalary , pan and aadhar
            randomAccessFile.writeBytes(basicSalary.toPlainString() + "\n");
            randomAccessFile.writeBytes(panNumber + "\n");
            randomAccessFile.writeBytes(aadharCardNumber + "\n");

            // updating header
            randomAccessFile.seek(0);
            lastGeneratedEmployeeIdString = String.format("%-10d", lastGeneratedEmployeeId);// here using %-10d because
                                                                                            // lastGeneratedEmployeeId
                                                                                            // is integer
            recordCountString = String.format("%-10d", recordCount);

            // writing updated header
            randomAccessFile.writeBytes(lastGeneratedEmployeeIdString + "\n");
            randomAccessFile.writeBytes(recordCountString + "\n");
            randomAccessFile.close();

            // setting the employeeId into DTO
            employeeDTO.setEmployeeId(employeeId);
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public void update(EmployeeDTOInterface employeeDTO) throws DAOException {
        // validations
        if (employeeDTO == null) // validating employeeDTO object
            throw new DAOException("Employee is null");

        // validating employeeId in object
        String employeeId = employeeDTO.getEmployeeId();
        if (employeeId == null)
            throw new DAOException("Employee Id. is null");
        employeeId = employeeId.trim();
        if (employeeId.length() == 0)
            throw new DAOException("Length of Employee Id. is zero");

        // validating name in object
        String name = employeeDTO.getName();
        if (name == null)
            throw new DAOException("Employee is null");
        name = name.trim();
        if (name.length() == 0)
            throw new DAOException("Length of Employee name is zero");

        // validating designationCode
        int designationCode = employeeDTO.getDesignationCode();
        if (designationCode <= 0)
            throw new DAOException("Invalid designation code");

        DesignationDAOInterface designationDAO;
        designationDAO = new DesignationDAO();
        if (!designationDAO.codeExists(designationCode)) // checking that designationCode exists
            throw new DAOException("Invalid designation code");

        // validating Date of birth
        Date dateOfBirth = employeeDTO.getDateOfBirth();
        if (dateOfBirth == null)
            throw new DAOException("Employee date of date of birth is null");

        // validating gender
        char gender = employeeDTO.getGender();
        if (gender == ' ')
            throw new DAOException("Gender not set to Male/Female");

        boolean isIndian = employeeDTO.getIsIndian();

        // validationg basicSalary
        BigDecimal basicSalary = employeeDTO.getBasicSalary();
        if (basicSalary == null)
            throw new DAOException("Employee basic salary is null");
        if (basicSalary.signum() == -1) // checking if it is not negative
            throw new DAOException("Employee basic salary is negative");

        // validating panNumber
        String panNumber = employeeDTO.getPANNumber();
        if (panNumber == null)
            throw new DAOException("Employee PAN number is null");
        panNumber = panNumber.trim();
        if (panNumber.length() == 0)
            throw new DAOException("Length of Employee PAN number is zero");

        // validating aadhar number
        String aadharCardNumber = employeeDTO.getAadharCardNumber();
        if (aadharCardNumber == null)
            throw new DAOException("Employee is null");
        aadharCardNumber = aadharCardNumber.trim();
        if (aadharCardNumber.length() == 0)
            throw new DAOException("Length of Employee Aadhar-card number is zero");

        // done object's validations

        try {
            // file not found or found empty then raise exception
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                throw new DAOException("Invalid employee Id. :" + employeeId);
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid employee Id. :" + employeeId);
            }

            // readign header
            randomAccessFile.readLine();
            randomAccessFile.readLine();

            // making variables
            SimpleDateFormat simpleDateFormat;
            simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            String fEmployeeId = "";
            String fName = "";
            int fDesignationCode;
            Date fDateOfBirth = null;
            GENDER fGender;
            boolean fIsIndian;
            BigDecimal fBasicSalary;
            String fPANNumber = "";
            String fAadharCardNumber = "";
            int x;
            boolean employeeIdFound = false;
            boolean panNumberFound = false;
            boolean aadharCardNumberFound = false;
            String panNumberFoundAgainstEmployeeId = "";
            String aadharCardNumberFoundAgainstEmployeeId = "";
            long foundAt = 0;

            // looping through the file
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                // if employeeId is not found yet then we will mark this position
                if (employeeIdFound == false)
                    foundAt = randomAccessFile.getFilePointer();

                fEmployeeId = randomAccessFile.readLine(); // reading employeeId

                for (x = 1; x <= 6; x++) // skipping 6 feilds
                                         // (name,designationCode,dateofbirth,gender,isIndian,basicSalary)
                {
                    randomAccessFile.readLine();
                }

                fPANNumber = randomAccessFile.readLine(); // reading pan number
                fAadharCardNumber = randomAccessFile.readLine(); // reading aadahar number

                // if we are here then we have readed a whole record

                // if employeeId is not found yet then we will check that this record's
                // employeeId is equal to the
                // employeeId which we are searching for and then mark employeeIdFound to true
                // otherwise we will not check
                // this simpy means check only once if found stop checking
                if (employeeIdFound == false && fEmployeeId.equalsIgnoreCase(employeeId)) {
                    employeeIdFound = true;
                }

                // searching for panNumber and if found set panNumberfound to true and mark the
                // emplooyeId against panNumber
                if (panNumberFound == false && fPANNumber.equalsIgnoreCase(panNumber)) {
                    panNumberFound = true;
                    panNumberFoundAgainstEmployeeId = fEmployeeId; // storing the employeeId of this record so as to
                                                                   // know that this pan number matched in this file on
                                                                   // which employeeId
                }

                // searching for aadharNumber and if found set aadharNumberfound to true and
                // mark the
                // emplooyeId against aadharNumber
                if (aadharCardNumberFound == false && fAadharCardNumber.equalsIgnoreCase(aadharCardNumber)) {
                    aadharCardNumberFound = true;
                    aadharCardNumberFoundAgainstEmployeeId = fEmployeeId; // storing the employeeId of this record so as
                                                                          // to khow that this aadhar number matched in
                                                                          // this file on which employeeId
                }

                // if employeeId , pan number and aadhar number all are found then break no need
                // to search ahead
                if (employeeIdFound && panNumberFound && aadharCardNumberFound)
                    break;
            }

            // if we are here and employeeId still not found then raise exception
            if (employeeIdFound == false) {
                randomAccessFile.close();
                throw new DAOException("Invalid employee Id. : " + employeeId);
            }

            boolean panNumberExists = false;
            if (panNumberFound && panNumberFoundAgainstEmployeeId.equalsIgnoreCase(employeeId) == false) {
                // this means that panNumber is found but it is found on any other employeeId,
                // so we will set that panNumber already exists
                panNumberExists = true;
            }

            boolean aadharCardNumberExists = false;
            if (aadharCardNumberFound && aadharCardNumberFoundAgainstEmployeeId.equalsIgnoreCase(employeeId) == false) {
                // this means that aadharNumber is found but it is found on any other
                // employeeId, so we will set that aadharNumber already exists
                aadharCardNumberExists = true;
            }

            // if panNUmber and aadhar number both already exists in any other record then
            // throw exception
            if (panNumberExists && aadharCardNumberExists) {
                randomAccessFile.close();
                throw new DAOException("Employee with PAN number : " + panNumber + " and Aadhar-card number : "
                        + aadharCardNumber + " already exists.");
            }

            // if panNumber already exist in any other record then throw exception
            if (panNumberExists) {
                randomAccessFile.close();
                throw new DAOException("Employee with PAN number : " + panNumber + " already exists.");
            }

            // if aadharNumber already exist in any other record then throw exception
            if (aadharCardNumberExists) {
                randomAccessFile.close();
                throw new DAOException("Employee with Aadhar-card number : " + aadharCardNumber + " already exists.");
            }

            // we were doing all this checks because we have three unique feilds in our DTO
            // so we were checking that
            // 1) employee id exist -> coz it is our primary key and we will update the
            // record on its basis
            // 2) checking that panNumber should not exist on any other record so as to
            // maintain its uniqueness
            // 3) checking that aadharNumber should not exist on any other record so as to
            // maintain uiqueness

            // if we are here then all the validations are done and we are good to go for
            // updations
            randomAccessFile.seek(foundAt); // pointing the file pointer at the starting position of the record in which
                                            // our employeeId(primary key) matched e.i the record that we want to update

            for (x = 1; x <= 9; x++) // skipping this record which is to be updated
            {
                randomAccessFile.readLine();
            }

            // creating a temporary file
            File tmpFile = new File("tmp.tmp");
            if (tmpFile.exists())
                tmpFile.delete();

            RandomAccessFile tmpRandomAccessFile;
            tmpRandomAccessFile = new RandomAccessFile(tmpFile, "rw");

            // copying all the records after that record which is to be updated in temp file
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                tmpRandomAccessFile.writeBytes(randomAccessFile.readLine() + "\n");
            }

            randomAccessFile.seek(foundAt);// again pointing the pointer on the starting position of our record to be
                                           // updated

            // updating the record
            randomAccessFile.writeBytes(employeeId + "\n");
            randomAccessFile.writeBytes(name + "\n");
            randomAccessFile.writeBytes(designationCode + "\n");
            randomAccessFile.writeBytes(simpleDateFormat.format(dateOfBirth) + "\n");
            randomAccessFile.writeBytes(gender + "\n");
            randomAccessFile.writeBytes(isIndian + "\n");
            randomAccessFile.writeBytes(basicSalary.toPlainString() + "\n");
            randomAccessFile.writeBytes(panNumber + "\n");
            randomAccessFile.writeBytes(aadharCardNumber + "\n");

            // now writing the records which we previosly copied
            tmpRandomAccessFile.seek(0);
            while (tmpRandomAccessFile.getFilePointer() < tmpRandomAccessFile.length()) {
                randomAccessFile.writeBytes(tmpRandomAccessFile.readLine() + "\n");
            }

            // now updating the lengths
            randomAccessFile.setLength(randomAccessFile.getFilePointer());
            tmpRandomAccessFile.setLength(0);
            randomAccessFile.close();
            tmpRandomAccessFile.close();
            // closing files and we are done :)

        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public void delete(String employeeId) throws DAOException {
        // validations
        if (employeeId == null)
            throw new DAOException("Employee Id. is null");
        employeeId = employeeId.trim();
        if (employeeId.length() == 0)
            throw new DAOException("Length of Employee Id. is zero");

        try {

            // file not found or found empty so raise exception
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                throw new DAOException("Invalid employee Id. :" + employeeId);
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid employee Id. :" + employeeId);
            }

            // reading lastGeneratedEmployeeId
            randomAccessFile.readLine();
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());

            String fEmployeeId = "";
            int x;
            boolean employeeIdFound = false;
            long foundAt = 0;

            // looping through the file and searching and marking the starting position of
            // our record to be deleted
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                foundAt = randomAccessFile.getFilePointer(); // marking the position

                fEmployeeId = randomAccessFile.readLine();

                for (x = 1; x <= 8; x++) { // skipping the 8 feilds
                    randomAccessFile.readLine();
                }

                if (fEmployeeId.equalsIgnoreCase(employeeId)) { // if empoyeeId matched then set found to true and break
                    employeeIdFound = true;
                    break;
                }
            }

            // if employeeId not matched with any of the record then raise exception
            if (employeeIdFound == false) {
                randomAccessFile.close();
                throw new DAOException("Invalid employee Id. : " + employeeId);
            }

            // creating a temporary file
            File tmpFile = new File("tmp.tmp");
            if (tmpFile.exists())
                tmpFile.delete();

            RandomAccessFile tmpRandomAccessFile;
            tmpRandomAccessFile = new RandomAccessFile(tmpFile, "rw");

            // copying all the data after our record to be deleted in temp file
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                tmpRandomAccessFile.writeBytes(randomAccessFile.readLine() + "\n");
            }

            // setting our main file pointer to the starting position of our record to be
            // deleted
            randomAccessFile.seek(foundAt);

            // pointing the temp file pointer to start
            tmpRandomAccessFile.seek(0);

            // copying our data back and overriting the data to be deleted
            while (tmpRandomAccessFile.getFilePointer() < tmpRandomAccessFile.length()) {
                randomAccessFile.writeBytes(tmpRandomAccessFile.readLine() + "\n");
            }

            // setting lenghts
            randomAccessFile.setLength(randomAccessFile.getFilePointer());

            // decreasing the record count
            recordCount--;
            String recordCountString = String.format("%-10d", recordCount);

            // pointing on the start
            randomAccessFile.seek(0);

            // reading lastGeneratedEmployeeId
            randomAccessFile.readLine();

            // updating recordCount
            randomAccessFile.writeBytes(recordCountString + "\n");

            // closing the files
            randomAccessFile.close();
            tmpRandomAccessFile.setLength(0);
            tmpRandomAccessFile.close();

        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public Set<EmployeeDTOInterface> getAll() throws DAOException {
        // creating a TreeSet
        Set<EmployeeDTOInterface> employees = new TreeSet<>();

        try {
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return employees; // returning empty TreeSet if file not found

            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return employees; // returning empty TreeSet if file is empty
            }

            // reading header
            randomAccessFile.readLine();
            randomAccessFile.readLine();

            EmployeeDTOInterface employeeDTO; // pointer
            char fGender;
            // looping throught the file creating objects putting values in objects and then
            // adding them into TreeSet
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                employeeDTO = new EmployeeDTO(); // creating object

                SimpleDateFormat simpleDateFormat;
                simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
                // setting attributes into object
                employeeDTO.setEmployeeId(randomAccessFile.readLine());
                employeeDTO.setName(randomAccessFile.readLine());
                employeeDTO.setDesignationCode(Integer.parseInt(randomAccessFile.readLine()));

                try {
                    employeeDTO.setDateOfBirth(simpleDateFormat.parse(randomAccessFile.readLine()));
                } catch (ParseException pe) {
                    randomAccessFile.close();
                    throw new DAOException(pe.getMessage());
                }

                fGender = randomAccessFile.readLine().charAt(0);
                if (fGender == 'm' || fGender == 'M')
                    employeeDTO.setGender(GENDER.MALE);
                if (fGender == 'f' || fGender == 'F')
                    employeeDTO.setGender(GENDER.FEMALE);

                employeeDTO.setIsIndian(Boolean.parseBoolean(randomAccessFile.readLine()));
                employeeDTO.setBasicSalary(new BigDecimal(randomAccessFile.readLine()));
                employeeDTO.setPANNumber(randomAccessFile.readLine());
                employeeDTO.setAadharCardNumber(randomAccessFile.readLine());

                // adding this object into TreeSet
                employees.add(employeeDTO);
            }

            // closing file and returning the TreeSet
            randomAccessFile.close();
            return employees;
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public Set<EmployeeDTOInterface> getByDesignationCode(int designationCode) throws DAOException {

        // validations
        if (new DesignationDAO().codeExists(designationCode) == false) {
            throw new DAOException("Invalid desigantion code : " + designationCode);
        }
        // creating TreeSet
        Set<EmployeeDTOInterface> employees = new TreeSet<>();

        try {

            // returning empty TreeSet if file not found or found empty
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return employees;
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return employees;
            }

            // reading header
            randomAccessFile.readLine();
            randomAccessFile.readLine();

            EmployeeDTOInterface employeeDTO; // pointer
            String fEmployeeId;
            String fName;
            int fDesignationCode;
            int x;
            char fGender;

            // looping through the file and creating the TreeSet
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fEmployeeId = randomAccessFile.readLine();
                fName = randomAccessFile.readLine();

                // extracting designationCode
                fDesignationCode = Integer.parseInt(randomAccessFile.readLine());

                // if the employee is not of the given designation e.i its designation code
                // doesn't matches then skip it
                if (fDesignationCode != designationCode) {
                    for (x = 1; x <= 6; x++) // skipping this data using for loop and starting again from the start by
                                             // continue statement
                        randomAccessFile.readLine();
                    continue;
                }

                // if we are here then it means all validations are done and designationCode
                // also matched
                employeeDTO = new EmployeeDTO(); // creating object
                SimpleDateFormat simpleDateFormat;
                simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

                // now feeding values into object
                employeeDTO.setEmployeeId(fEmployeeId);
                employeeDTO.setName(fName);
                employeeDTO.setDesignationCode(fDesignationCode);

                try {
                    employeeDTO.setDateOfBirth(simpleDateFormat.parse(randomAccessFile.readLine()));
                } catch (ParseException pe) {
                    throw new DAOException(pe.getMessage());
                }

                fGender = randomAccessFile.readLine().charAt(0);
                if (fGender == 'm' || fGender == 'M')
                    employeeDTO.setGender(GENDER.MALE);
                if (fGender == 'f' || fGender == 'F')
                    employeeDTO.setGender(GENDER.FEMALE);

                employeeDTO.setIsIndian(Boolean.parseBoolean(randomAccessFile.readLine()));
                employeeDTO.setBasicSalary(new BigDecimal(randomAccessFile.readLine()));
                employeeDTO.setPANNumber(randomAccessFile.readLine());
                employeeDTO.setAadharCardNumber(randomAccessFile.readLine());

                // adding the created object into TreeSet
                employees.add(employeeDTO);
            }

            // closing the file and returning the TreeSet
            randomAccessFile.close();
            return employees;
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public boolean isDesignationAlloted(int designationCode) throws DAOException {

        // validations
        if (new DesignationDAO().codeExists(designationCode) == false) { // creating DesignationDAO's object so as to
                                                                         // check that this code exist for any
                                                                         // designation or not
            throw new DAOException("Invalid designation code : " + designationCode);
        }

        try {

            // returning false if file not found or found empty
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return false;
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return false;
            }

            // reading header
            randomAccessFile.readLine();
            randomAccessFile.readLine();

            int fDesignationCode;
            // looping
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {

                // reading employeeId and name
                randomAccessFile.readLine();
                randomAccessFile.readLine();

                fDesignationCode = Integer.parseInt(randomAccessFile.readLine());
                if (fDesignationCode == designationCode) {
                    randomAccessFile.close(); // if designation code matches then it means that this designation is
                                              // alloted to a employee, so closing the file and returning true
                    return true;
                }

                for (int z = 1; z <= 6; z++) // using this for loop we are skipping the next 6 feilds
                                             // (dateOfBirth,gender,isIndian,basicSalary,panNumber,aadharNumber)
                    randomAccessFile.readLine();
            }

            // if we are here then it means the code doesn't matched with any employee so
            // not alloted, closing the file and returning false
            randomAccessFile.close();
            return false;

        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public EmployeeDTOInterface getByEmployeeId(String employeeId) throws DAOException {

        // validations
        if (employeeId == null || employeeId.trim().length() == 0)
            throw new DAOException("Invalid Employee ID  : length is zero ");
        employeeId = employeeId.trim();

        try {
            File file = new File(FILE_NAME);
            // if file not found or found empty then throw exception
            if (file.exists() == false)
                throw new DAOException("Invalid Employee ID : " + employeeId);
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid Employee ID : " + employeeId);
            }

            randomAccessFile.readLine(); // reading lastGeneratedEmployeeId
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) { // if no records then throw exception
                randomAccessFile.close();
                throw new DAOException("Invalid Employee ID : " + employeeId);
            }

            EmployeeDTOInterface employeeDTO; // pointer
            String fEmployeeId;
            SimpleDateFormat simpleDateFormat;
            simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            // looping and searching for employeeId
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fEmployeeId = randomAccessFile.readLine();

                // if found then creating object and putting values into it
                if (fEmployeeId.equalsIgnoreCase(employeeId)) {
                    employeeDTO = new EmployeeDTO(); // creating object

                    employeeDTO.setEmployeeId(fEmployeeId);
                    employeeDTO.setName(randomAccessFile.readLine());
                    employeeDTO.setDesignationCode(Integer.parseInt(randomAccessFile.readLine()));

                    try {
                        employeeDTO.setDateOfBirth(simpleDateFormat.parse(randomAccessFile.readLine()));
                    } catch (ParseException pe) {
                        new DAOException(pe.getMessage());
                    }

                    employeeDTO.setGender((randomAccessFile.readLine().charAt(0) == 'M') ? GENDER.MALE : GENDER.FEMALE);
                    employeeDTO.setIsIndian(Boolean.parseBoolean(randomAccessFile.readLine()));
                    employeeDTO.setBasicSalary(new BigDecimal(randomAccessFile.readLine()));
                    employeeDTO.setPANNumber(randomAccessFile.readLine());
                    employeeDTO.setAadharCardNumber(randomAccessFile.readLine());
                    randomAccessFile.close();
                    return employeeDTO; // closing file and retuning the object
                }
                for (int x = 1; x <= 8; x++)
                    randomAccessFile.readLine(); // skipping the next 8 feilds
            }
            // if we are here this means employeeId doesn't matched so closing file and
            // throwing exception
            randomAccessFile.close();
            throw new DAOException("Invalid Employee ID : " + employeeId);
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public EmployeeDTOInterface getByPANNumber(String panNumber) throws DAOException {

        // validations
        if (panNumber == null || panNumber.trim().length() == 0)
            throw new DAOException("Invalid PAN number  : length is zero ");
        panNumber = panNumber.trim();

        try {

            // file not found or found empty throwing exception
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                throw new DAOException("Invalid PAN number : " + panNumber);
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid PAN number : " + panNumber);
            }

            randomAccessFile.readLine(); // reading lastGeneratedEmployeeId
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) { // zero records throwing exception
                randomAccessFile.close();
                throw new DAOException("Invalid PAN number : " + panNumber);
            }

            EmployeeDTOInterface employeeDTO;
            String fEmployeeId;
            String fName;
            int fDesignationCode;
            Date fDateOfBirth = null;
            GENDER fGender;
            boolean fIsIndian;
            BigDecimal fBasicSalary;
            String fPANNumber;
            String fAadharCardNumber;
            SimpleDateFormat simpleDateFormat;
            simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

            // looping and searching
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                // reading attributes
                fEmployeeId = randomAccessFile.readLine();
                fName = randomAccessFile.readLine();
                fDesignationCode = Integer.parseInt(randomAccessFile.readLine());

                try {
                    fDateOfBirth = simpleDateFormat.parse(randomAccessFile.readLine());
                } catch (ParseException pe) {
                    new DAOException(pe.getMessage());
                }

                fGender = (randomAccessFile.readLine().charAt(0) == 'M') ? GENDER.MALE : GENDER.FEMALE;
                fIsIndian = Boolean.parseBoolean(randomAccessFile.readLine());
                fBasicSalary = new BigDecimal(randomAccessFile.readLine());
                fPANNumber = randomAccessFile.readLine();
                fAadharCardNumber = randomAccessFile.readLine();
                // checking that the panNumber matches or not if matches then setting attributes
                // into object
                if (fPANNumber.equalsIgnoreCase(panNumber)) {
                    employeeDTO = new EmployeeDTO(); // creating object

                    // setting attributes
                    employeeDTO.setEmployeeId(fEmployeeId);
                    employeeDTO.setName(fName);
                    employeeDTO.setDesignationCode(fDesignationCode);
                    employeeDTO.setDateOfBirth(fDateOfBirth);
                    employeeDTO.setGender(fGender);
                    employeeDTO.setIsIndian(fIsIndian);
                    employeeDTO.setBasicSalary(fBasicSalary);
                    employeeDTO.setPANNumber(fPANNumber);
                    employeeDTO.setAadharCardNumber(fAadharCardNumber);
                    randomAccessFile.close();
                    return employeeDTO; // closing file and returning object
                }
            }

            // if we are here then no matches for the given panNumber, so closing the file
            // and raising exception
            randomAccessFile.close();
            throw new DAOException("Invalid PAN number : " + panNumber);

        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public EmployeeDTOInterface getByAadharCardNumber(String aadharCardNumber) throws DAOException {

        // validations
        if (aadharCardNumber == null || aadharCardNumber.trim().length() == 0)
            throw new DAOException("Invalid Aadhar-card number  : length is zero ");
        aadharCardNumber = aadharCardNumber.trim();

        try {
            // file not found or found empty so raise exception
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                throw new DAOException("Invalid Aadhar-card number : " + aadharCardNumber);
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid Aadhar-card number : " + aadharCardNumber);
            }

            // no records so raise exception
            randomAccessFile.readLine();
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid Aadhar-card number : " + aadharCardNumber);
            }

            // variables
            EmployeeDTOInterface employeeDTO;
            String fEmployeeId;
            String fName;
            int fDesignationCode;
            Date fDateOfBirth = null;
            GENDER fGender;
            boolean fIsIndian;
            BigDecimal fBasicSalary;
            String fPANNumber;
            String fAadharCardNumber;
            SimpleDateFormat simpleDateFormat;
            simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

            // looping and searching
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                // reading the attributes and assigning them in variables
                fEmployeeId = randomAccessFile.readLine();
                fName = randomAccessFile.readLine();
                fDesignationCode = Integer.parseInt(randomAccessFile.readLine());

                try {
                    fDateOfBirth = simpleDateFormat.parse(randomAccessFile.readLine());
                } catch (ParseException pe) {
                    new DAOException(pe.getMessage());
                }

                fGender = (randomAccessFile.readLine().charAt(0) == 'M') ? GENDER.MALE : GENDER.FEMALE;
                fIsIndian = Boolean.parseBoolean(randomAccessFile.readLine());
                fBasicSalary = new BigDecimal(randomAccessFile.readLine());
                fPANNumber = randomAccessFile.readLine();
                fAadharCardNumber = randomAccessFile.readLine();

                // if aadharCardNumber matches then create object and return
                if (fAadharCardNumber.equalsIgnoreCase(aadharCardNumber)) {
                    employeeDTO = new EmployeeDTO(); // creating object

                    // assigning values in the object
                    employeeDTO.setEmployeeId(fEmployeeId);
                    employeeDTO.setName(fName);
                    employeeDTO.setDesignationCode(fDesignationCode);
                    employeeDTO.setDateOfBirth(fDateOfBirth);
                    employeeDTO.setGender(fGender);
                    employeeDTO.setIsIndian(fIsIndian);
                    employeeDTO.setBasicSalary(fBasicSalary);
                    employeeDTO.setPANNumber(fPANNumber);
                    employeeDTO.setAadharCardNumber(fAadharCardNumber);
                    randomAccessFile.close();
                    return employeeDTO; // closing the file and returning the object
                }
            }

            // if we are here then it means aadhar number not found so close the file and
            // raise exception
            randomAccessFile.close();
            throw new DAOException("Invalid Aadhar-card number : " + aadharCardNumber);

        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public boolean employeeIdExists(String employeeId) throws DAOException {
        // validations
        if (employeeId == null || employeeId.trim().length() == 0)
            return false;
        employeeId = employeeId.trim();

        try {

            // if file not found or found empty then return false
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return false;
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return false;
            }

            // reding lastgeneratedemployeeid
            randomAccessFile.readLine();
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) { // no records then return false
                randomAccessFile.close();
                return false;
            }

            String fEmployeeId;

            // looping and searching
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fEmployeeId = randomAccessFile.readLine();
                if (fEmployeeId.equalsIgnoreCase(employeeId)) {
                    randomAccessFile.close(); // if found close the file and return true
                    return true;
                }
                for (int x = 1; x <= 8; x++) // skipping the next 8 feilds
                    randomAccessFile.readLine();
            }

            // if we are here then our search is complete and employeeId not found so
            // closing the file and returning false
            randomAccessFile.close();
            return false;
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public boolean panNumberExists(String panNumber) throws DAOException {

        // validations
        if (panNumber == null || panNumber.trim().length() == 0)
            return false;
        panNumber = panNumber.trim();
        try {

            // file not found or found empty return false
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return false;
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return false;
            }

            // record count is 0 then return false
            randomAccessFile.readLine();
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) {
                randomAccessFile.close();
                return false;
            }

            // looping and checking if the pan number exists
            String fPANNumber;
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                for (int x = 1; x <= 7; x++) // skipping 7 feilds
                {
                    randomAccessFile.readLine();
                }
                fPANNumber = randomAccessFile.readLine(); // reading pan number
                randomAccessFile.readLine(); // reading aadhar number
                if (fPANNumber.equalsIgnoreCase(panNumber)) {
                    randomAccessFile.close(); // if found close file and return true
                    return true;
                }
            }

            // if we are here it means pan number not found so closing file and returning
            // false
            randomAccessFile.close();
            return false;

        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public boolean aadharCardNumberExists(String aadharCardNumber) throws DAOException {

        // validations
        if (aadharCardNumber == null || aadharCardNumber.trim().length() == 0)
            return false;
        aadharCardNumber = aadharCardNumber.trim();

        try {

            // file not found or found empty return false
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return false;
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return false;
            }

            // if no records return false
            randomAccessFile.readLine();
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) {
                randomAccessFile.close();
                return false;
            }

            String fAadharCardNumber;
            // looping and searching
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                for (int x = 1; x <= 8; x++) // skipping 8 feilds
                {
                    randomAccessFile.readLine();
                }
                fAadharCardNumber = randomAccessFile.readLine();
                if (fAadharCardNumber.equalsIgnoreCase(aadharCardNumber)) {
                    randomAccessFile.close(); // if matched then close file and return true
                    return true;
                }
            }

            // not matched so close file and return false
            randomAccessFile.close();
            return false;

        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public int getCount() throws DAOException {
        try {

            // file not found or found empty then return 0
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return 0;
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return 0;
            }

            randomAccessFile.readLine(); // reading lastGeneratedEmployeeId
            int recordCount;
            recordCount = Integer.parseInt(randomAccessFile.readLine().trim()); // reading record count and trimming it
                                                                                // before parsing coz it may contain
                                                                                // extra spaces
            randomAccessFile.close();
            return recordCount; // closing the file and returning recordCount

        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public int getCountByDesignation(int designationCode) throws DAOException {
        {
            // checking that this designationCode exist agaist a designation
            if (new DesignationDAO().codeExists(designationCode) == false) {
                throw new DAOException("Invalid designation code : " + designationCode); // throwing exception
            }

            try {

                // file not found or found empty then return 0
                File file = new File(FILE_NAME);
                if (file.exists() == false)
                    return 0;
                RandomAccessFile randomAccessFile;
                randomAccessFile = new RandomAccessFile(file, "rw");
                if (randomAccessFile.length() == 0) {
                    randomAccessFile.close();
                    return 0;
                }

                // reading header
                randomAccessFile.readLine();
                randomAccessFile.readLine();

                int fDesignationCode;
                int designationCodeCount = 0;
                // looping and counting
                while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                    // reading employeeId and name
                    randomAccessFile.readLine();
                    randomAccessFile.readLine();
                    // reading designationCode
                    fDesignationCode = Integer.parseInt(randomAccessFile.readLine());
                    if (fDesignationCode == designationCode) {
                        designationCodeCount++; // if match then increment the count
                    }

                    for (int z = 1; z <= 6; z++) // skipping the next 6 feilds
                    {
                        randomAccessFile.readLine();
                    }
                }

                // closing the file and returning count
                randomAccessFile.close();
                return designationCodeCount;

            } catch (IOException ioException) {
                throw new DAOException(ioException.getMessage());
            }
        }
    }
}