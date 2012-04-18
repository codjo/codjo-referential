package server.handler;
import java.io.File;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.datagen.DatagenFixture;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerCommand.CommandResult;
import net.codjo.mad.server.handler.HandlerCommandTestCase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.Collections.singletonMap;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DeletePmReferentialFamilyCommandTest extends HandlerCommandTestCase {
    private static final DatagenFixture DATAGEN = new DatagenFixture(DeletePmReferentialFamilyCommandTest.class,
                                                                     "../codjo-referential-datagen/src/datagen/datagen.xml");


    public void test_delete() throws Exception {
        CommandResult result = assertExecuteQuery("deleteReferenceWithFamilyList",
                                                  singletonMap("familyId", "1"));
        assertThat(result.toString(), is("OK"));
    }


    @Override
    public void setUp() throws Exception {
        DATAGEN.doSetUp();
        DATAGEN.generate();
        super.setUp();
        getJdbcFixture().advanced().dropAllObjects();

        create("PM_REFERENTIAL_FAMILY");
        create("PM_REF_FAMILY_REF_ASSO");
    }


    @Override
    protected void tearDown() throws Exception {
        getJdbcFixture().drop(SqlTable.table("PM_REF_FAMILY_REF_ASSO"));
        getJdbcFixture().drop(SqlTable.table("PM_REFERENTIAL_FAMILY"));
        DATAGEN.doTearDown();
        super.tearDown();
    }


    @Override
    protected HandlerCommand createHandlerCommand() {
        return new DeletePmReferentialFamilyCommand();
    }


    @Override
    protected String getHandlerId() {
        return "deletePmReferentialFamily";
    }


    private void create(String tableName) {
        getJdbcFixture().advanced().executeCreateTableScriptFile(new File(DATAGEN.getSqlPath(), tableName + ".tab"));
    }
}
