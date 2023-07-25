// 5
package com.thinking.machines.hr.dl.dao;

import com.thinking.machines.hr.dl.dto.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.exceptions.*;
import java.util.*;
import java.io.*;

// implementation of all the methods declared in DesignationDAOInterface
public class DesignationDAO implements DesignationDAOInterface {
    private static final String FILE_NAME = "designation.data"; // so that we don't have to change file name everywhere

    public void add(DesignationDTOInterface designationDTO) throws DAOException {
        /*
         * 1) check duplicacy(title) -> if exist raise exception
         * 2) generate code -> we will generate a unique code user can't choose it on
         * its own
         * 3) add record to file
         * 4) set generated code into the object
         */

        // validations
        if (designationDTO == null)
            throw new DAOException("Designation is null");
        String title = designationDTO.getTitle();
        if (title == null)
            throw new DAOException("Designation is null");
        title = title.trim();
        if (title.length() == 0)
            throw new DAOException("Length of Designation is zero");

        // try catch due to File can generate IOException
        try {
            // initializations
            File file = new File(FILE_NAME);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            int lastGeneratedCode = 0;
            int recordCount = 0;
            String lastGeneratedCodeString = "";
            String recordCountString = "";

            // if it is a new file
            if (randomAccessFile.length() == 0) {
                lastGeneratedCodeString = "0";
                // putting 0 as last generated code and then filling the other reserved blocks
                // by spaces
                while (lastGeneratedCodeString.length() < 10)
                    lastGeneratedCodeString += " ";
                // here lastGeneratedCodeString is "0_________" _ means space
                recordCountString = "0";
                // putting 0 as total records and then filling the other reserved blocks by
                // spaces
                while (recordCountString.length() < 10)
                    recordCountString += " ";
                // here recordCountString is "0_________" _ means space

                // writing the header section into the file, in the header we have first 10
                // bytes for last generated code and second 10 bytes for record count, we use \n
                // as seperator
                randomAccessFile.writeBytes(lastGeneratedCodeString); // writing first part of header
                randomAccessFile.writeBytes("\n");// separator
                randomAccessFile.writeBytes(recordCountString);// second part of header
                randomAccessFile.writeBytes("\n");// separator
            } else { // file already exists and have some data and header
                lastGeneratedCodeString = randomAccessFile.readLine().trim(); // trimming to avoid spaces which we have
                                                                              // used before to fill reserved blocks
                recordCountString = randomAccessFile.readLine().trim();
                lastGeneratedCode = Integer.parseInt(lastGeneratedCodeString); // getting last generated code by parsing
                                                                               // string to int
                recordCount = Integer.parseInt(recordCountString);
            }

            int fCode;
            String fTitle;
            // now we are on the next byte after the end of header
            // looping through the file to check if the title already exists
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fCode = Integer.parseInt(randomAccessFile.readLine()); // reading code from the file
                fTitle = randomAccessFile.readLine(); // reading title from the file

                if (fTitle.equalsIgnoreCase(title)) {
                    // if the title already exitst then close the file and inform that it already
                    // exists
                    randomAccessFile.close();
                    throw new DAOException("Designation " + title + " exists.");
                }
            }

            // if control comes here then it means that the title is not present
            int code = lastGeneratedCode + 1; // creating code for designation
            randomAccessFile.writeBytes(String.valueOf(code)); // writing code into the file
            randomAccessFile.writeBytes("\n");// separator
            randomAccessFile.writeBytes(title);// writing title into the file
            randomAccessFile.writeBytes("\n");// separator

            designationDTO.setCode(code); // setting the code into the object as well
            lastGeneratedCode = code; // updating last generated code
            recordCount++; // increasing record count

            // creating last generated code string to update in header
            lastGeneratedCodeString = String.valueOf(lastGeneratedCode); // converting int to String
            while (lastGeneratedCodeString.length() < 10)
                lastGeneratedCodeString += " ";
            // again setting blank space to fill reserved blocks in header
            recordCountString = String.valueOf(recordCount);
            while (recordCountString.length() < 10)
                recordCountString += " ";

            // setting the internal pointer to the start by .seek() function
            randomAccessFile.seek(0);
            randomAccessFile.writeBytes(lastGeneratedCodeString); // updating header
            randomAccessFile.writeBytes("\n");// separator
            randomAccessFile.writeBytes(recordCountString);// updating header
            randomAccessFile.writeBytes("\n");// separator
            randomAccessFile.close(); // close the file

        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage()); // throwing the exception by wrapping it in DAOException
        }
    }

    public void update(DesignationDTOInterface designationDTO) throws DAOException {
        // validations
        if (designationDTO == null)
            throw new DAOException("Designation is null");
        int code = designationDTO.getCode();
        if (code <= 0)
            throw new DAOException("Invalid code : " + code);
        String title = designationDTO.getTitle();
        if (title == null)
            throw new DAOException("Designation is null");
        title = title.trim();
        if (title.length() == 0)
            throw new DAOException("Length of designation is zero");

        try {
            File file = new File(FILE_NAME);
            if (!file.exists())
                throw new DAOException("Invalid code : " + code);
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid code : " + code);
            }

            int fCode;
            String fTitle;
            randomAccessFile.readLine(); // reading last generated code
            randomAccessFile.readLine();// reading record count
            boolean found = false; // flag

            // looping through the file and finding the code
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fCode = Integer.parseInt(randomAccessFile.readLine()); // reading code
                if (fCode == code) {
                    found = true;
                    break;
                }
                randomAccessFile.readLine(); // reading title
            }

            // code not found
            if (found == false) {
                randomAccessFile.close();
                throw new DAOException("Invalid code : " + code);
            }

            // if found
            randomAccessFile.seek(0); // pointing internal pointer to the start
            randomAccessFile.readLine(); // reading last generated code
            randomAccessFile.readLine();// reading record count

            // checking if our title already exists on a different code or not
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fCode = Integer.parseInt(randomAccessFile.readLine());
                fTitle = randomAccessFile.readLine();
                // code is different title is same, this conditions handles the case when we
                // update the same title as previous e.i when code and title is same
                if (fCode != code && fTitle.equalsIgnoreCase(title) == true) {
                    randomAccessFile.close();
                    throw new DAOException("Title : " + title + " exists.");
                }
            }

            // all checks done
            File tmpFile = new File("tmp.data"); // creating a temporary file pointer
            if (tmpFile.exists()) // delete if already exists
                tmpFile.delete();
            RandomAccessFile tmpRandomAccessFile;
            tmpRandomAccessFile = new RandomAccessFile(tmpFile, "rw"); // creating file
            randomAccessFile.seek(0); // pointing to start

            // copying the header section to the temp file
            tmpRandomAccessFile.writeBytes(randomAccessFile.readLine());
            tmpRandomAccessFile.writeBytes("\n");
            tmpRandomAccessFile.writeBytes(randomAccessFile.readLine());
            tmpRandomAccessFile.writeBytes("\n");

            // reading from original file and writing into temp file
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fCode = Integer.parseInt(randomAccessFile.readLine());
                fTitle = randomAccessFile.readLine();
                if (code != fCode) { // if the code is different that means this is not to update then just write as
                                     // it is
                    tmpRandomAccessFile.writeBytes(String.valueOf(fCode));
                    tmpRandomAccessFile.writeBytes("\n");
                    tmpRandomAccessFile.writeBytes(fTitle);
                    tmpRandomAccessFile.writeBytes("\n");
                } else {// else needed to be updated
                    tmpRandomAccessFile.writeBytes(String.valueOf(code));
                    tmpRandomAccessFile.writeBytes("\n");
                    tmpRandomAccessFile.writeBytes(title); // writing the new title
                    tmpRandomAccessFile.writeBytes("\n");
                }
            }

            // now we have out updated file, copying it to the original
            randomAccessFile.seek(0); // original file pointer to start
            tmpRandomAccessFile.seek(0);// tmp file pointer to start
            while (tmpRandomAccessFile.getFilePointer() < tmpRandomAccessFile.length()) {
                randomAccessFile.writeBytes(tmpRandomAccessFile.readLine());
                randomAccessFile.writeBytes("\n");
            }
            // now we have updated the file
            randomAccessFile.setLength(tmpRandomAccessFile.length()); // setting the length of our file
            tmpRandomAccessFile.setLength(0); // setting length to 0 e.i empty file
            randomAccessFile.close();
            tmpRandomAccessFile.close();
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public void delete(int code) throws DAOException {
        // validations
        if (code <= 0)
            throw new DAOException("Invalid code : " + code);
        try {
            File file = new File(FILE_NAME);
            // file not found
            if (file.exists() == false)
                throw new DAOException("Invalid code : " + code);
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            // file is empty
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid code : " + code);
            }
            // reading header
            randomAccessFile.readLine();
            randomAccessFile.readLine();
            int fCode;
            String fTitle = "";
            boolean flag = false;
            // checking that the code is present or not
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fCode = Integer.parseInt(randomAccessFile.readLine());
                fTitle = randomAccessFile.readLine();
                if (code == fCode) {
                    flag = true;
                    break;
                }
            }
            // not found
            if (flag == false) {
                randomAccessFile.close();
                throw new DAOException("Invalid code : " + code);
            }
            // this is for employee, it checks that is this designation is alloted to any
            // employee or not if it is then we can't delete the designation
            if (new EmployeeDAO().isDesignationAlloted(code)) {
                randomAccessFile.close();
                throw new DAOException("Employee exists with designation : " + fTitle);
            }

            File tmpFile = new File("tmp.data");
            if (tmpFile.exists())
                tmpFile.delete();
            // creating a new temporary file
            RandomAccessFile tmpRandomAccessFile;
            tmpRandomAccessFile = new RandomAccessFile(tmpFile, "rw");

            randomAccessFile.seek(0);
            tmpRandomAccessFile.writeBytes(randomAccessFile.readLine()); // reading last generated code and writing into
                                                                         // temp file
            tmpRandomAccessFile.writeBytes("\n");

            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            recordCount--; // updating record count
            String recordCountString = String.valueOf(recordCount);
            while (recordCountString.length() < 10) {
                recordCountString += " ";
            }
            tmpRandomAccessFile.writeBytes(recordCountString); // writing updated record count into temp file
            tmpRandomAccessFile.writeBytes("\n");
            // looping through the file writing it into temp file leaving when the code is
            // equal
            // e.i writing all except the data that is to be deleted
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fCode = Integer.parseInt(randomAccessFile.readLine());
                fTitle = randomAccessFile.readLine();
                if (fCode != code) {
                    tmpRandomAccessFile.writeBytes(String.valueOf(fCode));
                    tmpRandomAccessFile.writeBytes("\n");
                    tmpRandomAccessFile.writeBytes(fTitle);
                    tmpRandomAccessFile.writeBytes("\n");
                }
            }
            // copying the updated information into our original file
            randomAccessFile.seek(0);
            tmpRandomAccessFile.seek(0);
            while (tmpRandomAccessFile.getFilePointer() < tmpRandomAccessFile.length()) {
                randomAccessFile.writeBytes(tmpRandomAccessFile.readLine());
                randomAccessFile.writeBytes("\n");
            }
            // updating lengths
            randomAccessFile.setLength(tmpRandomAccessFile.length());
            tmpRandomAccessFile.setLength(0);
            randomAccessFile.close();
            tmpRandomAccessFile.close();
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public Set<DesignationDTOInterface> getAll() throws DAOException {
        Set<DesignationDTOInterface> designations; // creating a pointer
        designations = new TreeSet<>(); // initializing empty Treeset
        try {
            // validations
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return designations; // returning empty treeSet if file not found
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return designations; // returning empty treeSet
            }
            randomAccessFile.readLine(); // reading last generated code
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) {
                randomAccessFile.close();
                return designations; // empty treeSet
            }
            // creating TreeSet
            DesignationDTOInterface designationDTO; // creating pointer
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) { // looping through the file
                                                                                    // extracting data wrapping it into
                                                                                    // DesgnationDTO and adding into
                                                                                    // Treeset
                designationDTO = new DesignationDTO(); // creting object
                designationDTO.setCode(Integer.parseInt(randomAccessFile.readLine())); // setting code
                designationDTO.setTitle(randomAccessFile.readLine());// setting title
                designations.add(designationDTO); // adding object to TreeSet
            }
            randomAccessFile.close(); // closing file and returning the TreeSet
            return designations;
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public DesignationDTOInterface getByCode(int code) throws DAOException {
        // validations
        if (code <= 0)
            throw new DAOException("Invalid code : " + code);
        try {
            File file = new File(FILE_NAME);
            RandomAccessFile randomAccessFile;
            if (file.exists() == false)
                throw new DAOException("Invalid code : " + code);
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid code : " + code);
            }
            randomAccessFile.readLine(); // reading last generated code
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid code : " + code);
            }
            int fCode;
            String fTitle;
            // looping through the file and finding the code
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fCode = Integer.parseInt(randomAccessFile.readLine());
                fTitle = randomAccessFile.readLine();
                if (code == fCode) { // found
                    randomAccessFile.close();
                    // creating object to return
                    DesignationDTOInterface designationDTO;
                    designationDTO = new DesignationDTO();
                    designationDTO.setCode(fCode); // setting code and title in object
                    designationDTO.setTitle(fTitle);
                    return designationDTO;
                }
            }
            // if not found
            randomAccessFile.close();
            throw new DAOException("Invalid code : " + code);
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public DesignationDTOInterface getByTitle(String title) throws DAOException {
        // validations
        if (title == null || title.trim().length() == 0)
            throw new DAOException("Invalid title : " + title);
        title = title.trim();
        try {
            File file = new File(FILE_NAME);
            RandomAccessFile randomAccessFile;
            if (file.exists() == false)
                throw new DAOException("Invalid title : " + title);
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid title : " + title);
            }
            randomAccessFile.readLine(); // reading last generated code
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) {
                randomAccessFile.close();
                throw new DAOException("Invalid title : " + title);
            }
            int fCode;
            String fTitle;
            // looping through the file and finding the title
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fCode = Integer.parseInt(randomAccessFile.readLine());
                fTitle = randomAccessFile.readLine();
                if (title.equalsIgnoreCase(fTitle)) { // if found
                    randomAccessFile.close();
                    // creating object and return
                    DesignationDTOInterface designationDTO;
                    designationDTO = new DesignationDTO();
                    designationDTO.setCode(fCode);
                    designationDTO.setTitle(fTitle);
                    return designationDTO;
                }
            }
            // if not found
            randomAccessFile.close();
            throw new DAOException("Invalid title : " + title);
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public boolean codeExists(int code) throws DAOException {
        // validations
        if (code <= 0)
            return false;
        try {
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return false;
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return false;
            }
            randomAccessFile.readLine(); // reading last generated code
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) {
                randomAccessFile.close();
                return false;
            }
            int fCode;
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                fCode = Integer.parseInt(randomAccessFile.readLine()); // reading code
                if (fCode == code) {
                    randomAccessFile.close(); // code found closing the file and returning true
                    return true;
                }
                randomAccessFile.readLine(); // reading title
            }
            randomAccessFile.close();
            return false; // if control is here than it means that the code didn't exists
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public boolean titleExists(String title) throws DAOException {
        // validations
        if (title == null || title.trim().length() == 0)
            return false;
        title = title.trim();
        try {
            File file = new File(FILE_NAME);
            if (file.exists() == false)
                return false;
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) {
                randomAccessFile.close();
                return false;
            }
            randomAccessFile.readLine(); // reading last generated code
            int recordCount = Integer.parseInt(randomAccessFile.readLine().trim());
            if (recordCount == 0) {
                randomAccessFile.close();
                return false;
            }

            // looping through the file and checking if the title exists
            String fTitle;
            while (randomAccessFile.getFilePointer() < randomAccessFile.length()) {
                randomAccessFile.readLine(); // reading code
                fTitle = randomAccessFile.readLine(); // reading title
                if (fTitle.equalsIgnoreCase(title)) {
                    randomAccessFile.close();
                    return true; // return true if found
                }
            }
            randomAccessFile.close();
            return false; // if not found
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }

    public int getCount() throws DAOException {
        try {
            File file = new File(FILE_NAME);
            // validations
            if (file.exists() == false) // file not found
                return 0;
            RandomAccessFile randomAccessFile;
            randomAccessFile = new RandomAccessFile(file, "rw");
            if (randomAccessFile.length() == 0) { // file is empty
                randomAccessFile.close();
                return 0;
            }
            randomAccessFile.readLine(); // reading last generated code
            int recordCount;
            recordCount = Integer.parseInt(randomAccessFile.readLine().trim()); // reading record count and trimming
            randomAccessFile.close();
            return recordCount; // returning count
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage());
        }
    }
}