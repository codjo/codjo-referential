package net.codjo.referential.releasetest;
import net.codjo.gui.toolkit.number.NumberField;
import org.uispec4j.Button;
import org.uispec4j.CheckBox;
import org.uispec4j.ComboBox;
import org.uispec4j.Table;
import org.uispec4j.TextBox;
import org.uispec4j.Tree;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;

public class ReferentialTest extends ReferentialGuiTestCase {

    private Window getReferentialWindow() {
        mainWindow.getMenuBar().getMenu("Référentiel").getSubMenu("Gestion des référentiels").click();
        return mainWindow.getDesktop().getWindow("Gestion des référentiels");
    }


    public void test_displayReferenceNoFamilyList() throws Exception {
        tokio.insertInputInDb("displayReferenceNoFamilyList");
        Window window = getReferentialWindow();
        final Tree tree = window.getTree();

        assertTrue(tree.contentEquals("root\n"
                                      + "  Devise\n"
                                      + "  Fréquence de valorisation\n"
                                      + "  Pays\n"
                                      + "  VL d'exécution"));

        tree.select("Devise");
        Table table = window.getTable();
        assertTrue(table.getHeader().contentEquals(new String[]{"Code", "Libellé", "Date de clôture",
                                                                "Est valide", "Valeur", "Population",
                                                                "Produit Intérieur Brut"}));

        tree.select("Fréquence de valorisation");
        table = window.getTable();
        assertTrue(table.getHeader().contentEquals(new String[]{"Code", "Libellé", "Date de clôture"}));

        tree.select("Pays");
        table = window.getTable();
        assertTrue(table.contentEquals(new String[]{"Code", "Libellé"}, new String[][]{
              {"CH", "Chweiz"},
              {"FR", "Frankreich"},
        }));

        tree.select("VL d'exécution");
        table = window.getTable();
        assertTrue(table.getHeader().contentEquals(
              new String[]{"Code", "Libellé", "Date de clôture", "Fréquence de valorisation"}));
    }


    public void test_displayReferenceWithFamilyList() throws Exception {
        tokio.insertInputInDb("displayReferenceWithFamilyList");
        Window window = getReferentialWindow();
        Tree tree = window.getTree();

        tree.selectRoot();
        assertTrue(tree.contentEquals("root\n"
                                      + "  Finance\n"
                                      + "    Fréquence de valorisation\n"
                                      + "  Géographie\n"
                                      + "    Devise\n"
                                      + "    Pays\n"
                                      + "  Monétaire\n"
                                      + "    Devise\n"
                                      + "  VL d'exécution"));

        tree.select("Géographie/Devise");
        Table table = window.getTable();
        assertTrue(table.getHeader().contentEquals(new String[]{"Code", "Libellé", "Date de clôture",
                                                                "Est valide", "Valeur", "Population",
                                                                "Produit Intérieur Brut"}));

        tree.select("Géographie/Pays");
        table = window.getTable();
        assertTrue(table.getHeader().contentEquals(new String[]{"Code", "Libellé", "Date de clôture"}));

        tree.select("Finance/Fréquence de valorisation");
        table = window.getTable();
        assertTrue(table.getHeader().contentEquals(new String[]{"Code", "Libellé", "Date de clôture"}));

        tree.select("VL d'exécution");
        table = window.getTable();
        assertTrue(table.getHeader().contentEquals(
              new String[]{"Code", "Libellé", "Date de clôture", "Fréquence de valorisation"}));
    }


    public void test_deleteReferentialData() throws Exception {
        tokio.insertInputInDb("deleteReferentialData");
        Window window = getReferentialWindow();

        window.getTree().select("Pays");
        window.getTable().selectRow(0);

        WindowInterceptor.init(window.getButton("CountryList.DeleteAction").triggerClick())
              .processWithButtonClick("Oui")
              .run();

        tokio.assertAllOutputs("deleteReferentialData");
    }


