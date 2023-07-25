package com.thinking.machines.hr.pl.ui;

import com.thinking.machines.hr.pl.model.*;
import com.thinking.machines.hr.bl.exceptions.*;
import com.thinking.machines.hr.bl.interfaces.pojo.*;
import com.thinking.machines.hr.bl.pojo.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;

public class DesignationUI extends JFrame implements DocumentListener, ListSelectionListener {
    // properties
    private JLabel titleLabel; // Label for Title
    private JLabel searchLabel; // Label for Search
    private JTextField searchTextField; // TextField for search
    private JButton clearSearchTextFieldButton; // Button next to search field for clearing search
    private JLabel searchErrorLabel; // Label for search error
    private JTable designationTable; // Table for displaying designations
    private JScrollPane scrollPane; // ScrollPane for designations
    private DesignationModel designationModel; // Pointer of DesignationModel
    private Container container; // Container
    private DesignationPanel designationPanel; // inner Panel class pointer

    private enum MODE {
        VIEW, ADD, EDIT, DELETE, EXPORT_TO_PDF
    };

    private MODE mode;
    private ImageIcon logoIcon;
    private ImageIcon addIcon;
    private ImageIcon editIcon;
    private ImageIcon deleteIcon;
    private ImageIcon cancelIcon;
    private ImageIcon pdfIcon;
    private ImageIcon saveIcon;
    private ImageIcon clearIcon;

    public DesignationUI() {
        initComponents(); // object creation
        setAppearance(); // font setting and positioning
        addListeners(); // adding listeners event handlers
        setViewMode(); // setting the view mode of DesignationUI class
        designationPanel.setViewMode(); // setting the view mode of designation panel inner class
    }

