package net.codjo.referential.gui;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.Referential;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import junit.framework.TestCase;
/**
 *
 */
public class XmlReferentialMappingTest extends TestCase {

    public void test_loadReferentialList() throws Exception {
        SortedMap<String, Referential> referentialList = XmlReferentialMapping
              .loadFrom("/conf/basicReferential.xml")
              .getReferentialsByPreferenceId();

        assertEquals(4, referentialList.size());

        Iterator<Map.Entry<String, Referential>> referentialIter = referentialList.entrySet().iterator();

        Map.Entry<String, Referential> entry = referentialIter.next();
        Referential referential = entry.getValue();

        assertReferential(referential, "Country", "CountryList", "Pays");

        Map<String, Field> fieldList = referential.getFieldList();
        assertEquals(9, fieldList.size());
        assertField(fieldList, "countryCode", "country Code", "string", null, 6, 0, true);
        assertField(fieldList, "countryLabel", "country Label", "string", null, 255, 0, false);
        assertField(fieldList, "closedDate", "closed Date", "java.sql.Date", null, 0, 0, false);
        assertField(fieldList, "isValid", null, "boolean", null, 0, 0, false);
        assertField(fieldList, "people", null, "integer", null, 0, 0, false);
        assertField(fieldList, "area", null, "double", null, 0, 0, false);
        assertField(fieldList, "presidentSalary", null, "big-decimal", null, 10, 2, false);
        assertField(fieldList, "ceciliaBirthday", null, "sql-date", null, 0, 0, false);
        assertField(fieldList, "audit", null, "com.myapp.data.Audit", null, 0, 0, false);

        entry = referentialIter.next();
        referential = entry.getValue();
        assertReferential(referential, "Currency", "CurrencyList", "Devise");

        fieldList = referential.getFieldList();
        assertEquals(3, fieldList.size());
        assertField(fieldList, "currencyCode", null, "string", null, 10, 0, true);
        assertField(fieldList, "currencyLabel", null, "string", null, 255, 0, false);
        assertField(fieldList, "closedDate", null, "java.sql.Date", null, 0, 0, false);

        entry = referentialIter.next();
        referential = entry.getValue();
        assertReferential(referential, "ExecutionVl", "ExecutionVlList", "VL d'exécution");

        fieldList = referential.getFieldList();
        assertEquals(4, fieldList.size());

        assertField(fieldList, "refCode", null, "string", null, 6, 0, true);
        assertField(fieldList, "refLabel", null, "string", null, 255, 0, false);
        assertField(fieldList, "valFrequencyCode", null, "string", "selectAllValFrequencyWithCloseDate", 6, 0,
                    false);
        assertField(fieldList, "closedDate", null, "java.sql.Date", null, 0, 0, false);

        entry = referentialIter.next();
        referential = entry.getValue();
        assertReferential(referential, "ValFrequency", "ValFrequencyList", "Fréquence de valorisation");

        fieldList = referential.getFieldList();
        assertEquals(3, fieldList.size());
        assertField(fieldList, "refCode", null, "string", null, 6, 0, true);
        assertField(fieldList, "refLabel", null, "string", null, 255, 0, false);
        assertField(fieldList, "closedDate", null, "java.sql.Date", null, 0, 0, false);
    }


    public void test_loadReferentialListWithDefaultValues() throws Exception {
        SortedMap<String, Referential> referentialList = XmlReferentialMapping
              .loadFrom("/conf/referentialWithDefaultValues.xml")
              .getReferentialsByPreferenceId();

        assertEquals(1, referentialList.size());

        Iterator<Map.Entry<String, Referential>> referentialIter = referentialList.entrySet().iterator();

        Map.Entry<String, Referential> entry = referentialIter.next();
        Referential referential = entry.getValue();

        assertReferential(referential, "RefLinkType", "LinkTypeList", "Type d'association");

        Map<String, Field> fieldList = referential.getFieldList();
        assertEquals(7, fieldList.size());
        assertFieldDefaultValue(fieldList.get("refCode"), null);
        assertFieldDefaultValue(fieldList.get("refLabel"), null);
        assertFieldDefaultValue(fieldList.get("permanentList"), "1");
        assertFieldDefaultValue(fieldList.get("comment"), "defaultComment");
        assertFieldDefaultValue(fieldList.get("intField"), "15");
        assertFieldDefaultValue(fieldList.get("doubleField"), "32.1");
        assertFieldDefaultValue(fieldList.get("bigDecimalField"), "0.01");
    }


    private static void assertReferential(Referential referential,
                                          String name,
                                          String preferenceId,
                                          String title) {
        assertEquals(preferenceId, referential.getPreferenceId());
        assertEquals(title, referential.getTitle());
        assertEquals(name, referential.getName());
    }


    private static void assertField(Map<String, Field> fieldList,
                                    String name,
                                    String label, String type,
                                    String handlerId,
                                    int length,
                                    int decimalLength,
                                    boolean isPrimaryKey) {
        Field field = fieldList.get(name);
        assertEquals(name, field.getName());
        assertEquals(type, field.getType());
        assertEquals(length, field.getLength());
        assertEquals(decimalLength, field.getDecimalLength());
        assertEquals(isPrimaryKey, field.isPrimaryKey());
        assertEquals(handlerId, field.getHandlerId());
        assertEquals(label, field.getLabel());
    }


    private static void assertFieldDefaultValue(Field field, Object expectedDefaultValue) {
        assertEquals(expectedDefaultValue, field.getDefaultValue());
    }
}