    public void test_addReferential() throws Exception {
        tokio.insertInputInDb("addReferentialData");

        ReferentialChecker checker = new ReferentialChecker();
        checker.selectCurrency()
              .addCurrency()
              .assertCodeIsEditable(true)
              .assertCodeIsEmpty()
              .assertLabelIsEditable(true)
              .assertLabelIsEmpty()
              .assertIsValidIsSelected(false)
              .assertPeopleIsEditable(true)
              .assertPeopleIsEmpty()
              .assertPibIsEditable(true)
              .assertPibIsEmpty()
              .assertValueIsEditable(true)
              .assertValueIsEmpty()
              .assertCloseDateIsEditable(true)
              .assertCloseDateIsEmpty()
              .assertValidateButtonIsEnabled(false)
              .assertCancelButtonIsEnabled(true)
              .setCode("Code1")
              .setLabel("Label1")
              .assertValidateButtonIsEnabled(true)
              .clickIsValid()
              .setPib(123.01)
              .setPeople(456)
              .setValue(8.97)
              .setCloseDate("01", "01", "2007")
              .validate()
              .assertDetailWindowIsClosed();

        tokio.assertAllOutputs("addReferentialData");
    }


    public void test_updateReferential() throws Exception {
        tokio.insertInputInDb("updateReferentialData");

        ReferentialChecker checker = new ReferentialChecker();
        checker.selectCurrency()
              .selectRow(0)
              .updateCurrency()
              .assertCodeIsEditable(false)
              .assertCodeValue("CODE1")
              .assertLabelIsEditable(true)
              .assertLabelValue("LABEL1")
              .assertIsValidIsSelected(true)
              .assertPeopleIsEditable(true)
              .assertPeopleValue("200")
              .assertPibIsEditable(true)
              .assertPibValue("123.00000")
              .assertValueIsEditable(true)
              .assertValueValue("8.97")
              .assertCloseDateIsEditable(true)
              .assertCloseDateValue("01", "01", "2007")
              .assertValidateButtonIsEnabled(false)
              .assertCancelButtonIsEnabled(true)
              .setLabel("truc")
              .assertValidateButtonIsEnabled(true)
              .validate()
              .assertDetailWindowIsClosed();

        tokio.assertAllOutputs("updateReferentialData");
    }


    public void test_duplicateReferential() throws Exception {
        tokio.insertInputInDb("duplicateReferentialData");

        ReferentialChecker checker = new ReferentialChecker();
        checker.selectCurrency()
              .selectRow(0)
              .duplicateCurrency()
              .assertCodeIsEditable(true)
              .assertCodeValue("CODE1")
              .assertLabelIsEditable(true)
              .assertLabelValue("LABEL1")
              .assertIsValidIsSelected(true)
              .assertPeopleIsEditable(true)
              .assertPeopleValue("200")
              .assertPibIsEditable(true)
              .assertPibValue("123.00000")
              .assertValueIsEditable(true)
              .assertValueValue("8.97")
              .assertCloseDateIsEditable(true)
              .assertCloseDateValue("01", "01", "2007")
              .assertValidateButtonIsEnabled(false)
              .assertCancelButtonIsEnabled(true)
              .setCode("CODE2")
              .assertValidateButtonIsEnabled(true)
              .validate()
              .assertDetailWindowIsClosed();

        tokio.assertAllOutputs("duplicateReferentialData");
    }


    public void test_referentialWithComboBox() throws Exception {
        tokio.insertInputInDb("referentialWithComboBox");
        Window window = getReferentialWindow();
        Tree tree = window.getTree();
        tree.select("VL d'exécution");

        final Button addButton = window
              .getButton("ExecutionVlList.repB.net.codjo.referential.gui.AddReferentialAction");
        addButton.click();

        Window detailWindow = getWindow("VL d'exécution");
        TextBox code = detailWindow.getTextBox("refCode");
        TextBox label = detailWindow.getTextBox("refLabel");
        ComboBox comboBox = detailWindow.getComboBox("valFrequency");

        TextBox dateDay = detailWindow.getTextBox("closeDate.dayField");
        TextBox dateMonth = detailWindow.getTextBox("closeDate.monthField");
        TextBox dateYear = detailWindow.getTextBox("closeDate.yearField");

        Button cancelButton = detailWindow.getButton("Annuler");
        Button validateButton = detailWindow.getButton("Valider");

        assertTrue(code.isEditable());
        assertTrue(code.textIsEmpty());

        assertTrue(label.isEditable());
        assertTrue(label.textIsEmpty());

        assertTrue(comboBox.isEnabled());
        assertTrue(comboBox.contains(new String[]{" ", "EX1"}));

        assertTrue(dateDay.textIsEmpty());
        assertTrue(dateMonth.textIsEmpty());
        assertTrue(dateYear.textIsEmpty());

        assertFalse(validateButton.isEnabled());
        assertTrue(cancelButton.isEnabled());

        code.setText("Code1");
        assertFalse(validateButton.isEnabled());
        label.setText("Label1");
        assertTrue(validateButton.isEnabled());

        comboBox.select("EX1");

        dateDay.setText("01");
        dateMonth.setText("01");
        dateYear.setText("2007");

        validateButton.click();

        assertFalse(detailWindow.isVisible());

        tokio.assertAllOutputs("referentialWithComboBox");
    }


