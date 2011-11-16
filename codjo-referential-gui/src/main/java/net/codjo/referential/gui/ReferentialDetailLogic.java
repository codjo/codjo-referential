package net.codjo.referential.gui;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.gui.framework.AbstractDetailGui;
import net.codjo.mad.gui.framework.AbstractDetailLogic;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.mad.gui.request.util.RequiredFieldsValidator;
import net.codjo.referential.gui.api.CompositeGuiCustomiser;
import net.codjo.referential.gui.api.DefaultGuiCustomizer;
import net.codjo.referential.gui.api.DefaultGuiCustomizer.ComboBoxGuiCustomizer;
import net.codjo.referential.gui.api.Field;
import net.codjo.referential.gui.api.ReferentialItemPanel.GuiCustomizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import org.apache.log4j.Logger;

public class ReferentialDetailLogic extends AbstractDetailLogic {
    private static final Logger LOG = Logger.getLogger(ReferentialDetailLogic.class);
    protected static final CompositeGuiCustomiser DEFAULT_GUI_CUSTOMIZER = new CompositeGuiCustomiser();


    static {
        DEFAULT_GUI_CUSTOMIZER.addCustomizer(new ReferentialComboBoxGuiCustomizer());
        DEFAULT_GUI_CUSTOMIZER.addCustomizer(new DefaultGuiCustomizer());
    }


    public ReferentialDetailLogic(DetailDataSource dataSource, String title, Map<String, Field> fieldMap,
                                  GuiCustomizer customizer) throws Exception {
        super(dataSource, new ReferentialDetailGui(title, fieldMap, customizer));
        getButtonPanelLogic().setButtonLogicValidator(new RequiredFieldsValidator(dataSource));
    }


    public ReferentialDetailLogic(DetailDataSource dataSource,
                                  AbstractDetailGui referentialDetailGui) throws Exception {
        super(dataSource, referentialDetailGui);
        getButtonPanelLogic().setButtonLogicValidator(new RequiredFieldsValidator(dataSource));
    }


    public ReferentialDetailLogic(DetailDataSource dataSource, String title, Map<String, Field> fieldMap)
          throws Exception {
        this(dataSource, title, fieldMap, DEFAULT_GUI_CUSTOMIZER);
    }


    private static class ReferentialComboBoxGuiCustomizer extends ComboBoxGuiCustomizer {

        @Override
        public void initDefaultFieldValue(Field field) {
            try {
                RequestComboBox requestComboBox = (RequestComboBox)field.getComponent();
                requestComboBox.setDataSource(buildFatherDataSource(field.getHandlerId()));
                requestComboBox.load();
            }
            catch (Exception e) {
                LOG.error(e);
                throw new RuntimeException(e);
            }
            super.initDefaultFieldValue(field);
        }


        @Override
        protected ListDataSource buildFatherDataSource(String selectHandlerId) {
            ListDataSource listDataSource = new ListDataSource();
            listDataSource.setColumns(new String[]{"refCode", "refLabel"});
            listDataSource.setLoadFactoryId(selectHandlerId);
            listDataSource.setSelector(new FieldsList("closeDate", getCurrentDay()));
            return listDataSource;
        }


        private static String getCurrentDay() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(calendar.getTime());
        }
    }
}
