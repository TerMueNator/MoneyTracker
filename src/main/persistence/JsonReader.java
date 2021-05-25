package persistence;

import model.ExpenseEntry;
import model.ExpenseRecord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

// A JSON reader representation to read JSON files
// Adapted from: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
public class JsonReader {

    public static final String JSON_ARRAY_KEY = "entries";
    public static final String JSON_OBJECT_NAME_KEY = "name";
    public static final String JSON_OBJECT_AMOUNT_KEY = "amount";
    public static final String JSON_OBJECT_CATEGORY_KEY = "category";
    public static final String JSON_LOCATION = "./data/record.json";

    private final String source;

    // EFFECTS: construct reader object to read source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads expense record from saved JSON file and returns it
    //          throws IOException if error occurs while reading
    public ExpenseRecord read() throws IOException {
        String jsonString = readFile(source);
        JSONObject object = new JSONObject(jsonString);
        return configureRecord(object);
    }

    // EFFECTS: returns source file as string format
    //          throws IOException if source file not found
    private String readFile(String source) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(new File(source));
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.next());
        }
        return stringBuilder.toString();
    }

    // EFFECTS: configure expense record from saved JSON file
    private ExpenseRecord configureRecord(JSONObject jsonObject) {
        ExpenseRecord record = new ExpenseRecord();
        addRecords(record, jsonObject);

        return record;
    }

    // MODIFIES: record
    // EFFECTS:  retrieves all entries from JSON object and adds it to record
    private void addRecords(ExpenseRecord record, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_KEY);
        for (Object object: jsonArray) {
            JSONObject nextObject = (JSONObject) object;
            addRecord(record, nextObject);
        }
    }

    // MODIFIES: record
    // EFFECTS:  retrieves individual expense entry from JSON object and adds it to record
    private void addRecord(ExpenseRecord record, JSONObject jsonObject) {
        String name = jsonObject.getString(JSON_OBJECT_NAME_KEY);
        double amount = jsonObject.getDouble(JSON_OBJECT_AMOUNT_KEY);
        String category = jsonObject.getString(JSON_OBJECT_CATEGORY_KEY);
        ExpenseEntry ee = new ExpenseEntry(name, amount, category);
        record.addEntry(ee);
    }

}
