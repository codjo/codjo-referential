package server.handler;
import java.sql.Connection;
import java.sql.SQLException;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.sql.server.util.SqlTransactionalExecutor;
/**
 *
 */
public class DeletePmReferentialFamilyCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {

        Connection con = getContext().getConnection();
        String sql = query.getArgumentString("familyId");
        int familyId = Integer.parseInt(sql);

        SqlTransactionalExecutor
              .init(con)
              .prepare("delete from PM_REF_FAMILY_REF_ASSO where FAMILY_ID = ? ")
              .withInt(familyId)
              .then()
              .prepare("delete from PM_REFERENTIAL_FAMILY where FAMILY_ID = ? ")
              .withInt(familyId)
              .then()
              .execute();
        return createResult("OK");
    }
}
