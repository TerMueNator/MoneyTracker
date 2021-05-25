package ui;

import exception.ZeroInputException;
import model.ExpenseEntry;
import model.ExpenseRecord;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static persistence.JsonReader.JSON_LOCATION;

// GUI version of Expense Recorder App
public class ExpenseRecordAppGUI {

    private ExpenseRecord record;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;
    private JFrame mainFrame;
    private JPanel buttonsPanel;
    private JButton addButton;
    private JButton editButton;
    private JButton removeButton;
    private JTable recordTable;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private boolean editMade = false;

    public static Map<String, String> constants;

    // EFFECTS: initializes fields, then run the program
    public ExpenseRecordAppGUI() {
        setupConstantsMap();
        setupFields();
        preAppLoadCheck();
    }

    private void setupConstantsMap() {
        constants = new HashMap<>();
        constants.put("JSON_LOCATION", "./data/record.json");
        constants.put("JSON_OBJECT_NAME_KEY", "name");
        constants.put("JSON_OBJECT_AMOUNT_KEY", "amount");
        constants.put("JSON_OBJECT_CATEGORY_KEY", "category");
        constants.put("JSON_ARRAY_KEY", "entries");
    }

    // EFFECTS: sets up fields necessary for program functionalities
    private void setupFields() {
        jsonReader = new JsonReader(JSON_LOCATION);
        jsonWriter = new JsonWriter(JSON_LOCATION);

        tableModel = new DefaultTableModel();
        recordTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // EFFECTS: checks if save file available, then run setupAppUI
    private void preAppLoadCheck() {
        if (loadRecords()) {
            String chosenString = showPopupAlert("Alert", "save found, load?", PopupType.CONFIRM);
            assert chosenString != null;
            int chosenOption = Integer.parseInt(chosenString);
            if (chosenOption == JOptionPane.NO_OPTION) {
                record = new ExpenseRecord();
            }
        }
        setupAppUI();
    }

    // EFFECTS: sets up GUI for program
    private void setupAppUI() {
        setupMainframeTable();
        setupMainframeButtons();
        setupMainframeFrame();
        handleMainframeButtonActions();
    }

    // EFFECTS: sets up main ui for program
    private void setupMainframeFrame() {
        mainFrame = createMainJFrame();
        mainFrame.add(scrollPane);
        mainFrame.add(buttonsPanel, BorderLayout.SOUTH);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (editMade) {
                    String chosenString = showPopupAlert("close window?",
                            "save changes before exit?",
                            PopupType.CONFIRM);
                    assert chosenString != null;
                    int chosenOption = Integer.parseInt(chosenString);
                    if (chosenOption == JOptionPane.YES_OPTION) {
                        saveRecords();
                    }
                }
                System.exit(0);
            }
        });
        mainFrame.setTitle(printTotalExpenseOrIncome());
    }

    // EFFECTS: sets up 3 buttons for program
    private void setupMainframeButtons() {
        buttonsPanel = new JPanel();
        buttonsPanel.setSize(500,50);
        addButton = new JButton("add entry");
        editButton = new JButton("edit entry");
        removeButton = new JButton("remove entry");
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(removeButton);
    }

    // EFFECTS: sets up table for program
    private void setupMainframeTable() {
        recordTable.setCellSelectionEnabled(false);
        tableModel.addColumn("row");
        tableModel.addColumn("name");
        tableModel.addColumn("amount");
        tableModel.addColumn("category");
        tableModel.addColumn("type");
        for (ExpenseEntry ee: record.getEntryList()) {
            tableModel.insertRow(tableModel.getRowCount(),
                    new Object[] {tableModel.getRowCount() + 1,
                            ee.getEntryName(),
                            ee.getAmount(),
                            ee.getCategory(),
                            ee.isExpenseOrIncome()});
        }
        scrollPane = new JScrollPane(recordTable);
    }

    // EFFECTS: handles actions for button presses
    private void handleMainframeButtonActions() {
        addButton.addActionListener(e -> handleMainAddButtonAction());
        removeButton.addActionListener(e -> handleMainRemoveButtonAction());
        editButton.addActionListener(e -> handleMainEditButtonAction());
    }

    // EFFECTS: handles actions for add button
    private void handleMainEditButtonAction() {
        int rowNumber = recordTable.getSelectedRow();
        int columnNumber = recordTable.getSelectedColumn();
        System.out.println("row:" + rowNumber + " col: " + columnNumber);
        if (isInvalidCellSelected(rowNumber, columnNumber)) {
            return;
        }

        String newValue = showPopupAlert("alert", "enter new value for cell", PopupType.INPUT);
        if (newValue == null) {
            playFailureSound();
            return;
        }

        ExpenseEntry ee = record.getEntryFromIndexNumber(rowNumber);
        handleEditByColumnNumber(rowNumber, columnNumber, newValue, ee);
    }

    // helper method for handleMainEditButtonAction
    // MODIFIES: record
    // EFFECTS:  modifies data accordingly based on rowNumber and columnNumber
    private void handleEditByColumnNumber(int rowNumber, int columnNumber, String newValue, ExpenseEntry ee) {
//        ExpenseEntry ee = record.getEntryFromIndexNumber(rowNumber);
//        System.out.println(recordTable.getColumnName(columnNumber));
        switch (columnNumber) {
            case 1:
                recordTable.setValueAt(newValue, rowNumber, columnNumber);
                ee.setEntryName(newValue);
                break;
            case 2:
                try {
                    double newAmount = Double.parseDouble(newValue);
                    ee.setAmount(newAmount);
                    recordTable.setValueAt(newAmount, rowNumber, columnNumber);
                    recordTable.setValueAt(isAmountExpenseOrIncome(newAmount), rowNumber, 4);
                } catch (NumberFormatException | ZeroInputException e) {
                    playFailureSound();
                    showPopupAlert("EDIT ERROR", "requires non-zero number", PopupType.MESSAGE);
                    return;
                }
            case 3:
                recordTable.setValueAt(newValue, rowNumber, columnNumber);
                ee.setCategory(newValue);
            default:
        }
        changeSuccessful();
    }

    // helper method for handleMainEditButtonAction
    // EFFECTS: return true if rowNumber and columnNumber is invalid for editing
    //          returns false otherwise
    private boolean isInvalidCellSelected(int rowNumber, int columnNumber) {
        if (rowNumber == -1 || columnNumber == -1) {
            playFailureSound();
            showPopupAlert("error", "select a cell first", PopupType.MESSAGE);
            return true;
        } else if (columnNumber == 0 || columnNumber == 4) {
            playFailureSound();
            showPopupAlert("error", "select column 2-4 only", PopupType.MESSAGE);
            return true;
        }
        return false;
    }

    // MODIFIES: record
    // EFFECTS:  handles action when remove button pressed
    private void handleMainRemoveButtonAction() {
        try {
            String rowNumberString = showPopupAlert("Alert", "enter row number to remove",PopupType.INPUT);
            if (rowNumberString != null) {
                int rowNumber = Integer.parseInt(rowNumberString) - 1;
                tableModel.removeRow(rowNumber);
                record.removeEntry(rowNumber);
                for (int i = 0; i < recordTable.getRowCount(); i++) {
                    recordTable.setValueAt(i + 1, i, 0);
                }
                changeSuccessful();
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            playFailureSound();
            showPopupAlert("ERROR",
                    "input number between 1 and " + recordTable.getRowCount(),
                    PopupType.MESSAGE);
        }
    }

    // MODIFIES: record
    // EFFECTS:  handles action when add button pressed
    private void handleMainAddButtonAction() {
        try {
            String newName = showPopupAlert("", "enter new entry name", PopupType.INPUT);
            if (newName != null) {
                String newAmountString = showPopupAlert("", "enter amount", PopupType.INPUT);
                if (newAmountString != null) {
                    double newAmount = Double.parseDouble(newAmountString);
                    String newCategory = showPopupAlert("", "enter category name", PopupType.INPUT);
                    if (newCategory != null) {
                        tableModel.insertRow(tableModel.getRowCount(),
                                new Object[] {tableModel.getRowCount() + 1,
                                        newName,
                                        newAmount,
                                        newCategory,
                                        isAmountExpenseOrIncome(newAmount)});
                        record.addEntry(new ExpenseEntry(newName, newAmount, newCategory));
                        changeSuccessful();
                    }
                }
            }
        } catch (NumberFormatException e) {
            playFailureSound();
            showPopupAlert("ERROR", "requires non-zero number", PopupType.MESSAGE);
        }
    }

    // EFFECTS: display appropriate popup menu type with title, message based on popupType
    private String showPopupAlert(String title, String message, PopupType popupType) {
        switch (popupType) {
            case CONFIRM:
                int chosenOption = JOptionPane.showConfirmDialog(mainFrame,
                        message,
                        title,
                        JOptionPane.YES_NO_OPTION);
                return String.valueOf(chosenOption);
            case MESSAGE:
                JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.INFORMATION_MESSAGE);
                return "";
            case INPUT:
                return JOptionPane.showInputDialog(mainFrame, message, title);
            default:
                return null;
        }
    }

    // EFFECTS: try to load record from json file to record
    //          if load successful return true, return
    //          false otherwise
    private boolean loadRecords() {
        try {
            record = jsonReader.read();
            return true;
        } catch (IOException e) {
            System.out.println(JSON_LOCATION + " cannot be found. new record will be created");
            return false;
        }
    }

    // EFFECTS: try to save record to json file
    private void saveRecords() {
        try {
            jsonWriter.open();
            jsonWriter.write(record);
            jsonWriter.close();
            System.out.println("saved record to " + JSON_LOCATION);
            editMade = false;
        } catch (FileNotFoundException e) {
            System.out.println("ERROR : unable to save record to " + JSON_LOCATION);
        }
    }

    // EFFECTS: return income if amount > 0, return expense otherwise
    private String isAmountExpenseOrIncome(double amount) {
        if (amount > 0) {
            return "income";
        } else {
            return "expense";
        }
    }

    // EFFECTS: returns appropriate total string based on total expense/income
    private String printTotalExpenseOrIncome() {
        double total = 0;
        for (ExpenseEntry ee: record.getEntryList()) {
            total += ee.getAmount();
        }
        StringBuilder sb = new StringBuilder();
        if (total > 0) {
            sb.append("income of: ");
            sb.append(total);
        } else if (total < 0) {
            sb.append("expense of: ");
            sb.append(total);
        } else {
            sb.append("no net expense");
        }
        return sb.toString();
    }

    // different popup types
    private enum PopupType {
        MESSAGE, INPUT, CONFIRM
    }

    // EFFECTS: creates, initialize and return jframe for use in mainframe
    private JFrame createMainJFrame() {
        JFrame frame = new JFrame("Expense Recorder");
        frame.setLocationRelativeTo(null);
        frame.setSize(500, 500);
        frame.setVisible(true);
        return frame;
    }

    // EFFECTS: plays success sound
    private void playSuccessSound() {
        try {
            String sep = System.getProperty("file.separator");
            File soundFile = new File(System.getProperty("user.dir") + sep
                    + "resources" + sep + "success.wav");
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundFile.getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("ERROR PLAY SOUND SUCCESS");
        }
    }

    // EFFECTS: plays failure sound
    private void playFailureSound() {
        try {
            String sep = System.getProperty("file.separator");
            File soundFile = new File(System.getProperty("user.dir") + sep
                    + "resources" + sep + "failure.wav");
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundFile.getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("ERROR PLAY SOUND FAILURE");
        }
    }

    // EFFECTS: runs when change made successfully
    //          plays sound, change editMade boolean,
    //          change mainFrame title
    private void changeSuccessful() {
        editMade = true;
        playSuccessSound();
        mainFrame.setTitle(printTotalExpenseOrIncome());
    }

