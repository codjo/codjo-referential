package net.codjo.referential.gui;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.factory.DeleteFactory;
import net.codjo.mad.gui.request.factory.InsertFactory;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.mad.gui.request.factory.UpdateFactory;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class PreferenceUtil {

    private PreferenceUtil() {
    }


    static void initPreferences() {
        PreferenceFactory.initFactory();
        String detailWindowClassName = "net.codjo.referential.gui.ReferentialDetailLogic";

        Preference preference = new Preference();
        List<Column> list = new ArrayList<Column>(3);
        list.add(createColumn("countryCode", "Code", 0, 250, 150));
        list.add(createColumn("countryLabel", "Libellé", 0, 250, 150));
        list.add(createColumn("closedDate", "Date de clôture", 0, 250, 250, "net.codjo.mad.gui.request.util.renderers.HideInfiniteDateRenderer",
                              "date(dd/MM/yyyy)"));
        preference.setColumns(list);
        preference.setId("CountryList");
        preference.setDwClassName(detailWindowClassName);
        preference.setSelectByPk(new SelectFactory("selectCountryById"));
        preference.setUpdate(new UpdateFactory("updateCountry"));
        preference.setInsert(new InsertFactory("insertCountry"));
        preference.setDelete(new DeleteFactory("deleteCountry"));
        PreferenceFactory.addPreference(preference);

        preference = new Preference();
        list = new ArrayList<Column>(2);
        list.add(createColumn("currencyCode", "Code", 0, 250, 250));
        list.add(createColumn("currencyLabel", "Libellé", 0, 250, 250));
        preference.setColumns(list);
        preference.setId("CurrencyList");
        preference.setDwClassName(detailWindowClassName);
        preference.setSelectByPk(new SelectFactory("selectCurrencyById"));
        PreferenceFactory.addPreference(preference);

        preference = new Preference();
        list = new ArrayList<Column>(2);
        list.add(createColumn("refCode", "Code", 0, 250, 250));
        list.add(createColumn("refLabel", "Libellé", 0, 250, 250));
        preference.setColumns(list);
        preference.setId("ValFrequencyList");
        preference.setDwClassName(detailWindowClassName);
        preference.setSelectByPk(new SelectFactory("selectValFrequencyById"));
        PreferenceFactory.addPreference(preference);

        preference = new Preference();
        list = new ArrayList<Column>(3);
        list.add(createColumn("refCode", "Code", 0, 250, 250));
        list.add(createColumn("refLabel", "Libellé", 0, 250, 250));
        list.add(createColumn("valFrequencyCode", "Fréquence de valo", 0, 250, 250));
        preference.setColumns(list);
        preference.setId("ExecutionVlList");
        preference.setDwClassName(detailWindowClassName);
        preference.setInsert(new InsertFactory("insertExecutionVl"));
        preference.setSelectByPk(new SelectFactory("selectExecutionVlById"));
        PreferenceFactory.addPreference(preference);
    }


    private static Column createColumn(String fieldName, String label, int minSize, int maxSize,
                                       int preferredSize) {
        Column column = new Column();
        column.setFieldName(fieldName);
        column.setLabel(label);
        column.setMinSize(minSize);
        column.setMaxSize(maxSize);
        column.setPreferredSize(preferredSize);

        return column;
    }


    private static Column createColumn(String fieldName, String label, int minSize, int maxSize,
                                       int preferredSize, String renderer, String format) {
        Column column = createColumn(fieldName, label, minSize, maxSize, preferredSize);
        column.setRenderer(renderer);
        column.setFormat(format);

        return column;
    }


    public static void clearPreferences() {
        PreferenceFactory.clearPreferences();
    }
}
