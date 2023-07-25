package com.thinking.machines.hr.pl.model;

import com.thinking.machines.hr.bl.interfaces.managers.*;
import com.thinking.machines.hr.bl.interfaces.pojo.*;
import com.thinking.machines.hr.bl.managers.*;
import com.thinking.machines.hr.bl.pojo.*;
import com.thinking.machines.hr.bl.exceptions.*;
import java.util.*;
import java.io.*;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.io.image.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.io.font.constants.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.*;
import com.itextpdf.layout.borders.*;

import javax.swing.table.*;

// this is the model class of the MVC framework (changes in model will reflect changes is view)
public class DesignationModel extends AbstractTableModel {

    // properties
    private java.util.List<DesignationInterface> designations;// List type pointer
    private DesignationManagerInterface designationManager; // pointer for data from BL
    private String[] columnTitle;

    // constructor
    public DesignationModel() {
        this.populateDataStructures(); // calling populateDataStructures
    }

    // method for fetching data from the BL and populating the data structures of
    // our Model
    private void populateDataStructures() {
        this.columnTitle = new String[2]; // for two columns
        this.columnTitle[0] = "S.No.";
        this.columnTitle[1] = "Designation";

        try {
            designationManager = DesignationManager.getDesignationManager(); // this method returns the object of
                                                                             // designationManager
        } catch (BLException blException) {
            // ?????????
        }

        // getting the set of designation objects from BL
        Set<DesignationInterface> blDesignations = designationManager.getDesignations();

        this.designations = new LinkedList<>(); // creating a new LinkedList
        // itrating through the set from BL
        for (DesignationInterface designation : blDesignations) {
            this.designations.add(designation); // adding each object into the linked list

            // sorting our linked list based on the title using Comparator function
            Collections.sort(this.designations, new Comparator<DesignationInterface>() {
                public int compare(DesignationInterface left, DesignationInterface right) {
                    return left.getTitle().toUpperCase().compareTo(right.getTitle().toUpperCase()); // returning after
                                                                                                    // comparing the
                                                                                                    // title of
                                                                                                    // designation
                                                                                                    // objects
                }
            });
        }
    }

    // used for getting the row count returns designation count because number of
    // designations will be equal to number of rows needed to display them
    public int getRowCount() {
        return designations.size();
    }

    // returns the number of column required to represent the data
    public int getColumnCount() {
        return this.columnTitle.length; // returning the length of columnTitle string
    }

    // returns the name of the column on the given index
    public String getColumnName(int columnIndex) {
        return columnTitle[columnIndex];
    }

