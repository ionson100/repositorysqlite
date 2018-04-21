import repository.Repository;

import java.util.Date;

public class Main {

    public static final String sqlbase1 = "jdbc:sqlite:assa1.sqlite";
    public static final String sqlbase2 = "jdbc:sqlite:assa2.sqlite";

    private static volatile int session;

    public static void main ( String[] args ) {
        Repository <MyTest> repository1 = new Repository <> ( MyTest.class );
        Repository <MyTest2> repository2 = new Repository <> ( MyTest2.class );
        Repository <MyTest3> repository3 = new Repository <> ( MyTest3.class );
        Repository <MyTest4> repository4 = new Repository <> ( MyTest4.class );
        repository1.init ();
        repository2.init ();
        repository3.init ();
        repository4.init ();


        new Thread ( () -> {
            while (true) {
                {
                    MyTest test = new MyTest ();
                    test.date = new Date ();
                    test.session_id = ++session;
                    new Repository <> ( MyTest.class ).insert ( test );
                }
                {
                    MyTest3 test = new MyTest3 ();
                    test.date = new Date ();
                    test.session_id = ++session;
                    new Repository <> ( MyTest3.class ).insert ( test );
                }

            }

        } ).run ();




      
    }
}


