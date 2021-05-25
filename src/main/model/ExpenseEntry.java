package model;

import exception.ZeroInputException;
import org.json.JSONObject;
import persistence.Writable;

import static persistence.JsonReader.*;

// Represents each entry containing entry information
public class ExpenseEntry implements Writable {

    private String entryName;
    private double amount;
    private String expenseOrIncome;
    private String category;

    /*
     * REQUIRES: entryName not null, category not null
     * EFFECTS:  creates new ExpenseEntry object with
     *           entryName, amount and category. If amount < 0,
     *           expenseOrIncome gets expense, else expenseOrIncome gets income
     *           if amount = 0, assign it to 1
     */
    public ExpenseEntry(String entryName, double amount, String category) {
        setEntryName(entryName);
        setCategory(category);
        try {
            setAmount(amount);
        } catch (ZeroInputException e) {
            this.amount = 1;
        }
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String name) {
        this.entryName = name;
    }

    public double getAmount() {
        return amount;
    }

    // MODIFIES: this
    // EFFECTS:  sets expenseOrIncome string accordingly based on amount
    //           if amount is 0, throw ZeroInputException
    public void setAmount(double amount) throws ZeroInputException {
        if (amount == 0) {
            throw new ZeroInputException();
        }
        this.amount = amount;
        expenseOrIncome = (amount < 0) ? "expense" : "income";
    }

    public String isExpenseOrIncome() {
        return expenseOrIncome;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /*
     * EFFECTS: returns JSON representation of each entry object
     */
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_OBJECT_NAME_KEY, entryName);
        jsonObject.put(JSON_OBJECT_AMOUNT_KEY, amount);
        jsonObject.put(JSON_OBJECT_CATEGORY_KEY, category);
        return jsonObject;
    }
}