    // returns the value inside the table at (rowIndex,columnIndex)
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) // column index 0 represents serial number
            return rowIndex + 1; // so return the serial number
        else
            return this.designations.get(rowIndex).getTitle(); // return the title of the designation on the rowIndes in
                                                               // the
                                                               // LinkedList in our Model
    }

    // returns the Class (data type) of column at columnIndex
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) // serial number
            return Integer.class; // return Class.forName("java.lang.Integer")
        else
            return String.class; // designation title
    }

    // tells if the cell is editable or not
    public boolean isCellEditable(int rowIndex, int columIndex) {
        return false;
    }

    // Application Specific Methods

    // this method is used to add designation
    public void add(DesignationInterface designation) throws BLException {
        // adding designation to BL using addDesignation method
        designationManager.addDesignation(designation);
        // also adding this method to our data structure of our Model
        this.designations.add(designation);

        // now sorting our data structure after adding new designation
        Collections.sort(this.designations, new Comparator<DesignationInterface>() {
            public int compare(DesignationInterface left, DesignationInterface right) {
                return left.getTitle().toUpperCase().compareTo(right.getTitle().toUpperCase());
            }
        });

        // this is the method of AbstractTableModel this method forces JTable to update
        // the table contents
        fireTableDataChanged();
    }

    // this method returns the index of the given designation in our List in Model
    public int indexOfDesignation(DesignationInterface designation) throws BLException {
        // creating iterator
        Iterator<DesignationInterface> iterator = this.designations.iterator();
        DesignationInterface d;
        int index = 0;

        // looping throught the list
        while (iterator.hasNext()) {
            d = iterator.next();
            if (d.equals(designation)) { // if the designation matched then return the index
                return index;
            }
            index++; // incrementing index
        }

        // if we are here then it means that the loop is over and we still didn't get
        // the designation so throwing exception
        BLException blException = new BLException();
        blException.setGenericException("Invalid desiagntion : " +
                designation.getTitle());
        throw blException;
    }

    // this method returns the index of designation in our List by comparing title
    // this method is used in the search bar functionality
    // parameter partialLeftSearch determines comparing the whole string or portion
    public int indexOfTitle(String title, boolean partialLeftSearch) throws BLException {
        // creating iterator
        Iterator<DesignationInterface> iterator = this.designations.iterator();
        DesignationInterface d;
        int index = 0;

        // looping through the list
        while (iterator.hasNext()) {
            d = iterator.next();
            if (partialLeftSearch) { // if partialLeftSearch is true then search with startsWith
                if (d.getTitle().toUpperCase().startsWith(title.toUpperCase())) { // returns true if the first string
                                                                                  // starts with the second
                    return index; // returning index
                }
            } else {
                if (d.getTitle().equalsIgnoreCase(title)) { // if partialLeftSearch is false then use equals to compare
                    return index;
                }
            }
            index++; // incrementing index
        }

        // if we are here then it means that it doesn't matched so raising exception
        BLException blException = new BLException();
        blException.setGenericException("Invalid title : " + title);
        throw blException;
    }

    // method for designation updation
    public void update(DesignationInterface designation) throws BLException {

        // calling the updateDesignation method of designationManager and updating the
        // designation in the BL
        designationManager.updateDesignation(designation);

        // updating the designation in our List (Model)
        this.designations.remove(indexOfDesignation(designation)); // getting the index of the designtion to be removed
                                                                   // by calling the indexOfDesignation method
        this.designations.add(designation);

        // sorting the updated list
        Collections.sort(this.designations, new Comparator<DesignationInterface>() {
            public int compare(DesignationInterface left, DesignationInterface right) {
                return left.getTitle().toUpperCase().compareTo(right.getTitle().toUpperCase());
            }
        });

        // reflecting the changes in the table
        fireTableDataChanged();
    }

    // this method is to remove designation
    public void remove(int code) throws BLException {

        // removing designation from BL by calling removeDesignation
        designationManager.removeDesignation(code);

        // creating iterator
        Iterator<DesignationInterface> iterator = this.designations.iterator();
        int index = 0;

        // looping through the List to get the index of the designation to be removed
        while (iterator.hasNext()) {
            if (iterator.next().getCode() == code)
                break;
            index++;
        }

        // if we traversed the whole list then index will be equal to the size of the
        // list and it means the designation doesn't matched so throwing exception
        if (index == this.designations.size()) {
            BLException blException = new BLException();
            blException.setGenericException("Invalid designation code : " + code);
            throw blException;
        }

        // removing designation from the List
        this.designations.remove(index);

        // updating the table after removing the designation
        fireTableDataChanged();
    }

    // this method returns the designation object at the given index
    public DesignationInterface getDesignationAt(int index) throws BLException {
        if (index < 0 || index >= this.designations.size()) {
            BLException blException = new BLException();
            blException.setGenericException("Invalid index : " + index);
            throw blException;
        }
        return this.designations.get(index); // returning object from the List
    }

    // method for creating pdf file
    public void exportToPDF(File file) throws BLException {

        try {
            // if file already exists then delete it
            if (file.exists())
                file.delete();

            PdfWriter pdfWriter = new PdfWriter(file); // creating instance of PdfWriter
            PdfDocument pdfDocument = new PdfDocument(pdfWriter); // creating instance of PdfDocument and passing
                                                                  // pdfWriter to it
            Document doc = new Document(pdfDocument); // creating instance of Document and passing pdfDocument to it

            // creating image variable
            Image logo = new Image(ImageDataFactory.create(this.getClass().getResource("/icons/logo_icon.png")));

            // para for logo
            Paragraph logoPara = new Paragraph();
            logoPara.add(logo); // adding logo to para

            // para for company name
            Paragraph companyNamePara = new Paragraph();
            companyNamePara.add("HRS Corporation"); // adding company name
            // creating font for company name
            PdfFont companyNameFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            companyNamePara.setFont(companyNameFont); // setting font
            companyNamePara.setFontSize(18);// setting font size

            // para for report title
            Paragraph reportTitlePara = new Paragraph("List of designation");
            PdfFont reportTitleFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD); // font for report title
            reportTitlePara.setFont(reportTitleFont);// setting font
            reportTitlePara.setFontSize(15);// setting font size

            // more fonts
            PdfFont columnTitleFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            PdfFont dataFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

            // para for column title
            Paragraph columnTitle1 = new Paragraph("S.No.");
            columnTitle1.setFont(columnTitleFont);
            columnTitle1.setFontSize(14);

            // para for column title2
            Paragraph columnTitle2 = new Paragraph("Designations");
            columnTitle2.setFont(columnTitleFont);
            columnTitle2.setFontSize(14);

            // para for page number
            Paragraph pageNumberParagraph;
            PdfFont pageNumberFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);

            // para for data
            Paragraph dataParagraph;

            // variables
            float topTableColumnWidths[] = { 1, 5 };
            float dataTableColumnWidths[] = { 1, 5 };
            int sno, x, pageSize;
            pageSize = 5;
            boolean newPage = true;
            Table pageNumberTable;
            Table topTable;
            Table dataTable = null;
            Cell cell;

            // calculating number of pages
            int numberOfPages = this.designations.size() / pageSize;
            if ((this.designations.size() % pageSize) != 0)
                numberOfPages++;

            DesignationInterface designation;
            int pageNumber = 0;
            sno = 0;
            x = 0;
            while (x < this.designations.size()) {
                // if it is a new page
                if (newPage == true) {
                    // creating new page header
                    pageNumber++;
                    topTable = new Table(UnitValue.createPercentArray(topTableColumnWidths));
                    cell = new Cell();
                    cell.setBorder(Border.NO_BORDER);
                    cell.add(logoPara);
                    topTable.addCell(cell);
                    cell = new Cell();
                    cell.setBorder(Border.NO_BORDER);
                    cell.add(companyNamePara);
                    cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    topTable.addCell(cell);
                    doc.add(topTable);
                    pageNumberParagraph = new Paragraph("Page : " + pageNumber + "/" +
                            numberOfPages);
                    pageNumberParagraph.setFont(pageNumberFont);
                    pageNumberParagraph.setFontSize(13);
                    pageNumberTable = new Table(1);
                    pageNumberTable.setWidth(UnitValue.createPercentValue(100));
                    cell = new Cell();
                    cell.setBorder(Border.NO_BORDER);
                    cell.add(pageNumberParagraph);
                    cell.setTextAlignment(TextAlignment.RIGHT);
                    pageNumberTable.addCell(cell);
                    doc.add(pageNumberTable);
                    dataTable = new Table(UnitValue.createPercentArray(dataTableColumnWidths));
                    dataTable.setWidth(UnitValue.createPercentValue(100));
                    cell = new Cell(1, 2);
                    cell.add(reportTitlePara);
                    cell.setTextAlignment(TextAlignment.CENTER);
                    dataTable.addHeaderCell(cell);
                    dataTable.addHeaderCell(columnTitle1);
                    dataTable.addHeaderCell(columnTitle2);
                    newPage = false;
                }
                designation = this.designations.get(x);
                // adding row to tabel
                sno++;
                cell = new Cell();
                dataParagraph = new Paragraph(String.valueOf(sno));
                dataParagraph.setFont(dataFont);
                dataParagraph.setFontSize(14);
                cell.add(dataParagraph);
                cell.setTextAlignment(TextAlignment.RIGHT);
                dataTable.addCell(cell);

                cell = new Cell();
                dataParagraph = new Paragraph(designation.getTitle());
                dataParagraph.setFont(dataFont);
                dataParagraph.setFontSize(14);
                cell.add(dataParagraph);
                dataTable.addCell(cell);
                x++;
                if (sno % pageSize == 0 || x == this.designations.size()) {
                    // creating footer
                    doc.add(dataTable);
                    doc.add(new Paragraph("Software by : Anonymous"));
                    if (x < this.designations.size()) {
                        // add new page to document
                        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                        newPage = true;
                    }
                }
            } // loop end
            doc.close();
        } catch (Exception exception) {
            BLException blException = new BLException();
            blException.setGenericException(exception.getMessage());
            throw blException;
        }
    }// funtion ends

}// class ends