    private void initComponents() {
        logoIcon = new ImageIcon(this.getClass().getResource("/icons/logo_icon.png"));
        addIcon = new ImageIcon(this.getClass().getResource("/icons/add_icon.png"));
        editIcon = new ImageIcon(this.getClass().getResource("/icons/edit_icon.png"));
        cancelIcon = new ImageIcon(this.getClass().getResource("/icons/cancel_icon.png"));
        saveIcon = new ImageIcon(this.getClass().getResource("/icons/save_icon.png"));
        deleteIcon = new ImageIcon(this.getClass().getResource("/icons/delete_icon.png"));
        pdfIcon = new ImageIcon(this.getClass().getResource("/icons/pdf_icon.png"));
        clearIcon = new ImageIcon(this.getClass().getResource("/icons/cancel_icon.png"));

        setIconImage(logoIcon.getImage());

        designationModel = new DesignationModel(); // creating object of DesignationModel
        titleLabel = new JLabel("Designations"); // Setting "Designations" in Label
        searchLabel = new JLabel("Search"); // Setting "Search" in Label
        searchTextField = new JTextField(); // creating TextField
        clearSearchTextFieldButton = new JButton(clearIcon); // Creating button and setting clear icon on it
        searchErrorLabel = new JLabel(""); // Setting empty string in error label coz in start there will be no errors
        designationTable = new JTable(designationModel); // passing object of designationModel to JTable so that JTable
                                                         // will fetch data from designationModel (the Model View
                                                         // approach)
        scrollPane = new JScrollPane(designationTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Creating ScrollPane in designationTable and
                                                                     // setting vertical scrooll bar to always and
                                                                     // horizontal scroll bar to if needed
        container = getContentPane(); // creating container

        designationPanel = new DesignationPanel(); // creating object of inner class DesignationPanel
        setDefaultCloseOperation(EXIT_ON_CLOSE); // setting default close operation to exit on close
    }

    private void setAppearance() {

        // Creating font variables
        Font titleFont = new Font("Verdana", Font.BOLD, 18);
        Font captionFont = new Font("Verdana", Font.BOLD, 16);
        Font dataFont = new Font("Verdana", Font.PLAIN, 16);
        Font columnHeaderFont = new Font("Verdana", Font.BOLD, 16);
        Font searchErrorFont = new Font("Verdana", Font.BOLD, 12);

        // Setting these font variables into different components
        titleLabel.setFont(titleFont);
        searchLabel.setFont(captionFont);
        searchTextField.setFont(dataFont);
        searchErrorLabel.setFont(searchErrorFont);
        designationTable.setFont(dataFont);

        searchErrorLabel.setForeground(Color.red); // setting text color of searchErrorLabel to red
        designationTable.setRowHeight(35); // setting height of each row in designationTable
        designationTable.getColumnModel().getColumn(0).setPreferredWidth(20); // setting width for first column (S.No)
                                                                              // of designationTable
        designationTable.getColumnModel().getColumn(1).setPreferredWidth(400);// setting width for second column
                                                                              // (Designation) of designationTable

        // getting the table header of designationTable
        JTableHeader header = designationTable.getTableHeader();
        header.setFont(columnHeaderFont); // setting font of header
        header.setReorderingAllowed(false); // not allowing user to drag the columns
        header.setResizingAllowed(false); // not allowing user to resize the columns

        designationTable.setRowSelectionAllowed(true); // allowing user to select row in table
        designationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // allowing only single selection

        container.setLayout(null); // setting container layout to null

        // variables
        int lm, tm; // left and top margin
        lm = 0;
        tm = 0;

        // setting dimensions and position of components
        // .................. x coordinate y coordinate
        titleLabel.setBounds(lm + 10, tm + 10, 200, 40);
        searchErrorLabel.setBounds(lm + 10 + 100 + 400 + 10 - 75, tm + 10 + 20 + 10, 100, 20);
        searchLabel.setBounds(lm + 10, tm + 10 + 40 + 10, 100, 30);
        searchTextField.setBounds(lm + 10 + 100 + 5, tm + 10 + 40 + 10, 400, 30);
        clearSearchTextFieldButton.setBounds(lm + 10 + 100 + 400 + 10, tm + 10 + 40 + 10, 30, 30);
        scrollPane.setBounds(lm + 10, tm + 10 + 40 + 10 + 30 + 10, 565, 300);
        designationPanel.setBounds(lm + 10, tm + 10 + 40 + 10 + 30 + 10 + 300 + 10, 565, 200);

        // adding components to the container
        container.add(titleLabel);
        container.add(searchErrorLabel);
        container.add(searchLabel);
        container.add(searchTextField);
        container.add(clearSearchTextFieldButton);
        container.add(scrollPane);
        container.add(designationPanel);

        // variables
        int w, h;
        w = 600;
        h = 680;
        setSize(w, h); // setting size of the widow
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // getting dimensions of the screen
        setLocation((d.width / 2) - (w / 2), (d.height / 2) - (h / 2)); // setting position of the window

    }

    private void addListeners() {
        searchTextField.getDocument().addDocumentListener(this); // adding DocumentListener to searchTextFeild and
                                                                 // passing the address of DesignationUI

        // adding ActionListener to cLearSearchTextField Button
        clearSearchTextFieldButton.addActionListener(new ActionListener() { // using anonymous class

            // writing the actionPerformed method
            public void actionPerformed(ActionEvent ev) {
                searchTextField.setText(""); // setting TextFeild to blank
                searchTextField.requestFocus(); // Focus on Search TextFeild
            }
        });

        // adding listener to the selected row
        designationTable.getSelectionModel().addListSelectionListener(this); // getting the selectionModel from the
                                                                             // designation table and adding
                                                                             // ListSelectionListener to it

    }

    private void searchDesignation() {
        searchErrorLabel.setText(""); // setting error label to blank
        String title = searchTextField.getText().trim(); // getting text from the text feild

        // validation
        if (title.length() == 0)
            return;

        // variable for row index of searched row
        int rowIndex;
        try {
            rowIndex = designationModel.indexOfTitle(title, true); // index of title will return the index if found
        } catch (BLException blException) {
            searchErrorLabel.setText("Not Found"); // in not found then displaying error label
            return;
        }

        // if we are here it means we found the designation seached
        designationTable.setRowSelectionInterval(rowIndex, rowIndex); // selecting the searched row
        Rectangle rectangle = designationTable.getCellRect(rowIndex, 0, true);
        designationTable.scrollRectToVisible(rectangle); // this method will scroll our rectangle to visible area in
                                                         // designationTable
    }

    // abstract method of DocumentListener
    public void changedUpdate(DocumentEvent de) {
        searchDesignation();
    }

    public void removeUpdate(DocumentEvent de) {
        searchDesignation();
    }

    public void insertUpdate(DocumentEvent de) {
        searchDesignation();
    }
    // (DocumentListener) abstract methods ends

    // abstract method of ListSelectionListener
    public void valueChanged(ListSelectionEvent ev) { // this method is called when the value of selection model is
                                                      // changed
        int selectedRowIndex = designationTable.getSelectedRow(); // getting the index of the selected row of
                                                                  // designation table
        try {
            DesignationInterface designation = designationModel.getDesignationAt(selectedRowIndex); // getting
                                                                                                    // designation
            designationPanel.setDesignation(designation); // setting this designation in designation panel
        } catch (BLException blException) {
            designationPanel.clearDesignation();
        }
    }
    // (ListSelectionListener) abstract methods ends

    private void setViewMode() {
        this.mode = MODE.VIEW; // setting view mode to VIEW

        // if list is empty
        if (designationModel.getRowCount() == 0) {
            searchTextField.setEnabled(false); // search textFeild is disabled
            clearSearchTextFieldButton.setEnabled(false); // clear button is also disabled
            designationTable.setEnabled(false); // designation table is disabled
        } else { // if designation table has designations
            searchTextField.setEnabled(true); // enabling search feild, clear button and designation table
            clearSearchTextFieldButton.setEnabled(true);
            designationTable.setEnabled(true);
        }
    }

    private void setAddMode() {
        this.mode = MODE.ADD; // setting view mode to ADD
        searchTextField.setEnabled(false); // search is disabled
        clearSearchTextFieldButton.setEnabled(false); // button is disabled
        designationTable.setEnabled(false); // designation table is disabled
    }

    private void setEditMode() {
        this.mode = MODE.EDIT; // setting view mode to EDIT
        searchTextField.setEnabled(false); // search is disabled
        clearSearchTextFieldButton.setEnabled(false); // button is disabled
        designationTable.setEnabled(false); // designation table is disabled
    }

    private void setDeleteMode() {
        this.mode = MODE.DELETE; // setting view mode to DELETE
        searchTextField.setEnabled(false);// search is disabled
        clearSearchTextFieldButton.setEnabled(false);// button is disabled
        designationTable.setEnabled(false);// designation table is disabled
    }

    private void setExportToPDFMode() {
        this.mode = MODE.EXPORT_TO_PDF; // setting view mode to export to pdf
        searchTextField.setEnabled(false);// search is disabled
        clearSearchTextFieldButton.setEnabled(false);// button is disabled
        designationTable.setEnabled(false);// designation table is disabled
    }

    // inner class starts
    class DesignationPanel extends JPanel { // Panel class
        // attributes
        private JLabel titleCaptionLabel;
        private JLabel titleLabel;
        private JTextField titleTextField;
        private JButton clearTitleTextFieldButton;
        private JButton addButton;
        private JButton editButton;
        private JButton cancelButton;
        private JButton deleteButton;
        private JButton exportToPDFButton;
        private JPanel buttonsPanel;

        private DesignationInterface designation;

        DesignationPanel() {
            setBorder(BorderFactory.createLineBorder(new Color(165, 165, 165))); // setting border of panel
            initComponents(); // initialising components
            setAppearance(); // setting their appereance
            addListeners(); // adding listeners
        }

        //
        public void setDesignation(DesignationInterface designation) {
            this.designation = designation;
            titleLabel.setText(designation.getTitle());
        }

        public void clearDesignation() {
            this.designation = null;
            titleLabel.setText("");
        }

        private void initComponents() {
            designation = null;

            // creating objects of components
            titleCaptionLabel = new JLabel("Designation");
            titleLabel = new JLabel("");
            titleTextField = new JTextField();
            clearTitleTextFieldButton = new JButton(clearIcon);
            buttonsPanel = new JPanel();
            addButton = new JButton(addIcon);
            editButton = new JButton(editIcon);
            cancelButton = new JButton(cancelIcon);
            deleteButton = new JButton(deleteIcon);
            exportToPDFButton = new JButton(pdfIcon);
        }

        private void setAppearance() {

            // creating fonts
            Font captionFont = new Font("Verdana", Font.BOLD, 16);
            Font dataFont = new Font("Verdana", Font.PLAIN, 16);

            // setting fonts
            titleCaptionLabel.setFont(captionFont);
            titleLabel.setFont(dataFont);
            titleTextField.setFont(dataFont);

            // setting layout
            setLayout(null);

            // variables
            int lm, tm;
            lm = 0;
            tm = 0;

            // setting dimensions and position of components
            titleCaptionLabel.setBounds(lm, tm + 10 + 5, 110, 30);
            titleLabel.setBounds(lm + 110 + 5, tm + 20, 400, 20);
            titleTextField.setBounds(lm + 10 + 110 + 5, tm + 20, 350, 30);
            clearTitleTextFieldButton.setBounds(lm + 10 + 110 + 5 + 350 + 5, tm + 20, 30,
                    30);
            buttonsPanel.setBounds(50, tm + 20 + 30 + 30, 465, 90); // 75 last one

            // setting border of buttons panel
            buttonsPanel.setBorder(BorderFactory.createLineBorder(new Color(165, 165,
                    165)));

            addButton.setBounds(70, 17, 50, 50); // 2nd one 12
            editButton.setBounds(70 + 50 + 20, 17, 50, 50);
            cancelButton.setBounds(70 + 50 + 20 + 50 + 20, 17, 50, 50);
            deleteButton.setBounds(70 + 50 + 20 + 50 + 20 + 50 + 20, 17, 50, 50);
            exportToPDFButton.setBounds(70 + 50 + 20 + 50 + 20 + 50 + 20 + 50 + 20, 17,
                    50, 50);

            // setting layout of buttons Panel
            buttonsPanel.setLayout(null);

            // adding component in buttons panel
            buttonsPanel.add(addButton);
            buttonsPanel.add(editButton);
            buttonsPanel.add(cancelButton);
            buttonsPanel.add(deleteButton);
            buttonsPanel.add(exportToPDFButton);

            // adding components to container
            add(titleCaptionLabel);
            add(titleTextField);
            add(titleLabel);
            add(clearTitleTextFieldButton);
            add(buttonsPanel);
        }

        // this method is called in the actionPerformed method of add button
        private boolean addDesignation() {
            String title = titleTextField.getText().trim(); // getting text from the text feild

            if (title.length() == 0) {
                JOptionPane.showMessageDialog(this, "Designation required"); // dailog of designation required
                titleTextField.requestFocus(); // text feild on focus
                return false; // returning false because not added
            }

            DesignationInterface d = new Designation(); // creating object of Designation
            d.setTitle(title); // setting title

            try {
                designationModel.add(d); // adding designation

                // getting row
                int rowIndex = 0;
                try {
                    rowIndex = designationModel.indexOfDesignation(d);
                } catch (BLException blException) {
                    // do nothing
                }

                // selcting row and scrolling to visible
                designationTable.setRowSelectionInterval(rowIndex, rowIndex);
                Rectangle rectangle = designationTable.getCellRect(rowIndex, 0, true);
                designationTable.scrollRectToVisible(rectangle);
                return true; // returning true

            } catch (BLException blException) {

                // if any exception
                if (blException.hasGenericException()) {
                    JOptionPane.showMessageDialog(this, blException.getGenericException()); // showing dialog of generic
                                                                                            // exception
                } else {
                    // any exception with title "title"
                    if (blException.hasException("title")) {
                        JOptionPane.showMessageDialog(this, blException.getException("title")); // showing dialog
                    }
                }
                titleTextField.requestFocus(); // requesting focus
                return false; // returning false coz not added
            }
        }

        private boolean updateDesignation() {
            String title = titleTextField.getText().trim(); // getting title
            if (title.length() == 0) {
                JOptionPane.showMessageDialog(this, "Designation required"); // showing dialog
                titleTextField.requestFocus();
                return false;// returning false
            }

            // creating designation object and setting code and title
            DesignationInterface d = new Designation();
            d.setCode(this.designation.getCode());
            d.setTitle(title);

            try {
                designationModel.update(d);// calling update method
                int rowIndex = 0;
                try {
                    rowIndex = designationModel.indexOfDesignation(d);
                } catch (BLException blException) {
                    // do nothing
                }

                // selecting updated designation and scroll it to visible
                designationTable.setRowSelectionInterval(rowIndex, rowIndex);
                Rectangle rectangle = designationTable.getCellRect(rowIndex, 0, true);
                designationTable.scrollRectToVisible(rectangle);
                return true; // returning true

            } catch (BLException blException) {

                // if any generic exception
                if (blException.hasGenericException()) {
                    JOptionPane.showMessageDialog(this, blException.getGenericException());
                } else {

                    // if any exception in title
                    if (blException.hasException("title")) {
                        JOptionPane.showMessageDialog(this, blException.getException("title"));
                    }
                }

                // requesting focus and return false
                titleTextField.requestFocus();
                return false;
            }
        }

        private void removeDesignation() {
            try {
                String title = this.designation.getTitle(); // getting title

                // showing confirm dialog with yes and no option
                int selectedOption = JOptionPane.showConfirmDialog(this, "Delete " + title +
                        " ?", "Confirmation",
                        JOptionPane.YES_NO_OPTION);

                // if no or close then do nothing and return
                if (selectedOption == JOptionPane.NO_OPTION)
                    return;
                if (selectedOption == JOptionPane.CLOSED_OPTION)
                    return;

                // calling remove method of designationModel
                designationModel.remove(this.designation.getCode());
                JOptionPane.showMessageDialog(this, title + " deleted"); // showing message that it is deleted

                // calling clearDesignation
                this.clearDesignation();

            } catch (BLException blException) {
                // exceptions
                if (blException.hasGenericException()) {
                    JOptionPane.showMessageDialog(this, blException.getGenericException());
                } else {
                    if (blException.hasException("title")) {
                        JOptionPane.showMessageDialog(this, blException.getException("title"));
                    }
                }
            }
        }

        private void addListeners() {
            this.exportToPDFButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File("."));
                    int selectedOption = jfc.showSaveDialog(DesignationUI.this);
                    if (selectedOption == JFileChooser.APPROVE_OPTION) {
                        try {
                            File selectedFile = jfc.getSelectedFile();
                            String pdfFile = selectedFile.getAbsolutePath();
                            if (pdfFile.endsWith("."))
                                pdfFile += "pdf";
                            else if (pdfFile.endsWith(".pdf") == false)
                                pdfFile += ".pdf";
                            File file = new File(pdfFile);
                            File parent = new File(file.getParent());
                            if (parent.exists() == false || parent.isDirectory() == false)
                                if (new File(file.getParent()).exists() == false) {
                                    JOptionPane.showMessageDialog(DesignationUI.this,
                                            "Incorrect path : " + file.getAbsolutePath());
                                    return;
                                }
                            designationModel.exportToPDF(file);
                            JOptionPane.showMessageDialog(DesignationUI.this,
                                    "Data exported to " + file.getAbsolutePath());
                            // System.out.println(selectFile.getAbsolutePath());
                        } catch (BLException blException) {
                            if (blException.hasGenericException()) {
                                JOptionPane.showMessageDialog(DesignationUI.this,
                                        blException.getGenericException());
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }

            });

            // adding listener to add button
            this.addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    // if we are currently in view mode then switch to add mode
                    if (mode == MODE.VIEW) {
                        setAddMode();
                    } else {
                        // calling addDesignation and setting view mode to view
                        if (addDesignation()) {
                            setViewMode();
                        }
                    }
                }
            });

            // adding listener to edit button
            this.editButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    // if we are currently in view mode then switch to edit mode
                    if (mode == MODE.VIEW) {
                        setEditMode();
                    } else {
                        // calling updateDesignation and setting view mode to view
                        if (updateDesignation()) {
                            setViewMode();
                        }
                    }
                }
            });

            // adding listener to cancel button
            this.cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setViewMode(); // just set to view mode
                }
            });

            // adding listener to delete button
            this.deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    setDeleteMode(); // set to delete mode
                }
            });

            // adding listener to clearTextFeild button
            this.clearTitleTextFieldButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {

                    // clearing text field and requesting focus
                    titleTextField.setText("");
                    titleTextField.requestFocus();
                }
            });

        }

        // this fuction is to set the view mode
        void setViewMode() {
            DesignationUI.this.setViewMode(); // calling the setViewMode function of outer class (DesignationUI)

            // setting button icons
            this.addButton.setIcon(addIcon);
            this.editButton.setIcon(editIcon);

            // hiding the title text field and clear button
            this.titleTextField.setVisible(false);
            this.clearTitleTextFieldButton.setVisible(false);

            // enabling title label and add button
            this.titleLabel.setVisible(true);
            this.addButton.setEnabled(true);

            // disabling cancel button
            this.cancelButton.setEnabled(false);

            // if table is not empty
            if (designationModel.getRowCount() > 0) {
                // enable edit, delete and export to pdf button
                this.editButton.setEnabled(true);
                this.deleteButton.setEnabled(true);
                this.exportToPDFButton.setEnabled(true);
            } else {
                // disable buttons
                this.editButton.setEnabled(false);
                this.deleteButton.setEnabled(false);
                this.exportToPDFButton.setEnabled(false);
            }
        }

        // this function is to set add mode
        void setAddMode() {
            DesignationUI.this.setAddMode(); // calling the setAddMode function of outer class (DesignationUI)

            this.titleTextField.setText(""); // blank title text field
            this.titleLabel.setVisible(false); // hiding title label

            // enabling clear button and title text feild
            this.clearTitleTextFieldButton.setVisible(true);
            this.titleTextField.setVisible(true);

            // now add button is save button
            addButton.setIcon(saveIcon);

            // other buttons are disabled
            editButton.setEnabled(false);
            cancelButton.setEnabled(true);
            deleteButton.setEnabled(false);
            exportToPDFButton.setEnabled(false);
        }

        // this function is to set edit mode
        void setEditMode() {

            // means if there are 10 records then it should be from 0 to 9
            if (designationTable.getSelectedRow() < 0
                    || designationTable.getSelectedRow() >= designationModel.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Select designation to edit"); // message dialog will appear
                return;
            }

            DesignationUI.this.setEditMode();
            this.titleTextField.setText(this.designation.getTitle());
            this.titleLabel.setVisible(false);
            this.clearTitleTextFieldButton.setVisible(true);
            this.titleTextField.setVisible(true);

            // only cancel button is enabled
            addButton.setEnabled(false);
            cancelButton.setEnabled(true);
            deleteButton.setEnabled(false);
            exportToPDFButton.setEnabled(false);

            // edit is save
            editButton.setIcon(saveIcon);
        }

        void setDeleteMode() {
            if (designationTable.getSelectedRow() < 0
                    || designationTable.getSelectedRow() >= designationModel.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Select designation to delete"); // message dialog will appear
                return;
            }
            DesignationUI.this.setDeleteMode();

            // all buttons disabled
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            cancelButton.setEnabled(false);
            deleteButton.setEnabled(false);
            exportToPDFButton.setEnabled(false);

            // calling remove designation
            removeDesignation();

            // setting to view mode after deleting designation
            DesignationUI.this.setViewMode();
            this.setViewMode();
        }

        void setExportToPDFMode() {
            DesignationUI.this.setExportToPDFMode();

            // all buttons disabled
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            cancelButton.setEnabled(false);
            deleteButton.setEnabled(false);
            exportToPDFButton.setEnabled(false);
        }

    }// inner class ends

}// class ends