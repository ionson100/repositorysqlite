import org.apache.log4j.Logger;
import repository.RepoBaseName;
import repository.RepoColumn;
import repository.RepoPrimaryKey;
import repository.RepoTable;

import java.util.Date;

@RepoBaseName (Main.sqlbase2)
@RepoTable ("table4")
public class MyTest4 {
    private static final Logger log = Logger.getLogger ( MyTest.class );

    @RepoPrimaryKey ("_id")
    public int _id;// primary key


    @RepoColumn
    public int session_id;

    @RepoColumn
    public Date date;

}
