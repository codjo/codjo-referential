package net.codjo.referential.gui.addon.tabbed.util;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.DetailDataSource;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
/**
 *
 */
public class RequiredFieldValidatorTest {
    private DetailDataSource detailDatasource;


    @Before
    public void before() {
        detailDatasource = Mockito.mock(DetailDataSource.class);
        final Row selectedRow = Mockito.mock(Row.class);
        when(detailDatasource.getSelectedRow()).thenReturn(selectedRow);
        when(selectedRow.getFieldValue("fred")).thenReturn("null");
    }


    @Test
    public void test_isNull() throws Exception {
        RequiredFieldValidator validator = new RequiredFieldValidator(detailDatasource);
        assertThat(validator.isNullField("fred"), equalTo(true));
    }
}
