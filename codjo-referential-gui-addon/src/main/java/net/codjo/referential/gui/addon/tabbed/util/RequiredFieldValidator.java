package net.codjo.referential.gui.addon.tabbed.util;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonLogicValidator;
import java.util.Set;
/**
 *
 */
public class RequiredFieldValidator implements ButtonLogicValidator {
    private DetailDataSource dataSource;


    public RequiredFieldValidator(DetailDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public boolean isValid() {
        Set<String> fieldNames = dataSource.getDeclaredFields().keySet();
        for (String fieldName : fieldNames) {
            if (dataSource.isFieldRequired(fieldName) && isNullField(fieldName)) {
                return false;
            }
        }
        return true;
    }


    boolean isNullField(String fieldName) {
        return "null".equals(dataSource.getSelectedRow().getFieldValue(fieldName));
    }
}
