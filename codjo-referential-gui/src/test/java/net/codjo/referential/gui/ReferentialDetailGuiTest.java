package net.codjo.referential.gui;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Util;
import java.math.BigDecimal;
import junit.framework.TestCase;
/**
 *
 */
public class ReferentialDetailGuiTest extends TestCase {

    public void test_numberFields_bigDecimal() throws Exception {
        Field field = new Field();
        field.setLength(5);
        field.setDecimalLength(2);
        assertInitField(field, new BigDecimal("1000.00"), 2);
    }


    public void test_numberFields_bigDecimal2() throws Exception {
        Field field = new Field();
        field.setLength(5);
        field.setDecimalLength(4);
        assertInitField(field, new BigDecimal("10.0000"), 4);
    }


    public void test_numberFields_bigInteger() throws Exception {
        Field field = new Field();
        field.setLength(5);
        assertInitField(field, new BigDecimal("100000"), 0);
    }


    public void test_numberFields_scalar() throws Exception {
        assertInitField(new Field(), null, -1);
    }


    private void assertInitField(Field field, BigDecimal expectedMax, int expectedDecimalLength) {
        NumberField numberField = new NumberField();

        Util.initField(numberField, field);

        assertEquals(expectedDecimalLength, numberField.getMaximumFractionDigits());
        assertEquals(expectedMax, numberField.getMaxValue());

        if (expectedMax != null) {
            assertEquals(expectedMax.multiply(new BigDecimal(-1)), numberField.getMinValue());
        }
        else {
            assertNull(null, numberField.getMinValue());
        }
    }
}
