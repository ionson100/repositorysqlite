package repository;//package bitnic.repository;
//
//import bitnic.checkarchive.AmountSelf;
//import bitnic.checkarchive.CheckArchiveE;
//import bitnic.checkarchive.MProductSmail;
//import bitnic.model.MProduct;
//import bitnic.orm.Configure;
//import bitnic.table.DisplayColumn;
//import bitnic.utils.Patcher;
//import bitnic.utils.UtilsOmsk;
//import com.google.gson.reflect.TypeToken;
//import org.apache.log4j.Logger;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@RepoBaseName (Patcher.basenameArchive)
//@RepoTable ("archive22")
//public class TestRepo {
//    private static final Logger log = Logger.getLogger ( TestRepo.class );
//
//
//
//    @RepoPrimaryKey ("_id")
//    public int _id;// primary key
//
//
//    @RepoColumn
//    public int session_id;// смена
//
//
//    @RepoColumn
//    public int doc_id;//номер кассового документа
//
//
//    @RepoColumn
//    public Date date;// data чека
//
//
//    @RepoColumn
//    public String products;
//
//    @DisplayColumn(name_column = "is_active",width = 200)
//    @RepoColumn
//    public boolean is_active=true;// z report action
//
//    @DisplayColumn(name_column = "amount_product",width = 200)
//    @RepoColumn
//    public double amount_product;
//
//    @RepoColumn
//    public boolean is_bank;
//
//    @RepoColumn
//    public String link_bank;
//
//    @RepoColumn
//    public Date date_return;
//
//    @RepoColumn
//    public String result_bank;
//
//    @RepoColumn
//    public String check_body_bank;//банковский чек
//
//    @RepoColumn
//    public String string_cvs;//
//
//
//    @RepoColumn
//    public String cod_doc;
//
//
//    @RepoColumn
//    public boolean is_send_bank;
//
//
//
//    @RepoColumn
//    public double summ;
//
//    public static Repository2<TestRepo>  getTestRepoRepository(){
//        return new Repository2<TestRepo>(TestRepo.class);
//    }
//
//
//
//
//
//
//
//
//    private List<MProduct> mProductsHiden;
//
//    public List<MProduct> getProductList() {
//        if (mProductsHiden == null) {
//            Type listOfTestObject = new TypeToken<List<MProductSmail>> () {
//            }.getType();
//            List<MProductSmail> list2 = UtilsOmsk.getGson().fromJson(products, listOfTestObject);
//            List<MProduct> mProducts = new ArrayList<> ();
//            for (MProductSmail mProductSmail : list2) {
//                List<MProduct> list = Configure.GetSession().getList(MProduct.class, "id_core = ? ", mProductSmail.code_prod);
//                if (list.size() > 0) {
//                    MProduct mProduct = list.get(0);
//                    mProduct.price = mProductSmail.price;
//                    mProduct.selectAmount = mProductSmail.amount;
//                    mProducts.add(mProduct);
//                } else {
//                    MProduct mProduct = new MProduct();
//                    mProduct.code_prod = mProductSmail.code_prod;
//                    mProduct.price = mProductSmail.price;
//                    mProduct.selectAmount = mProductSmail.amount;
//                    mProduct.name = "Не найден";
//                    mProduct.name_check = "Не найден";
//                    mProducts.add(mProduct);
//                }
//            }
//            mProductsHiden = mProducts;
//        }
//        return mProductsHiden;
//
//    }
//    public void setProduct(List<MProduct>  mProducts){
//        List<MProductSmail> res=new ArrayList<>();
//        for (MProduct mProduct : mProducts) {
//            MProductSmail smail=new MProductSmail();
//            smail.amount=mProduct.selectAmount;
//            smail.code_prod=mProduct.code_prod;
//            smail.price=mProduct.price;
//            res.add(smail);
//        }
//
//        Type listOfTestObject = new TypeToken<List<MProductSmail>>(){}.getType();
//        products = UtilsOmsk.getGson().toJson(res, listOfTestObject);
//    }
//
//    public static void updateReturn(int doc_id) {
//
//        System.out.println("update archive set  date = "+new Date().getTime()+" where doc_id = "+doc_id);
//        new  Repository2<> (CheckArchiveE.class).freeSql("update archive set  date_return = "+new Date().getTime()+" where doc_id = "+doc_id);
//    }
//
//    public static void zReport(int numberSession) {
//        new  Repository2<> (CheckArchiveE.class).freeSql("update archive set is_active = ? ",false);
//        log.info("заперли точки Z отчетом");
//    }
//}
