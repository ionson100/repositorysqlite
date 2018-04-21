package repository;

import org.apache.commons.lang3.Pair;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class
RepoData{
   public Class aClass;
   public String tablename;
   public String basename;
   public Pair<Field,String> primaryKey;
   public Map<Field,String> fieldColunn =new HashMap<> (  );
}
