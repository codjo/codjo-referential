package net.codjo.referential.gui.addon;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.referential.gui.api.Field;
/**
 *
 */
public class TestUtil {
    private TestUtil() {
    }


    public static Field field(String name) {
        Field field = new Field();
        field.setName(name);
        field.setLabel("label " + name);
        field.setType("string");
        return field;
    }


    public static Result result(Row... rows) {
        return new Result(new FieldsList(), rows);
    }


    public static Row row(String... contents) {
        Row row = new Row();
        for (String content : contents) {
            String[] tokens = content.split("=");
            row.addField(tokens[0], tokens[1]);
        }
        return row;
    }
}
