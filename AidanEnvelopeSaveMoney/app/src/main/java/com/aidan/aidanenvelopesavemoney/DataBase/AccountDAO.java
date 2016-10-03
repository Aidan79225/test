package com.aidan.aidanenvelopesavemoney.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aidan.aidanenvelopesavemoney.DevelopTool.Singleton;
import com.aidan.aidanenvelopesavemoney.Model.Account;
import com.aidan.aidanenvelopesavemoney.Model.Envelope;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aidan on 2016/10/2.
 */

public class AccountDAO {
    // 表格名稱
    public static final String TAG = "AccountDAO";
    public static final String TABLE_NAME = "Account";

    // 編號表格欄位名稱，固定不變
    public static final String KeyID = "id";

    // 其它表格欄位名稱
    public static final String NameColumn = "name";
    public static final String CommentColumn = "comment";
    public static final String CostColumn = "cost";
    public static final String ObjectIdColumn = "objectId";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KeyID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NameColumn + " TEXT NOT NULL, " +
                    CommentColumn + " TEXT NOT NULL, " +
                    ObjectIdColumn + " TEXT NOT NULL, "+
                    CostColumn + " INTEGER NOT NULL)";
    private SQLiteDatabase db;
    private static AccountDAO accountDAO;
    public static void init(Context context){
        Singleton.log("AccountDAO init");
        accountDAO = new AccountDAO(context);
    }
    public static AccountDAO getInstance(){
        if (accountDAO == null)return null;
        return accountDAO;
    }
    private AccountDAO(Context context) {
        db = DBHelper.getDatabase(context);
    }
    public void close() {
        db.close();
    }
    // 新增參數指定的物件
    public Account insert(Account item) {
        // 建立準備新增資料的ContentValues物件
        Singleton.log("AccountDAO insert");
        ContentValues cv = new ContentValues();
        cv.put(NameColumn, item.getEnvelopeName());
        cv.put(CommentColumn, item.getComment());
        cv.put(CostColumn, item.getCost());
        cv.put(ObjectIdColumn, item.getId());
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        item.setIndex(id);
        // 回傳結果
        return item;
    }
    // 修改參數指定的物件
    public boolean update(Account item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(NameColumn, item.getEnvelopeName());
        cv.put(CommentColumn, item.getComment());
        cv.put(CostColumn, item.getCost());
        cv.put(ObjectIdColumn, item.getId());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KeyID + "=" + item.getIndex();
        long test = db.update(TABLE_NAME, cv, where, null);
        // 執行修改資料並回傳修改的資料數量是否成功
        Log.e(TAG,test + "");
        return test > 0;
    }
    public boolean delete(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KeyID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }
    public List<Account> getAll() {
        List<Account> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            try {
                result.add(getRecord(cursor));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        cursor.close();
        return result;
    }
    public List<Account> getEnvelopsAccount(String envelopName) {
        List<Account> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, NameColumn + "= \"" + envelopName + "\"", null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                result.add(getRecord(cursor));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        cursor.close();
        return result;
    }
    // 取得指定編號的資料物件
    public Account get(long id) {
        // 準備回傳結果用的物件
        Account item = null;
        // 使用編號為查詢條件
        String where = KeyID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }

    // 把Cursor目前的資料包裝為物件
    public Account getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Account result = new Account();

        result.setIndex(cursor.getLong(0));
        result.setEnvelopeName(cursor.getString(1));
        result.setComment(cursor.getString(2));
        result.setId(cursor.getString(3));
        result.setCost(cursor.getInt(4));
        // 回傳結果
        return result;
    }


}
