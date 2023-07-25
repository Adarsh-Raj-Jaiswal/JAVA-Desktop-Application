package com.thinking.machines.hr.bl.managers;

import com.thinking.machines.hr.bl.interfaces.pojo.*;
import com.thinking.machines.hr.bl.interfaces.managers.*;
import com.thinking.machines.hr.bl.exceptions.*;
import com.thinking.machines.hr.bl.pojo.*;
import java.util.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.dao.*;
import com.thinking.machines.hr.dl.dto.*;

public class DesignationManager implements DesignationManagerInterface {

    // these are our data structures pointers
    private Map<Integer, DesignationInterface> codeWiseDesignationMap; // first data structure Map of (designation code
                                                                       // and object) pair, to search fast on the basis
                                                                       // of designation code

    private Map<String, DesignationInterface> titleWiseDesignationMap; // second data structure Map of (designation
                                                                       // title and object) pair, to search fast on the
                                                                       // basis of designation title

    private Set<DesignationInterface> designationsSet; // third data structure Set of objects for orederd list etc

    // static pointer initialized to null, this will be used to implement the
    // sigleton pattern, if it is null then it means that object is not created yet
    // and we will create object, it is static so that it can be accessed in static
    // method
    private static DesignationManager designationManager = null; // pointer

    // private constructor means that creating its object is not allowed, kyoki ham
    // chahte hai ki iska ek hi object bane agar socho 4 objects ban gye to 4 data
    // sturctures ho jayenge pr hamko chahiye ki poori application ki life cycle m
    // iska ek hi object bane (this is called singleton pattern)
    private DesignationManager() throws BLException { // private constructor
        populateDataStructure(); // invoking populate data structures method
    }

    // this method is used to implement the sigleton pattern and it is static so
    // that it can be accessed without creating object
    public static DesignationManagerInterface getDesignationManager() throws BLException { // this method ensures that
                                                                                           // the object is created only
                                                                                           // once in application life
                                                                                           // cycle
        if (designationManager == null) // if it is null so that means object is not created yet
            designationManager = new DesignationManager(); // so create object
        return designationManager; // return object
    }

    // this method will populate(feed data into it) our dataStructures
    private void populateDataStructure() throws BLException {
        this.codeWiseDesignationMap = new HashMap<>(); // creating hashmaps
        this.titleWiseDesignationMap = new HashMap<>();
        this.designationsSet = new TreeSet<>(); // creating a TreeSet

        try {

            Set<DesignationDTOInterface> dlDesignations; // creating a DesignationDTOInterface type Set pointer
            dlDesignations = new DesignationDAO().getAll(); // now assigning set of DesignationDTO type objects to this
                                                            // pointer using the getAll method of DesignationDAO class

            DesignationInterface designation; // creating pointer
            for (DesignationDTOInterface dlDesignation : dlDesignations) { // iterating in the set of objects from DL

                designation = new Designation(); // creating our object (POJO)

                designation.setCode(dlDesignation.getCode()); // cloning from the set into our object (POJO)
                designation.setTitle(dlDesignation.getTitle());

                this.codeWiseDesignationMap.put(new Integer(designation.getCode()), designation); // putting into code
                                                                                                  // wise map getting
                                                                                                  // code by calling
                                                                                                  // getCode method
                this.titleWiseDesignationMap.put(designation.getTitle().toUpperCase(), designation); // putting into
                                                                                                     // title wise map
                                                                                                     // getting title by
                                                                                                     // calling getTitle
                                                                                                     // method and
                                                                                                     // converting it to
                                                                                                     // uppercase
                this.designationsSet.add(designation); // putting into Set

                // Note that we are putting address of our object into these data structures not
                // the whole object so there is not any data redundancy
            }

        } catch (DAOException daoException) { // if there is any exception raised on the DL level then we will throw it
                                              // by wrapping it into BLExceptions
            BLException blException = new BLException();
            blException.setGenericException(daoException.getMessage()); // setting generic exception
            throw blException;
        }
    }

