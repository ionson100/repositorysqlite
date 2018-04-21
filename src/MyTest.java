import org.apache.log4j.Logger;
import repository.RepoBaseName;
import repository.RepoColumn;
import repository.RepoPrimaryKey;
import repository.RepoTable;

import java.util.Date;

@RepoBaseName (Main.sqlbase1)
@RepoTable ("table1")
public class MyTest {
    private static final Logger log = Logger.getLogger ( MyTest.class );

    @RepoPrimaryKey ("_id")
    public int _id;// primary key


    @RepoColumn
    public int session_id;

    @RepoColumn
    public Date date;

}

