package persistence;

import model.ExpenseRecord;
import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// A JSON writer representation to write to JSON files
// Adapted from: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
public class JsonWriter {

    private final String destination;
    private PrintWriter printWriter;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    // be opened for writing
    public void open() throws FileNotFoundException {
        printWriter = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of ExpenseRecord to file
    public void write(ExpenseRecord record) {
        JSONObject json = record.toJson();
        printWriter.print(json.toString(4));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        printWriter.close();
    }

}
