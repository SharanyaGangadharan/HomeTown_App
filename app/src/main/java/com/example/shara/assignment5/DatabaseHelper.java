package com.example.shara.assignment5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.shara.assignment5.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DatabaseHelper {

    private  DatabaseWrapper databaseWrapper ;


    public DatabaseHelper(Context context){
        databaseWrapper = new DatabaseWrapper(context);

    }

    private ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContent.ID,user.getId());
        values.put(DatabaseContent.NICKNAME, user.getNickname());
        values.put(DatabaseContent.COUNTRY, user.getCountry());
        values.put(DatabaseContent.STATE, user.getState());
        values.put(DatabaseContent.CITY, user.getCity());
        values.put(DatabaseContent.YEAR, user.getYear());
        values.put(DatabaseContent.LATITUDE, user.getLatitude());
        values.put(DatabaseContent.LONGITUDE,user.getLongitude());
        return values;
    }


    public void insertUsers(List<User> users) {
        SQLiteDatabase writableDatabase = databaseWrapper.getWritableDatabase();

        for (User user : users){
            ContentValues values = getContentValues(user);
            writableDatabase.replace(DatabaseContent.TABLE_NAME, null, values);
        }

        writableDatabase.close();
    }

    public List<User> displayUsers(String filter, int beforeid){
        List<User> result = new ArrayList<>();
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseContent.TABLE_NAME + constructWhereClause(filter,beforeid);
        Log.i("rew",query);
        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User post = cursorToUser(cursor);
                result.add(post);
                cursor.moveToNext();
            }
        }
        database.close();
        return result;
    }

    public User cursorToUser(Cursor cursor){
        User user = new User();
        user.setUid(cursor.getString(cursor.getColumnIndex(DatabaseContent.ID)));
        user.setNickname(cursor.getString(cursor.getColumnIndex(DatabaseContent.NICKNAME)));
        user.setCountry(cursor.getString(cursor.getColumnIndex(DatabaseContent.COUNTRY)));
        user.setState(cursor.getString(cursor.getColumnIndex(DatabaseContent.STATE)));
        user.setCity(cursor.getString(cursor.getColumnIndex(DatabaseContent.CITY)));
        user.setYear(cursor.getString(cursor.getColumnIndex(DatabaseContent.YEAR)));
        user.setLatitude(cursor.getString(cursor.getColumnIndex(DatabaseContent.LATITUDE)));
        user.setLongitude(cursor.getString(cursor.getColumnIndex(DatabaseContent.LONGITUDE)));
        return user;
    }

    public String  constructWhereClause(String filter, int beforeid){
        String clause=" WHERE ";
        if(beforeid!=0)
            clause = clause + " Id >= " + beforeid;
        if(filter !=null) {
            StringTokenizer tokenizer = new StringTokenizer(filter,"&");
            while (tokenizer.hasMoreTokens()) {
                String item = tokenizer.nextToken().replace("=", "='");
                item = item + "'";
                if (!clause.endsWith(" WHERE "))
                    clause = clause +" AND "+ item;
                else
                    clause = clause + " AND " + item;
            }
        }
        return clause== " WHERE " ? "":clause+" ORDER BY Id DESC";
    }
}
