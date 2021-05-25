package persistence;

import model.ExpenseRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest {

    @BeforeEach
    void setup() {

    }

    @Test
    void testReadFromInvalidFile() {
        JsonReader reader = new JsonReader("./data/doesnotexist.json");
        try {
            ExpenseRecord rec = reader.read();
            fail("ioexception expected");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    void testReaderEmptyRecord() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyRecord.json");
        try {
            ExpenseRecord newRecord = reader.read();
            assertEquals(0, newRecord.size());
            assertTrue(newRecord.isEmpty());
        } catch (IOException e) {
            fail("exception not expected");
        }
    }

    @Test
    void testReaderNotEmptyRecord() {
        JsonReader reader = new JsonReader("./data/testReaderNotEmptyRecord.json");
        try {
            ExpenseRecord record = reader.read();
            assertEquals(2, record.size());
            assertFalse(record.isEmpty());

            assertEquals("n1", record.getEntryFromIndexNumber(0).getEntryName());
            assertEquals(100, record.getEntryFromIndexNumber(0).getAmount());
            assertEquals("c1", record.getEntryFromIndexNumber(0).getCategory());

            assertEquals("n2", record.getEntryFromIndexNumber(1).getEntryName());
            assertEquals(-100, record.getEntryFromIndexNumber(1).getAmount());
            assertEquals("c2", record.getEntryFromIndexNumber(1).getCategory());
        } catch (IOException e) {
            fail("exception not expected");
        }
    }

}
