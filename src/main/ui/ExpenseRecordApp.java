//package ui;
//
//import model.ExpenseEntry;
//import model.ExpenseRecord;
//import persistence.JsonReader;
//import persistence.JsonWriter;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.InputMismatchException;
//import java.util.Scanner;
//
//import static persistence.JsonReader.JSON_LOCATION;
//
//// Expense Recorder App
//public class ExpenseRecordApp {
//
//    private ExpenseRecord record;
//    private Scanner scanner;
//    private JsonReader jsonReader;
//    private JsonWriter jsonWriter;
//    boolean editMade = false;
//
//    // EFFECTS: runs the program
//    public ExpenseRecordApp() {
//        runApplication();
//    }
//
//    // MODIFIES: this
//    // EFFECTS:  initializes new record and necessary functions, handles user inputs
//    private void runApplication() {
//        record = new ExpenseRecord();
//        scanner = new Scanner(System.in);
//        jsonReader = new JsonReader(JSON_LOCATION);
//        jsonWriter = new JsonWriter(JSON_LOCATION);
//
//        String input;
//
//        preAppLoadCheck();
//
//        do {
//            scanner = new Scanner(System.in);
//            showOptions();
//            System.out.print("type your input: ");
//            input = scanner.next().toLowerCase();
//            try {
//                processMenuInput(input);
//            } catch (IndexOutOfBoundsException e) {
//                System.out.println("specified index out of bounds");
//            } catch (InputMismatchException e) {
//                System.out.println("cannot enter string into numbers");
//            } catch (Exception e) {
//                System.out.println("unknown error occurred");
//            }
//        } while (!input.equals("q"));
//    }
//
//    // EFFECTS: prints available options users can use in the program
//    private void showOptions() {
//        System.out.println("-------------------------------");
//        System.out.println("p --> print all entries");
//        System.out.println("n --> new entry");
//        System.out.println("d --> deleting entry");
//        System.out.println("e --> editing entry");
//        System.out.println("q --> quit");
//        System.out.println("-------------------------------");
//    }
//
//    // EFFECTS: processes user inputs and run appropriate functions accordingly
//    private void processMenuInput(String in) {
//        switch (in) {
//            case "p":
//                listOfEntries();
//                break;
//            case "n":
//                newEntry();
//                break;
//            case "d":
//                deleteEntry();
//                break;
//            case "e":
//                editEntry();
//                break;
//            case "q":
//                checkBeforeQuit();
//                break;
//            default:
//                System.out.println("invalid choice. choose from list");
//                break;
//        }
//    }
//
//    // EFFECTS: prints list of all entries in record
//    private void listOfEntries() {
//        System.out.println("your expense record: ");
//        System.out.println(record.toString());
//    }
//
//    // MODIFIES: this
//    // EFFECTS:  handles adding new entry to record
//    private void newEntry() {
//        System.out.print("new entry name: ");
//        String name = scanner.next();
//        while (name.equals("")) {
//            System.out.println("entry name cannot be empty. try again");
//            name = scanner.next();
//        }
//
//        System.out.println("enter amount (negative number indicates expense, positive indicates income): ");
//        double amount = scanner.nextDouble();
//
//        System.out.println("enter category name: ");
//        String category = scanner.next();
//
//        ExpenseEntry entry = new ExpenseEntry(name, amount, category);
//        record.addEntry(entry);
//        editMade = true;
//
//        listOfEntries();
//    }
//
//    // MODIFIES: this
//    // EFFECTS:  handles removing entry from record
//    private void deleteEntry() {
//        if (record.isEmpty()) {
//            System.out.println("record is empty, nothing to delete");
//            return;
//        }
//        System.out.println("enter index number of entry you want to delete: ");
//        int in = scanner.nextInt();
//        if (record.removeEntry(in)) {
//            System.out.println("entry removed successfully");
//            editMade = true;
//        } else {
//            System.out.println("error removing entry. check your entry number and try again");
//        }
//
//        listOfEntries();
//    }
//
//    // MODIFIES: this
//    // EFFECTS:  handles editing entry in record, throws exceptions when unexpected inputs given
//    private void editEntry() {
//        if (record.isEmpty()) {
//            System.out.println("record is empty, nothing to edit");
//            return;
//        }
//        System.out.print("enter index number of entry to edit: ");
//        int index = scanner.nextInt();
//        System.out.print("a --> edit amount, n --> edit name, c --> edit category: ");
//        String s = scanner.next();
//        editEntryCommandHelper(s, index);
//        editMade = true;
//
//        listOfEntries();
//    }
//
//    // MODIFIES: this
//    // EFFECTS:  handles user commands accordingly
//    private void editEntryCommandHelper(String s, int index) {
//        switch (s) {
//            case "a":
//                System.out.print("enter new amount: ");
//                record.editEntryAmount(index, scanner.nextDouble());
//                break;
//            case "n":
//                System.out.print("enter new name: ");
//                record.editEntryName(index, scanner.next());
//                break;
//            case "c":
//                System.out.println("enter new category name: ");
//                record.editCategoryName(index, scanner.next());
//                break;
//            default:
//                System.out.println("invalid input");
//                break;
//        }
//    }
//
//    // EFFECTS: saves record to JSON_LOCATION
//    private void saveRecords() {
//        try {
//            jsonWriter.open();
//            jsonWriter.write(record);
//            jsonWriter.close();
//            System.out.println("saved record to " + JSON_LOCATION);
//            editMade = false;
//        } catch (FileNotFoundException e) {
//            System.out.println("ERROR : unable to save record to " + JSON_LOCATION);
//        }
//    }
//
//    // MODIFIES: this
//    // EFFECTS:  try to load record from JSON_LOCATION
//    //           returns true if load successful, false otherwise
//    private boolean loadRecords() {
//        try {
//            record = jsonReader.read();
//            return true;
//        } catch (IOException e) {
//            System.out.println(JSON_LOCATION + " cannot be found. new record will be created");
//            return false;
//        }
//    }
//
//    // EFFECTS: ask if user want to save record before quitting if change is made
//    private void checkBeforeQuit() {
//        if (editMade) {
//            System.out.println("changes made to record, save changes before quitting?");
//            System.out.println("y -> yes, n -> no");
//            String s = scanner.next();
//            while (!s.equals("y") && !s.equals("n")) {
//                System.out.println("invalid input, try again");
//                s = scanner.next();
//            }
//            if ("y".equals(s)) {
//                saveRecords();
//            }
//        }
//    }
//
//    // EFFECTS: ask if user wants to load record from file if file is found
//    private void preAppLoadCheck() {
//        String input;
//        if (loadRecords()) {
//            System.out.println("saved file found. load save? y -> yes, n -> no");
//            input = scanner.next();
//            while (!input.equals("y") && !input.equals("n")) {
//                System.out.println("invalid input, try again");
//                input = scanner.next();
//            }
//            if (input.equals("n")) {
//                record = new ExpenseRecord();
//            }
//        }
//    }
//
//}
