package model;

import exception.ZeroInputException;
import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

import static persistence.JsonReader.JSON_ARRAY_KEY;

// Represents a record of all ExpenseEntry objects in a list
public class ExpenseRecord implements Writable {

    private List<ExpenseEntry> entryList;

    // EFFECTS: create a new list which holds ExpenseEntry objects
    public ExpenseRecord() {
        entryList = new ArrayList<>();
    }

    /*
     * REQUIRES: entry not null
     * MODIFIES: this
     * EFFECTS:  adds entry to entryList
     */
    public void addEntry(ExpenseEntry entry) {
        entryList.add(entry);
    }

    /*
     * MODIFIES: this
     * EFFECTS:  removes entry with specified entryNumber from entryList
     */
    public boolean removeEntry(int index) {
        return entryList.remove(entryList.get(index));
    }

    /*
     * REQUIRES: index >= 0 and index <entryList.size()
     * MODIFIES: this
     * EFFECTS:  edits amount of entry with specified entry number with newAmount
     */
    public void editEntryAmount(int index, double newAmount) {
        try {
            ExpenseEntry e = entryList.get(index);
            e.setAmount(newAmount);
        } catch (ZeroInputException e) {
            e.printStackTrace();
        }
    }

    /*
     * REQUIRES: index >= 0 and index <entryList.size()
     * MODIFIES: this
     * EFFECTS:  edits name of entry with specified index with newName
     */
    public void editEntryName(int index, String newName) {
        ExpenseEntry e = entryList.get(index);
        e.setEntryName(newName);
    }

    /*
     * REQUIRES: index >= 0 and index <entryList.size()
     * MODIFIES: this
     * EFFECTS:  edits category of entry with specified index with newCategoryName
     */
    public void editCategoryName(int index, String newCategoryName) {
        entryList.get(index).setCategory(newCategoryName);
    }

    /*
     * EFFECTS: returns string containing information of entryList
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (entryList.isEmpty()) {
            return "record is empty";
        }

        for (ExpenseEntry ee: entryList) {
            sb.append("id: ").append(entryList.indexOf(ee))
                    .append("   ").append("name: ").append(ee.getEntryName())
                    .append("   ").append("amount: ").append(ee.getAmount())
                    .append("   ")
                    .append(ee.isExpenseOrIncome())
                    .append("   ").append("category: ").append(ee.getCategory())
                    .append("\n");
        }
        if (getTotalExpense() < 0) {
            sb.append("total expenses: ").append(-1.0 * getTotalExpense());
        } else if (getTotalExpense() > 0) {
            sb.append("total income: ").append(getTotalExpense());
        } else {
            sb.append("net income/expense is 0");
        }

        return sb.toString();
    }

    /*
     * REQUIRES: index >= 0 and index <entryList.size()
     * EFFECTS:  returns ExpenseEntry object with specified entryNumber
     */
    public ExpenseEntry getEntryFromIndexNumber(int index) {
        return entryList.get(index);
    }

    /*
     * EFFECTS:  returns total expense of entries in list
     */
    public double getTotalExpense() {
        double expense = 0;
        for (ExpenseEntry e: entryList) {
            expense += e.getAmount();
        }
        return expense;
    }

    /*
     * EFFECTS:  returns size of entryList
     */
    public int size() {
        return entryList.size();
    }

    /*
     * EFFECTS:  returns true if entryList is empty, false otherwise
     */
    public boolean isEmpty() {
        return entryList.isEmpty();
    }

    /*
     * EFFECTS: returns list of all ExpenseEntry item in record
     */
    public List<ExpenseEntry> getEntryList() {
        return entryList;
    }

    /*
     * EFFECTS: returns JSON representation of this
     */
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ARRAY_KEY, entriesToJson());
        return jsonObject;
    }

    /*
     * EFFECTS: adds item in entry list to JSON array, returns that array
     */
    private JSONArray entriesToJson() {
        JSONArray jsonArray = new JSONArray();

        for (ExpenseEntry ee: entryList) {
            jsonArray.put(ee.toJson());
        }

        return jsonArray;
    }
}