    public void test_quickSearch() throws Exception {
        tokio.insertInputInDb("updateReferentialData");
        Window window = getReferentialWindow();
        Tree tree = window.getTree();

        TextBox searchBox = window.getTextBox("searchField");
        Button resetButton = window.getButton("resetSearchButton");
        Button searchButton = window.getButton("performSearchButton");
        tree.selectRoot();
        tree.select("Devise");

        assertTrue(tree.contentEquals("root\n"
                                      + "  Devise\n"
                                      + "  Fréquence de valorisation\n"
                                      + "  Pays\n"
                                      + "  VL d'exécution"));

        searchBox.setText("de");
        searchButton.click();

        assertTrue(tree.contentEquals("root\n"
                                      + "  Devise\n"
                                      + "  Fréquence de valorisation"));

        resetButton.click();

        assertTrue(searchBox.textIsEmpty());
        assertTrue(tree.contentEquals("root\n"
                                      + "  Devise\n"
                                      + "  Fréquence de valorisation\n"
                                      + "  Pays\n"
                                      + "  VL d'exécution"));
    }


    private class ReferentialChecker {
        protected Window window;


        private ReferentialChecker() {
            mainWindow.getMenuBar().getMenu("Référentiel").getSubMenu("Gestion des référentiels").click();
            window = mainWindow.getDesktop().getWindow("Gestion des référentiels");
        }


        public ReferentialChecker selectReferential(String referential) {
            window.getTree().select(referential);
            return this;
        }


        public ReferentialChecker selectRow(int rowIndex) {
            window.getTable().selectRow(rowIndex);
            return this;
        }


        public CurrencyReferentialChecker selectCurrency() {
            window.getTree().select("Devise");
            return new CurrencyReferentialChecker(window);
        }
    }

    private class CurrencyReferentialChecker {
        private Window detailWindow;
        private TextBox refCode;
        private TextBox refLabel;
        private CheckBox isValid;
        private TextBox people;
        private TextBox pib;
        private TextBox value;
        private TextBox closeDateDay;
        private TextBox closeDateMonth;
        private TextBox closeDateYear;
        private Window window;


        private CurrencyReferentialChecker(Window window) {
            this.window = window;
        }


        public CurrencyReferentialChecker selectRow(int rowIndex) {
            window.getTable().selectRow(rowIndex);
            return this;
        }


        public CurrencyReferentialChecker addCurrency() {
            Button addButton = window
                  .getButton("CurrencyList.repB.net.codjo.referential.gui.AddReferentialAction");
            addButton.click();
            getCurrencyWindow();
            return this;
        }


        public CurrencyReferentialChecker updateCurrency() {
            Button editButton = window
                  .getButton("CurrencyList.repB.net.codjo.referential.gui.EditReferentialAction");
            editButton.click();
            getCurrencyWindow();
            return this;
        }


        public CurrencyReferentialChecker duplicateCurrency() {
            window.getButton("duplicate").click();
            getCurrencyWindow();
            return this;
        }


        private void getCurrencyWindow() {
            detailWindow = getWindow("Devise");
            refCode = detailWindow.getTextBox("refCode");
            refLabel = detailWindow.getTextBox("refLabel");
            isValid = detailWindow.getCheckBox("isValid");
            people = detailWindow.getTextBox("people");
            pib = detailWindow.getTextBox("pib");
            value = detailWindow.getTextBox("value");
            closeDateDay = detailWindow.getTextBox("closeDate.dayField");
            closeDateMonth = detailWindow.getTextBox("closeDate.monthField");
            closeDateYear = detailWindow.getTextBox("closeDate.yearField");
        }


        public CurrencyReferentialChecker assertCodeIsEditable(boolean expected) {
            assertEquals(expected, refCode.isEditable());
            return this;
        }


        public CurrencyReferentialChecker assertCodeIsEmpty() {
            assertTrue(refCode.textIsEmpty());
            return this;
        }


