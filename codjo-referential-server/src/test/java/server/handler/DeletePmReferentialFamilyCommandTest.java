package server.handler;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerCommand.CommandResult;
import net.codjo.mad.server.handler.HandlerCommandTestCase;
import org.junit.Test;

import static java.util.Collections.singletonMap;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DeletePmReferentialFamilyCommandTest extends HandlerCommandTestCase {

    @Test
    public void test_delete() throws Exception {
        CommandResult result = assertExecuteQuery("deleteReferenceWithFamilyList",
                                                  singletonMap("familyId", "1"));
        assertThat(result.toString(), is("OK"));
    }


    @Override
    protected HandlerCommand createHandlerCommand() {
        return new DeletePmReferentialFamilyCommand();
    }


    @Override
    protected String getHandlerId() {
        return "deletePmReferentialFamily";
    }
}
