package net.codjo.referential.releasetest;
import org.uispec4j.Button;
import org.uispec4j.MenuBar;
import org.uispec4j.MenuItem;
import org.uispec4j.Table;
import org.uispec4j.TextBox;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;
/**
 *
 */
public class ReferentialFamilyTest extends ReferentialGuiTestCase {

    private Window getFamilyWindow() {
        MenuBar bar = mainWindow.getMenuBar();
        MenuItem menuItem = bar.getMenu("R�f�rentiel");
        MenuItem subMenu = menuItem.getSubMenu("Gestion des familles de r�f�rentiels");
        subMenu.click();
        return mainWindow.getDesktop().getWindow("Gestion des familles de r�f�rentiels");
    }


    public void test_loadEmptyFamilies() throws Exception {
        tokio.insertInputInDb("LoadEmptyFamilies");
        Window window = getFamilyWindow();
        Table refList = window.getTable("referentialList");

        Table table = window.getTable("PmReferentialFamily");
        assertTrue(table.contentEquals(new String[][]{
              {"Famille 1"},
              {"Famille 2"}
        }));

        assertTrue(refList.isEmpty());

        table.selectRow(0);

        assertTrue(refList.contentEquals(new String[][]{
              {"Devise"},
              {"Fr�quence de valorisation"},
              {"Pays"},
              {"VL d'ex�cution"},
        }));
    }


    public void test_loadInvalidReferential() throws Exception {
        tokio.insertInputInDb("loadInvalidReferential");
        Window window = getFamilyWindow();
        Table refAssoList = window.getTable("RefFamilyAsso");
        Table table = window.getTable("PmReferentialFamily");

        table.selectRow(0);

        assertTrue(refAssoList.contentEquals(new String[][]{
              {"Devise"},
              {"Fr�quence de valorisation"},
        }));
        window.getButton("Valider").click();

        tokio.assertAllOutputs("loadInvalidReferential");
    }


    public void test_loadFamilyContents() throws Exception {
        tokio.insertInputInDb("LoadFamiliesWithReferentials");
        Window window = getFamilyWindow();
        assertFamilyAssoEquals(window, 0, new String[][]{{"Devise"}, {"Pays"}});
        assertFamilyAssoEquals(window, 1, new String[][]{{"Devise"}, {"Fr�quence de valorisation"}});
    }


    public void test_addFamily() throws Exception {
        tokio.insertInputInDb("CreateFamily");
        Window window = getFamilyWindow();

        Table table = window.getTable("PmReferentialFamily");
        assertTrue(table.isEmpty());

        window.getButton("PmReferentialFamily.AddAction").click();
        Window detailWindow = getWindow("Famille de r�f�rentiels");
        TextBox textBox = detailWindow.getTextBox("familyLabel");
        assertTrue(textBox.textIsEmpty());
        textBox.setText("Famille en or");
        detailWindow.getButton("Valider").click();

        assertEquals(1, table.getRowCount());
        assertTrue(table.contentEquals(new String[][]{{"Famille en or"}}));

        tokio.assertAllOutputs("CreateFamily");
    }


    public void test_updateFamily() throws Exception {
        tokio.insertInputInDb("UpdateFamily");
        Window window = getFamilyWindow();

        Table table = window.getTable("PmReferentialFamily");
        table.selectRow(0);
        window.getButton("PmReferentialFamily.EditAction").click();
        Window detailWindow = getWindow("Famille de r�f�rentiels");
        TextBox textBox = detailWindow.getTextBox("familyLabel");
        assertEquals("Famille en plomb", textBox.getText());
        textBox.setText("Famille en or");
        detailWindow.getButton("Valider").click();

        assertEquals(1, table.getRowCount());
        assertTrue(table.contentEquals(new String[][]{{"Famille en or"}}));

        tokio.assertAllOutputs("UpdateFamily");
    }


    public void test_assignReferential() throws Exception {
        tokio.insertInputInDb("AssignReferentialToFamily");
        Window window = getFamilyWindow();

        Table familyList = window.getTable("PmReferentialFamily");
        familyList.selectRow(0);

        Table refAssoList = window.getTable("RefFamilyAsso");
        Button addReferentialButton = window.getButton("addReferential");
        Button removeReferentialButton = window.getButton("removeReferential");

        assertTrue(refAssoList.isEmpty());
        assertFalse(addReferentialButton.isEnabled());
        assertFalse(removeReferentialButton.isEnabled());

        Table refList = window.getTable("referentialList");
        refList.selectRows(new int[]{0, 2}); // Devise, Pays

        assertTrue(addReferentialButton.isEnabled());
        assertFalse(removeReferentialButton.isEnabled());

        addReferentialButton.click();

        assertFalse(addReferentialButton.isEnabled());
        assertFalse(removeReferentialButton.isEnabled());

        assertTrue(refAssoList.contentEquals(new String[][]{
              {"Devise"},
              {"Pays"},
        }));

        familyList.selectRow(1);

        assertTrue(refAssoList.contentEquals(new String[][]{
              {"Devise"},
        }));
        assertFalse(addReferentialButton.isEnabled());
        assertFalse(removeReferentialButton.isEnabled());

        refList.selectRows(new int[]{0, 1}); // Devise, Fr�quence de valorisation

        assertFalse(addReferentialButton.isEnabled());
        assertFalse(removeReferentialButton.isEnabled());

        refList.selectRow(1);
        assertTrue(addReferentialButton.isEnabled());
        assertFalse(removeReferentialButton.isEnabled());

        addReferentialButton.click();

        assertFalse(addReferentialButton.isEnabled());
        assertFalse(removeReferentialButton.isEnabled());
        assertTrue(refAssoList.contentEquals(new String[][]{
              {"Devise"},
              {"Fr�quence de valorisation"},
        }));

        refAssoList.selectRow(0);
        assertFalse(addReferentialButton.isEnabled());
        assertTrue(removeReferentialButton.isEnabled());

        removeReferentialButton.click();

        assertFalse(addReferentialButton.isEnabled());
        assertFalse(removeReferentialButton.isEnabled());

        assertTrue(refAssoList.contentEquals(new String[][]{
              {"Fr�quence de valorisation"},
        }));

        window.getButton("Valider").click();

        assertFalse(window.isVisible());

        tokio.assertAllOutputs("AssignReferentialToFamily");
    }