    public void addDesignation(DesignationInterface designation) throws BLException {

        // creating instance of exception class
        BLException blException;
        blException = new BLException();

        // validation if designation is null
        if (designation == null) {
            blException.setGenericException("Designation required"); // setting generic exception
            throw blException; // throwing it coz it is generic
        }

        // validations on code and title of designation
        int code = designation.getCode();
        String title = designation.getTitle();

        if (code != 0) { // code should not be set by user we will set it so it is necessary that it
                         // should be 0 (default value of int)
            blException.addException("code", "Code should be zero");
        }
        if (title == null) { // if title not provided
            blException.addException("title", "Title required");
            title = "";
        } else {
            title = title.trim();
            if (title.length() == 0) {
                blException.addException("title", "Title required");
            }
        }
        if (title.length() > 0) { // if title already exist
            if (this.titleWiseDesignationMap.containsKey(title.toUpperCase())) { // checking into map also converting to
                                                                                 // uppercase for searching
                blException.addException("title", "Designation : " + title + " exists.");
            }
        }

        // if there is any exception found then throw it
        if (blException.hasExceptions()) {
            throw blException;
        }

        // adding
        try {
            DesignationDTOInterface designationDTO;
            designationDTO = new DesignationDTO(); // creating designationDTO object for DL
            designationDTO.setTitle(title); // setting title in DTO

            DesignationDAOInterface designationDAO;
            designationDAO = new DesignationDAO(); // creating object of DAO
            designationDAO.add(designationDTO); // giving this object to DL (adding designation)
            code = designationDTO.getCode(); // when designation is added it will generate a code
            designation.setCode(code); // setting this code into parameter object (from PL)

            Designation dsDesignation;
            dsDesignation = new Designation(); // now creating our POJO
            dsDesignation.setCode(code); // cloning into POJO (setting code and title)
            dsDesignation.setTitle(title);

            // now adding this POJO into our data structures
            this.codeWiseDesignationMap.put(new Integer(code), dsDesignation);
            this.titleWiseDesignationMap.put(title.toUpperCase(), dsDesignation); // converting title to uppercase
            this.designationsSet.add(dsDesignation);

        } catch (DAOException daoException) {
            blException.setGenericException(daoException.getMessage()); // if there is any DAOException (exception
                                                                        // raised at DL level)
            throw blException;
        }
    }

    public void updateDesignation(DesignationInterface designation) throws BLException {

        // creating instance of BLException
        BLException blException;
        blException = new BLException();

        // validating designation
        if (designation == null) {
            blException.setGenericException("Designation required");
            throw blException;
        }

        // extracting code and title from object
        int code = designation.getCode();
        String title = designation.getTitle();

        // validations
        if (code <= 0) {
            blException.addException("code", "Invalid code : " + code); // validating code
        }
        if (code > 0) {
            if (this.codeWiseDesignationMap.containsKey(new Integer(code)) == false) { // if it is not present in the
                                                                                       // map that means it is not valid
                blException.addException("code", "Invalid code : " + code);
                throw blException; // if code is invalid then no sense in further checking so throwing exception
                                   // here only
            }
        }

        // validating title
        if (title == null) {
            blException.addException("title", "Title required");
            title = "";
        } else {
            title = title.trim();
            if (title.length() == 0) {
                blException.addException("title", "Title required");
            }
        }

        // checking that this title already exist against any designation or not
        if (title.length() > 0) {
            DesignationInterface d;
            d = this.titleWiseDesignationMap.get(title.toUpperCase());
            if (d != null && d.getCode() != code) { // if title already exist against any other designation then add
                                                    // Exception
                blException.addException("title", "Designation : " + title + " exists.");
            }
        }

        // if there is any exception then throw it
        if (blException.hasExceptions()) {
            throw blException;
        }

        // all the validations are like -> code should exist so that we can identify
        // that which object has to be updated coz code is our primary key and title
        // should not exist on any other designation so that to maintain its uniqueness

        // updating
        try {

            DesignationInterface dsDesignation = this.codeWiseDesignationMap.get(code); // searching for object which is
                                                                                        // to be updated using code
            DesignationDTOInterface dlDesignationDTO = new DesignationDTO(); // creating DTO for updations in DL

            // setting code and title in DTO
            dlDesignationDTO.setCode(code);
            dlDesignationDTO.setTitle(title);
            new DesignationDAO().update(dlDesignationDTO); // calling update method of DAO this will update the
                                                           // designation at DL level

            // remove old object from the data structures
            this.codeWiseDesignationMap.remove(code);
            this.titleWiseDesignationMap.remove(dsDesignation.getTitle().toUpperCase());
            this.designationsSet.remove(dsDesignation);

            // updating POJO
            dsDesignation.setTitle(title);

            // adding the updated object
            this.codeWiseDesignationMap.put(code, dsDesignation);
            this.titleWiseDesignationMap.put(title.toUpperCase(), dsDesignation);
            this.designationsSet.add(dsDesignation);

            // throwing any exception raised at DL level as generic exception by wrapping it
            // into BLException
        } catch (DAOException daoException) {
            blException.setGenericException(daoException.getMessage());
            throw blException;
        }
    }

