package net.codjo.referential.gui.api;
import net.codjo.gui.toolkit.number.NumberField;
import java.math.BigDecimal;
/**
 *
 */
public class Util {

    private Util() {
    }


    public static void initField(NumberField numberField, Field field) {
        if (field.getLength() == 0) {
            return;
        }

        numberField.setMaximumFractionDigits(field.getDecimalLength());

        // Le setScale negatif permet de faire 1*10^x
        BigDecimal max = new BigDecimal(1)
              .setScale(-(field.getLength() - field.getDecimalLength()), BigDecimal.ROUND_CEILING)
              .setScale(field.getDecimalLength());

        numberField.setMaxValue(max);
        numberField.setMinValue(max.multiply(new BigDecimal(-1)));
    }
}
