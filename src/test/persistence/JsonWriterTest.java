package persistence;

import exception.ZeroInputException;
import model.ExpenseEntry;
import model.ExpenseRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest {

    ExpenseRecord rec;
    JsonWriter writer;

    @BeforeEach
    void setup() {
        rec = new ExpenseRecord();
    }

    @Test
    void testIllegalFileName() {
        try {
            writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("expected ioexception");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    void testWriterEmptyRecord() {
        try {
            writer = new JsonWriter("./data/testWriterEmptyRecord.json");
            writer.open();
            writer.write(rec);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyRecord.json");
            ExpenseRecord newRec = reader.read();
            assertEquals(0, newRec.size());
            assertTrue(newRec.isEmpty());
        } catch (IOException e) {
            fail("no exception expected");
        }
    }

    @Test
    void testWriterNotEmptyRecord() {
        try {
            rec.addEntry(new ExpenseEntry("n1", 100, "c1"));
            rec.addEntry(new ExpenseEntry("n2", -100, "c2"));

            writer = new JsonWriter("./data/testWriterNotEmptyRecord.json");
            writer.open();
            writer.write(rec);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterNotEmptyRecord.json");
            ExpenseRecord newRec = reader.read();

            assertEquals(2, newRec.size());
            assertFalse(newRec.isEmpty());

            assertEquals("n1", newRec.getEntryFromIndexNumber(0).getEntryName());
            assertEquals(100, newRec.getEntryFromIndexNumber(0).getAmount());
            assertEquals("c1", newRec.getEntryFromIndexNumber(0).getCategory());

            assertEquals("n2", newRec.getEntryFromIndexNumber(1).getEntryName());
            assertEquals(-100, newRec.getEntryFromIndexNumber(1).getAmount());
            assertEquals("c2", newRec.getEntryFromIndexNumber(1).getCategory());
        } catch (IOException e) {
            fail("no exception expected");
        }
    }

}