//    // different jobject types
//    private enum JObjectType {
//        LABEL, BUTTON, TEXTFIELD
//    }
//
//    // EFFECTS: creates, initializes and returns j object based on specified parameters
//    private JComponent createJObject(JObjectType objectType, int width, int height, int x, int y, String title) {
//        switch (objectType) {
//            case LABEL:
//                JLabel label = new JLabel(title);
//                label.setBounds(x,y,width,height);
//                return label;
//            case BUTTON:
//                JButton button = new JButton(title);
//                button.setBounds(x,y,width,height);
//                return button;
//            case TEXTFIELD:
//                JTextField textField = new JTextField();
//                textField.setBounds(x,y,width,height);
//                return textField;
//            default:
//                return null;
//        }
//    }
//
//    // EFFECTS: creates, initializes and returns jlabel based on specified parameters
//    private JLabel createJLabel(int width, int height, int x, int y, String title) {
//        return (JLabel) createJObject(JObjectType.LABEL, width, height, x, y, title);
//    }
//
//    // EFFECTS: creates, initializes and returns jbutton based on specified parameters
//    private JButton createJButton(int width, int height, int x, int y, String title) {
//        return (JButton) createJObject(JObjectType.BUTTON, width, height, x, y, title);
//    }
//
//    // EFFECTS: creates, initializes and returns jtextfield based on specified parameters
//    private JTextField createJTextfield(int width, int height, int x, int y, String title) {
//        return (JTextField) createJObject(JObjectType.TEXTFIELD, width, height, x, y, title);
//    }

}
