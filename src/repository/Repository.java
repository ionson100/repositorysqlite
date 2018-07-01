package repository;

import biz.source_code.miniConnectionPoolManager.MiniConnectionPoolManager;
import org.apache.log4j.Logger;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository<T> {
    private static final Logger log = Logger.getLogger ( Repository.class );



    private static Map <String, WrapperPool> poolMap = new HashMap <> ();

    private Class <T> aClass;

    public Repository ( Class <T> aClass ) {

        this.aClass = aClass;
        repoData = RepoReflection.getRepoDate ( aClass );

    }

    private final Object lock = new Object ();


    private RepoData repoData;


    private void close ( Connection connection ) {
        if ( connection != null ) {
            try {
                connection.close ();
            } catch (SQLException e) {
                log.error ( e );
            }
        }
    }

    public void init () {

        synchronized (lock) {
            String sql = RepoReflection.getSqlCreatetable ( aClass );
            Connection conn = null;
            try {
                conn = getConnection ();
                conn.createStatement ().execute ( sql );
            } catch (SQLException e) {
                log.error ( e );
            } finally {
                close ( conn );
            }
        }

    }



    public synchronized Connection getConnection () {
        synchronized (lock) {
            try {
                if ( poolMap.containsKey ( repoData.basename ) ) {
                    return poolMap.get ( repoData.basename ).poolMgr.getConnection ();
                } else {
                    WrapperPool wrapperPool = new WrapperPool ( repoData.basename );
                    poolMap.put ( repoData.basename , wrapperPool );

                    return wrapperPool.poolMgr.getConnection ();
                }
            } catch (SQLException e) {
                log.error ( e );
                return null;
            }
        }

    }


    public void insert ( T t ) {
        synchronized (lock) {
            Connection connection = getConnection ();
            try {
                String sql = RepoReflection.getSqlInsert ( t.getClass () );
                PreparedStatement pstmt = connection.prepareStatement ( sql );
                doublicate ( t , pstmt );
                pstmt.executeUpdate ();
                pstmt.close ();
            } catch (Exception ex) {

                log.error ( ex );
            } finally {
                close ( connection );
            }
        }

    }

    public List <T> getList ( String where , Object... params ) {
        synchronized (lock) {
            Connection connection = getConnection ();
            try {
                String sql = " select * from '" + repoData.tablename + "' ";
                if ( where == null ) {

                } else if ( where.trim ().length () > 0 ) {
                    sql = sql + " where " + where + " ";
                }
                String ss = sql;
                PreparedStatement pstmt = connection.prepareStatement ( sql );
                //  pstmt.setInt ( 1,1 );
                doublicate2 ( params , pstmt );

                ResultSet resultSet = pstmt.executeQuery ();
                List <T> res = new ArrayList <> ();
                while (resultSet.next ()) {
                    T tt = (T) repoData.aClass.newInstance ();

                    for (Field field : repoData.fieldColunn.keySet ()) {

                        repoData.primaryKey.left.set ( tt , resultSet.getInt ( repoData.primaryKey.right ) );

                        if ( field.getType () == int.class || field.getType () == Integer.class ) {
                            field.set ( tt , resultSet.getInt ( repoData.fieldColunn.get ( field ) ) );
                        } else if ( field.getType () == double.class || field.getType () == Double.class ) {
                            field.set ( tt , resultSet.getDouble ( repoData.fieldColunn.get ( field ) ) );
                        } else if ( field.getType () == float.class || field.getType () == Float.class ) {
                            field.set ( tt , resultSet.getFloat ( repoData.fieldColunn.get ( field ) ) );
                        } else if ( field.getType () == boolean.class || field.getType () == Boolean.class ) {
                            field.set ( tt , resultSet.getBoolean ( repoData.fieldColunn.get ( field ) ) );
                        } else if ( field.getType () == String.class ) {
                            field.set ( tt , resultSet.getString ( repoData.fieldColunn.get ( field ) ) );
                        } else if ( field.getType () == java.util.Date.class ) {
                            //
                            Date resultdate = resultSet.getDate ( repoData.fieldColunn.get ( field ) );
                            if ( resultdate == null ) {
                                field.set ( tt , null );
                            } else {
                                java.util.Date newDate = new java.util.Date ( resultdate.getTime () );
                                field.set ( tt , newDate );
                            }

                        }

                    }


                    res.add ( tt );
                }
                resultSet.close ();
                return res;


            } catch (Exception ex) {
                log.error ( ex );
                return null;
            } finally {
                close ( connection );
            }

        }


    }

    private void doublicate ( T t , PreparedStatement pstmt ) throws Exception {
        synchronized (lock) {
            int paraams = 1;
            for (Field field : repoData.fieldColunn.keySet ()) {
                if ( field.getType () == int.class || field.getType () == Integer.class ) {
                    pstmt.setInt ( paraams , field.getInt ( t ) );
                } else if ( field.getType () == double.class || field.getType () == Double.class ) {
                    pstmt.setDouble ( paraams , field.getDouble ( t ) );
                } else if ( field.getType () == float.class || field.getType () == Float.class ) {
                    pstmt.setFloat ( paraams , field.getFloat ( t ) );
                } else if ( field.getType () == boolean.class || field.getType () == Boolean.class ) {
                    pstmt.setBoolean ( paraams , field.getBoolean ( t ) );
                } else if ( field.getType () == String.class ) {
                    pstmt.setString ( paraams , String.valueOf ( field.get ( t ) ) );
                } else if ( field.getType () == java.util.Date.class ) {
                    Object o = field.get ( t );
                    if ( o == null ) {
                        pstmt.setDate ( paraams , null );
                    } else {
                        long l = ((java.util.Date) o).getTime ();
                        Date d = new Date ( l );
                        pstmt.setDate ( paraams , d );
                    }
                }
                paraams = paraams + 1;
            }
        }


    }

    public void update ( T t ) {
        synchronized (lock) {
            Connection connection = getConnection ();
            try {
                String sql = RepoReflection.getSqlUpdate ( t.getClass () , repoData.primaryKey.left.get ( t ) );
                PreparedStatement pstmt = connection.prepareStatement ( sql );

                doublicate ( t , pstmt );
                pstmt.executeUpdate ();
                pstmt.close ();

            } catch (Exception ex) {
                log.error ( ex );
            } finally {
                close ( connection );
            }
        }

    }

    private void doublicate2 ( Object[] params , PreparedStatement pstmt ) throws Exception {
        synchronized (lock) {
            int i = 0;
            if ( params != null ) {
                for (Object object : params) {
                    if ( object instanceof java.util.Date ) {
                        Date date = new Date ( ((java.util.Date) object).getTime () );
                        pstmt.setDate ( i++ , date );
                    } else {
                        pstmt.setObject ( ++i , object );
                    }

                }
            }
        }


    }

    public void freeSql ( String sql , Object... params ) {
        synchronized (lock) {
            Connection connection = getConnection ();
            try {

                PreparedStatement pstmt = connection.prepareStatement ( sql );

                doublicate2 ( params , pstmt );

                pstmt.execute ();
                pstmt.close ();

            } catch (Exception ex) {
                log.error ( ex );
            } finally {
                close ( connection );
            }
        }


    }

    public void deleteAllRows () {
        synchronized (lock) {
            Connection connection = getConnection ();
            try {
                PreparedStatement pstmt = connection.prepareStatement ( "DELETE  FROM '" + repoData.tablename + "'" );
                pstmt.execute ();
                pstmt.close ();

            } catch (Exception ex) {
                log.error ( ex );
            } finally {
                close ( connection );
            }
        }


    }


    public Object getExecuteScalar ( String sql , Object... params ) {
        synchronized (lock) {
            Connection connection = getConnection ();
            try {

                PreparedStatement pstmt = connection.prepareStatement ( sql );
                doublicate2 ( params , pstmt );
                ResultSet resultSet = pstmt.executeQuery ();
                Object res = null;
                while (resultSet.next ()) {
                    res = resultSet.getObject ( 1 );
                }
                resultSet.close ();
                pstmt.close ();
                return res;

            } catch (Exception ex) {
                log.error ( ex );
                return null;
            } finally {
                close ( connection );
            }
        }

    }

    public void insertBulk ( List <T> tList ) {
        synchronized (lock) {
            Connection connection = getConnection ();
            try {
                String sql = RepoReflection.getSqlBulk ( aClass , tList );
                log.info ( sql );
                PreparedStatement pstmt = connection.prepareStatement ( sql );
                pstmt.execute ();
                pstmt.close ();
            } catch (Exception ex) {

                log.error ( ex );
            } finally {
                close ( connection );
            }
        }


    }



    static class WrapperPool {
        private SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource ();
        public MiniConnectionPoolManager poolMgr;

        WrapperPool ( String basename ) {
            dataSource.setUrl ( basename );
            dataSource.setJournalMode("WAL");
            dataSource.getConfig().setBusyTimeout("10000");
            poolMgr = new MiniConnectionPoolManager ( dataSource , 10 );
        }
    }


}