        public CurrencyReferentialChecker assertCodeValue(String expected) {
            assertTrue(refCode.textEquals(expected));
            return this;
        }


        public CurrencyReferentialChecker assertLabelIsEditable(boolean expected) {
            assertEquals(expected, refLabel.isEditable());
            return this;
        }


        public CurrencyReferentialChecker assertLabelIsEmpty() {
            assertTrue(refLabel.textIsEmpty());
            return this;
        }


        public CurrencyReferentialChecker assertLabelValue(String expected) {
            assertTrue(refLabel.textEquals(expected));
            return this;
        }


        public CurrencyReferentialChecker assertIsValidIsSelected(boolean expected) {
            assertEquals(expected, isValid.isSelected());
            return this;
        }


        public CurrencyReferentialChecker assertPeopleIsEditable(boolean expected) {
            assertEquals(expected, people.isEditable());
            return this;
        }


        public CurrencyReferentialChecker assertPeopleIsEmpty() {
            assertTrue(people.textIsEmpty());
            return this;
        }


        public CurrencyReferentialChecker assertPeopleValue(String expected) {
            assertTrue(people.textEquals(expected));
            return this;
        }


        public CurrencyReferentialChecker assertPibIsEditable(boolean expected) {
            assertEquals(expected, pib.isEditable());
            return this;
        }


        public CurrencyReferentialChecker assertPibIsEmpty() {
            assertTrue(pib.textIsEmpty());
            return this;
        }


        public CurrencyReferentialChecker assertPibValue(String expected) {
            assertTrue(pib.textEquals(expected));
            return this;
        }


        public CurrencyReferentialChecker assertValueIsEditable(boolean expected) {
            assertEquals(expected, value.isEditable());
            return this;
        }


        public CurrencyReferentialChecker assertValueIsEmpty() {
            assertTrue(value.textIsEmpty());
            return this;
        }


        public CurrencyReferentialChecker assertValueValue(String expected) {
            assertTrue(value.textEquals(expected));
            return this;
        }


        public CurrencyReferentialChecker assertCloseDateIsEditable(boolean expected) {
            assertEquals(expected, closeDateDay.isEditable());
            assertEquals(expected, closeDateMonth.isEditable());
            assertEquals(expected, closeDateYear.isEditable());
            return this;
        }


        public CurrencyReferentialChecker assertCloseDateIsEmpty() {
            assertTrue(closeDateDay.textIsEmpty());
            assertTrue(closeDateMonth.textIsEmpty());
            assertTrue(closeDateYear.textIsEmpty());
            return this;
        }


        public CurrencyReferentialChecker assertCloseDateValue(String day, String month, String year) {
            assertEquals(day, closeDateDay.getText());
            assertEquals(month, closeDateMonth.getText());
            assertEquals(year, closeDateYear.getText());
            return this;
        }


        public CurrencyReferentialChecker assertValidateButtonIsEnabled(boolean expected) {
            assertEquals(expected, detailWindow.getButton("Valider").isEnabled());
            return this;
        }


        public CurrencyReferentialChecker assertCancelButtonIsEnabled(boolean expected) {
            assertEquals(expected, detailWindow.getButton("Annuler").isEnabled());
            return this;
        }


        public CurrencyReferentialChecker setCode(String code) {
            refCode.setText(code);
            return this;
        }


        public CurrencyReferentialChecker setLabel(String label) {
            refLabel.setText(label);
            return this;
        }


        public CurrencyReferentialChecker clickIsValid() {
            isValid.click();
            return this;
        }


        public CurrencyReferentialChecker setPib(Number number) {
            ((NumberField)pib.getAwtComponent()).setNumber(number);
            return this;
        }


        public CurrencyReferentialChecker setPeople(Number number) {
            ((NumberField)people.getAwtComponent()).setNumber(number);
            return this;
        }


        public CurrencyReferentialChecker setValue(Number number) {
            ((NumberField)value.getAwtComponent()).setNumber(number);
            return this;
        }


        public CurrencyReferentialChecker setCloseDate(String day, String month, String year) {
            closeDateDay.setText(day);
            closeDateMonth.setText(month);
            closeDateYear.setText(year);
            return this;
        }


        public CurrencyReferentialChecker validate() {
            detailWindow.getButton("Valider").click();
            return this;
        }


        public CurrencyReferentialChecker assertDetailWindowIsClosed() {
            assertFalse(detailWindow.isVisible());
            return this;
        }
    }
}