    public void removeDesignation(int code) throws BLException {

        // creating instance of BLException
        BLException blException;
        blException = new BLException();

        // validating code
        if (code <= 0) {
            blException.addException("code", "Invalid code : " + code);
            throw blException;
        }

        if (code > 0) {
            if (this.codeWiseDesignationMap.containsKey(new Integer(code)) == false) { // if code not exist then throw
                                                                                       // exception
                blException.addException("code", "Invalid code : " + code);
                throw blException;
            }
        }

        try {

            DesignationInterface dsDesignation = this.codeWiseDesignationMap.get(code); // searching for the object with
                                                                                        // this code

            // deleting designation from DL
            new DesignationDAO().delete(code);

            // removing object from data structures
            this.codeWiseDesignationMap.remove(code);
            this.titleWiseDesignationMap.remove(dsDesignation.getTitle().toUpperCase());
            this.designationsSet.remove(dsDesignation);

            // throw if any exception
        } catch (DAOException daoException) {
            blException.setGenericException(daoException.getMessage());
            throw blException;
        }
    }

    // this below method is for internal use
    DesignationInterface getDSDesignationByCode(int code) { // if method is not public then package ke bahar uska use
                                                            // nhi ho skta
        DesignationInterface designation;
        designation = this.codeWiseDesignationMap.get(code);
        return designation; // this method returns the actual address of the object from our dataStructure
                            // not clone
    }

    public DesignationInterface getDesignationByCode(int code) throws BLException {

        DesignationInterface designation;
        designation = this.codeWiseDesignationMap.get(code); // searching designation in map using code

        if (designation == null) {
            BLException blException; // if not found then throw exception
            blException = new BLException();
            blException.addException("code", "Invalid Code : " + code);
            throw blException;
        }

        // if found then create object and return
        DesignationInterface d = new Designation(); // creating object (POJO)

        // setting code and title into object
        d.setCode(designation.getCode());
        d.setTitle(designation.getTitle());

        return d; // returning object
    }

    public DesignationInterface getDesignationByTitle(String title) throws BLException {

        DesignationInterface designation;
        designation = this.titleWiseDesignationMap.get(title.toUpperCase()); // searching designation in map using title

        if (designation == null) {
            BLException blException; // if not found then throw exception
            blException = new BLException();
            blException.addException("title", "Invalid Title : " + title);
            throw blException;
        }

        // if found then create object and return
        DesignationInterface d = new Designation(); // creating object

        // setting code and title into object
        d.setCode(designation.getCode());
        d.setTitle(designation.getTitle());

        return d; // returning object
    }

    public int getDesignationCount() {
        return this.designationsSet.size(); // returning count
    }

    public boolean designationCodeExists(int code)  {
        return this.codeWiseDesignationMap.containsKey(code); // checking by calling the containsKey method of code wise
                                                              // map
    }

    public boolean designationTitleExists(String title)  {
        return this.titleWiseDesignationMap.containsKey(title.toUpperCase()); // checking by calling the containsKey
                                                                              // method of code wise map
    }

    // this method returns the Set of Designation objects
    public Set<DesignationInterface> getDesignations() {

        Set<DesignationInterface> designations;
        designations = new TreeSet<>(); // creating a treeSet to return

        // iterating through the set using for each loop
        designationsSet.forEach((designation) -> {

            // creating object
            DesignationInterface d = new Designation();

            // setting code and title into it (cloning)
            d.setCode(designation.getCode());
            d.setTitle(designation.getTitle());

            // adding this obect into treeSet
            designations.add(d);
        });

        // returning the TreeSet
        return designations;
    }

}// class ends