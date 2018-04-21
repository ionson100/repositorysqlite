package repository;//package bitnic.repository;



import org.apache.commons.lang3.Pair;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepoReflection {
    private static final String myPath = "%s";
    private static Map<Class, RepoData> classListMap = new HashMap<>();

    public static RepoData getRepoDate(Class aClass) {

        if (classListMap.containsKey(aClass)) {
            return classListMap.get(aClass);
        } else {
            RepoData repoData = new RepoData();
            repoData.aClass = aClass;
            if (aClass.isAnnotationPresent(RepoTable.class)) {

                RepoTable repoTable = (RepoTable) aClass.getAnnotation(RepoTable.class);
                if (repoTable.value().trim().equals("")) {
                    repoData.tablename = aClass.getName();
                } else {
                    repoData.tablename = repoTable.value().trim();
                }
            }

            if (aClass.isAnnotationPresent(RepoBaseName.class)) {

                RepoBaseName base = (RepoBaseName) aClass.getAnnotation(RepoBaseName.class);
                if (base.value().trim().equals("")) {
                    repoData.basename = String.format(myPath, aClass.getName());
                } else {
                    repoData.basename = String.format(myPath,
                            base.value().trim());
                }
            }

            for (Field field : aClass.getDeclaredFields()) {
                RepoPrimaryKey key = field.getAnnotation(RepoPrimaryKey.class);
                RepoColumn column = field.getAnnotation(RepoColumn.class);
                if (column == null && key == null) {
                    continue;
                }
                if (key != null) {
                    String keyname;
                    if (key.value().trim().equals("")) {
                        keyname = field.getName();
                    } else {
                        keyname = key.value().trim();
                    }
                    repoData.primaryKey = new Pair<> (field, keyname);
                }
                if (column != null) {
                    String colname;
                    if (column.value().trim().equals("")) {
                        colname = field.getName();
                    } else {
                        colname = column.value().trim();
                    }
                    repoData.fieldColunn.put(field, colname);
                }
            }
            ///////////////// validate
            if (repoData.basename == null) {
                throw new RuntimeException(" репоизитарий класс " + aClass.getName() + " не содержит атрибута названия базы ");
            }
            if (repoData.tablename == null) {
                throw new RuntimeException(" репоизитарий класс " + aClass.getName() + " не содержит атрибута названия таблици ");
            }
            if (repoData.primaryKey == null) {
                throw new RuntimeException(" репоизитарий класс " + aClass.getName() + " не содержит атрибута первичного ключа ");
            }
            if (repoData.fieldColunn.size() == 0) {
                throw new RuntimeException(" репоизитарий класс " + aClass.getName() + " не содержит атрибутов полей таблици ");
            }
            classListMap.put(aClass, repoData);
            return repoData;
        }


    }

    public static String getSqlCreatetable(Class aClass) {
        RepoData repoData = getRepoDate(aClass);
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS '" + repoData.tablename + "' (");
        sb.append ( "\"" ).append ( repoData.primaryKey.right ).append ( "\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , " );
        for (Field field : repoData.fieldColunn.keySet()) {
            if (field.getType() == int.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\" INTEGER DEFAULT 0," );
            } else if (field.getType() == Integer.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\" INTEGER ," );
            }else if (field.getType() == Long.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\" INTEGER ," );
            }else if (field.getType() == long.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\" INTEGER DEFAULT 0," );
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\" BOOL ," );
            } else if (field.getType() == double.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\"  DOUBLE DEFAULT 0," );
            } else if (field.getType() == Double.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\"  DOUBLE ," );
            } else if (field.getType() == float.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\"  FLOAT DEFAULT 0," );
            } else if (field.getType() == Float.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\"  FLOAT ," );
            } else if (field.getType() == Date.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\"  DATETIME ," );
            } else if (field.getType() == String.class) {
                sb.append ( "\"" ).append ( repoData.fieldColunn.get ( field ) ).append ( "\"  TEXT ," );
            }
        }
        return sb.toString().substring(0, sb.length() - 1) + ");";

    }

    public static String getSqlInsert(Class aClass) {
        RepoData repoData = getRepoDate(aClass);
        StringBuilder sb = new StringBuilder("INSERT INTO '" + repoData.tablename + "' (");
        for (Field field : repoData.fieldColunn.keySet()) {
            sb.append(repoData.fieldColunn.get(field)).append(",");
        }
        String s = sb.toString().substring(0, sb.length() - 1);
        sb.setLength(0);
        sb.append(s).append(") ").append("VALUES (");
        for (Field ignored : repoData.fieldColunn.keySet()) {
            sb.append(" ?,");
        }
        s = sb.toString().substring(0, sb.length() - 1) + ")";
        return s;
    }

    public static String getSqlUpdate(Class<?> aClass, Object o) {
        RepoData repoData = getRepoDate(aClass);
        StringBuilder sb = new StringBuilder("UPDATE '" + repoData.tablename + "' SET").append(System.lineSeparator());
        for (Field field : repoData.fieldColunn.keySet()) {
            sb.append(System.lineSeparator()).append(repoData.fieldColunn.get(field)).append(" = ?,");
        }
        String s = sb.toString().substring(0, sb.length() - 1);
        sb.setLength(0);
        sb.append(s).append(" where ").append(repoData.primaryKey.right).append(" = ").append(String.valueOf(o));


        return sb.toString();
    }

    public static  <T> String getSqlBulk(Class aClass, List<T> list) throws IllegalAccessException {
        RepoData repoData = getRepoDate(aClass);

        StringBuilder sb = new StringBuilder("INSERT INTO '" + repoData.tablename + "' (");
        for (Field field : repoData.fieldColunn.keySet()) {
            sb.append(" '").append(repoData.fieldColunn.get(field)).append("'").append(",");
        }
        String s = sb.toString().substring(0, sb.length() - 1);
        sb.setLength(0);
        sb.append(s).append(") ").append("VALUES ");
        for (Object t : list) {
            sb.append("(");
            for (Field field : repoData.fieldColunn.keySet()) {
                if (field.getType() == String.class) {
                    String sd = (String) field.get(t);
                    if (sd == null) {
                        sb.append("'',");
                    } else {
                        sb.append("'").append(sd).append("' ,");
                    }

                } else if (field.getType() == Date.class) {
                    Date date = (Date) field.get(t);
                    if (date == null) {
                        sb.append(",");
                    } else {
                        sb.append(date.getTime()).append(",");
                    }
                } else if (field.getType() == Boolean.class) {
                    Boolean b = field.getBoolean(t);
                    String sdb = "0";
                    if ( b ) {
                        sdb = "1";
                    }
                    sb.append(sdb).append(",");
                } else if (field.getType() == boolean.class) {
                    Boolean b = field.getBoolean(t);
                    String sdb;
                    if ( b ) {
                        sdb = "1";

                    } else {
                        sdb = "0";
                    }
                    sb.append(sdb).append(",");
                } else if (field.getType() == Integer.class) {
                    Integer b = (Integer) field.get(t);

                    if (b == null) {
                        sb.append(",");

                    } else {
                        sb.append(b).append(",");
                    }

                } else if (field.getType() == int.class) {
                    int b = (int) field.get(t);
                    sb.append(b).append(",");
                } else if (field.getType() == Double.class) {
                    Double b = (Double) field.get(t);

                    if (b == null) {
                        sb.append(",");

                    } else {
                        sb.append(b).append(",");
                    }

                } else if (field.getType() == double.class) {
                    double b = (double) field.get(t);
                    sb.append(b).append(",");
                } else if (field.getType() == Float.class) {
                    Float b = (Float) field.get(t);

                    if (b == null) {
                        sb.append(",");

                    } else {
                        sb.append(b).append(",");
                    }

                } else if (field.getType() == float.class) {
                    float b = (float) field.get(t);
                    sb.append(b).append(",");
                }


            }
            sb.replace(sb.length() - 1, sb.length(), "");
            sb.append("),");

        }
        return sb.substring(0, sb.length() - 1);


    }


}