    public void test_deleteFamily() throws Exception {
        tokio.insertInputInDb("DeleteFamily");
        Window window = getFamilyWindow();

        Table table = window.getTable("PmReferentialFamily");
        assertTrue(table.contentEquals(new String[][]{
              {"Famille 1"},
              {"Famille 2"}
        }));

        table.selectRow(0);

        WindowInterceptor.init(window.getButton("PmReferentialFamily.DeleteAction").triggerClick())
              .processWithButtonClick("Oui")
              .run();

        assertTrue(table.contentEquals(new String[][]{
              {"Famille 2"}
        }));

        tokio.assertAllOutputs("DeleteFamily");
    }


    public void test_sortReferential() throws Exception {
        tokio.insertInputInDb("SortReferential");
        final Window window = getFamilyWindow();

        final Table familyList = window.getTable("PmReferentialFamily");
        familyList.selectRow(0);

        final Table toTable = window.getTable("RefFamilyAsso");

        assertTrue(toTable.contentEquals(new String[][]{
              {"Devise"},
              {"Pays"},
        }));

        final Table fromTable = window.getTable("referentialList");
        assertTrue(fromTable.contentEquals(new String[][]{
              {"Devise"},
              {"Fr�quence de valorisation"},
              {"Pays"},
              {"VL d'ex�cution"},
        }));

        fromTable.selectRow(1); // Fr�quence de valorisation
        final Button addReferentialButton = window.getButton("addReferential");
        addReferentialButton.click();

        assertTrue(toTable.contentEquals(new String[][]{
              {"Devise"},
              {"Fr�quence de valorisation"},
              {"Pays"},
        }));

        final Button removeReferentialButton = window.getButton("removeReferential");
        toTable.selectRow(0);
        removeReferentialButton.click();

        assertTrue(toTable.contentEquals(new String[][]{
              {"Fr�quence de valorisation"},
              {"Pays"},
        }));

        window.getButton("Valider").click();

        tokio.assertAllOutputs("SortReferential");
    }


    public void test_filterReferentialTable() throws Exception {
        tokio.insertInputInDb("FilterReferentialTable");
        final Window window = getFamilyWindow();

        final Table familyList = window.getTable("PmReferentialFamily");
        familyList.selectRow(0);

        final Table fromTable = window.getTable("referentialList");
        assertTrue(fromTable.contentEquals(new String[][]{
              {"Devise"},
              {"Fr�quence de valorisation"},
              {"Pays"},
              {"VL d'ex�cution"},
        }));

        final TextBox filterRefTextField = window.getInputTextBox("filterRefTextField");
        assertTrue(filterRefTextField.textIsEmpty());

        filterRefTextField.setText("tion");
        assertTrue(fromTable.contentEquals(new String[][]{
              {"Fr�quence de valorisation"},
              {"VL d'ex�cution"},
        }));

        filterRefTextField.setText("");
        assertTrue(fromTable.contentEquals(new String[][]{
              {"Devise"},
              {"Fr�quence de valorisation"},
              {"Pays"},
              {"VL d'ex�cution"},
        }));

        filterRefTextField.insertText("tion", 0);
        assertTrue(fromTable.contentEquals(new String[][]{
              {"Devise"},
              {"Fr�quence de valorisation"},
              {"Pays"},
              {"VL d'ex�cution"},
        }));

        window.getButton("performFilterButton").click();
        assertTrue(fromTable.contentEquals(new String[][]{
              {"Fr�quence de valorisation"},
              {"VL d'ex�cution"},
        }));

        window.getButton("resetFilterButton").click();
        assertTrue(fromTable.contentEquals(new String[][]{
              {"Devise"},
              {"Fr�quence de valorisation"},
              {"Pays"},
              {"VL d'ex�cution"},
        }));
        assertTrue(filterRefTextField.textIsEmpty());

        filterRefTextField.setText("tion");
        assertTrue(fromTable.contentEquals(new String[][]{
              {"Fr�quence de valorisation"},
              {"VL d'ex�cution"},
        }));

        familyList.selectRow(1);
        assertTrue(fromTable.contentEquals(new String[][]{
              {"Fr�quence de valorisation"},
              {"VL d'ex�cution"},
        }));
    }


    private void assertFamilyAssoEquals(Window window, int familyIndex, String[][] expected) {
        Table table = window.getTable("PmReferentialFamily");
        Table refAssoList = window.getTable("RefFamilyAsso");
        table.selectRow(familyIndex);
        assertTrue(refAssoList.contentEquals(expected));
    }
}
