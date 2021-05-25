package model;

import exception.ZeroInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseRecordTest {

    ExpenseRecord record;

    @BeforeEach
    void setup() {
        record = new ExpenseRecord();
        record.addEntry(new ExpenseEntry("n1", -111, ""));
    }

    @Test
    void testAddEntry() {
        assertEquals(1, record.size());
        assertEquals("n1", record.getEntryFromIndexNumber(0).getEntryName());
        assertEquals(-111, record.getEntryFromIndexNumber(0).getAmount());
    }

    @Test
    void testDeleteValidEntry() {
        record.addEntry(new ExpenseEntry("n2", 200, ""));

        record.removeEntry(1);

        assertEquals(1, record.size());

        record.removeEntry(0);

        assertEquals(0, record.size());
        assertTrue(record.isEmpty());

    }

    @Test
    void testEditNameValidEntry() {
        record.editEntryName(0, "newname1");
        assertEquals(record.getEntryFromIndexNumber(0).getEntryName(), "newname1");
    }

    @Test
    void testEditAmountValidEntry() {
        record.editEntryAmount(0, 999);
        assertEquals(record.getEntryFromIndexNumber(0).getAmount(), 999);
    }

    @Test
    void testEditAmountZeroEntry() {
        record.editEntryAmount(0, 0);
        assertEquals(record.getEntryFromIndexNumber(0).getAmount(), -111);
    }

    @Test
    void testIsExpense() {
        record.addEntry(new ExpenseEntry("ex", -111, ""));
        record.addEntry(new ExpenseEntry("in", 111, ""));

        assertEquals(record.getEntryFromIndexNumber(1).isExpenseOrIncome(), "expense");
        assertEquals(record.getEntryFromIndexNumber(2).isExpenseOrIncome(), "income");
    }

    @Test
    void testToStringEmpty() {
        record.removeEntry(0);
        assertEquals("record is empty", record.toString());
    }

    @Test
    void testToStringExpenses() {
        assertEquals("id: 0   name: n1   amount: -111.0   expense   category: \ntotal expenses: 111.0", record.toString());

        record.addEntry(new ExpenseEntry("n2", 10, ""));
        assertEquals("id: 0   name: n1   amount: -111.0   expense   category: \nid: 1   name: n2   amount: 10.0   income   category: \ntotal expenses: 101.0", record.toString());
    }

    @Test
    void testToStringIncome() {
        record.addEntry(new ExpenseEntry("ex", 222, ""));
        assertEquals("id: 0   name: n1   amount: -111.0   expense   category: \nid: 1   name: ex   amount: 222.0   income   category: \ntotal income: 111.0", record.toString());
    }

    @Test
    void testToStringNotExpenseNorIncome() {
        record.addEntry(new ExpenseEntry("ex", 111, ""));
        assertEquals("id: 0   name: n1   amount: -111.0   expense   category: \nid: 1   name: ex   amount: 111.0   income   category: \nnet income/expense is 0", record.toString());
    }

    @Test
    void testEditCategoryName() {
        record.addEntry(new ExpenseEntry("n1", 11, "cat"));
        record.editCategoryName(1, "newcat");
        assertEquals("newcat", record.getEntryFromIndexNumber(1).getCategory());
    }

    @Test
    void testGetEntryList() {
        List<ExpenseEntry> expenseEntries = record.getEntryList();
        assertEquals(1, expenseEntries.size());
        assertEquals("n1", expenseEntries.get(0).getEntryName());
        assertEquals(-111, expenseEntries.get(0).getAmount());
        assertEquals("", expenseEntries.get(0).getCategory());
    }

    @Test
    void testEditAmountToZeroExpectException() {
        ExpenseEntry ee = record.getEntryFromIndexNumber(0);
        try {
            ee.setAmount(0);
        } catch (ZeroInputException e) {
            // expected
        }
    }

    @Test
    void testEditAmountToNotZeroExpectNoException() {
        ExpenseEntry ee = record.getEntryFromIndexNumber(0);
        try {
            ee.setAmount(100);
        } catch (ZeroInputException e) {
            fail("no exception should be thrown");
        }
    }

    @Test
    void testCreateNewEntryWithZeroAmount() {
        ExpenseEntry ee = new ExpenseEntry("e", 0, "c");
        assertEquals(1, ee.getAmount());
    }
}