package com.tomoima.twittertest.models;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by tomoaki on 2015/01/01.
 */
public class TweetProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher;
    private static final int CALENDAR = 1;
    private static final int CALENDAR_ID = 2;
    public static final String AUTHORITY = "com.tomoima.twittertest.calendarprovider";
    private static final String DATABASE_NAME = "Calendar";
    private static final int DATABASE_VERSION = 2;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    // 利用者がメソッドを呼び出したURIに対応する処理を判定処理に使用します
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TweetEvent.EVENTS_TABLE, CALENDAR);
        sUriMatcher.addURI(AUTHORITY, TweetEvent.EVENTS_TABLE + "/#", CALENDAR_ID);
    }
    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TweetEvent.EVENTS_TABLE);
            createTables(db);
        }

        private void createTables(SQLiteDatabase db){
            db.execSQL("CREATE TABLE "
                    + TweetEvent.EVENTS_TABLE
                    + "(" + TweetEvent.ID + " integer primary key autoincrement, "
                    + TweetEvent.TWEET_ID +" integer, "
                    + TweetEvent.MSG + " TEXT, "
                    + TweetEvent.TWEET_TIME +" TEXT);");
        }

    }
    @Override
    public boolean onCreate() {
        mDBHelper = new DatabaseHelper(getContext());
        mDb = mDBHelper.getWritableDatabase();
        return (mDb == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

        switch(sUriMatcher.match(uri)){
            case CALENDAR:
            case CALENDAR_ID:
                sqlBuilder.setTables(TweetEvent.EVENTS_TABLE);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = sqlBuilder.query(mDb,projection,selection,selectionArgs,null,null,sortOrder);
//        sqlBuilder.setTables(EVENTS_TABLE);
//        if(sUriMatcher.match(uri) == CALENDAR){
//            //select対象を指定
//            sqlBuilder.setProjectionMap(projection);
//        } else if (sUriMatcher.match(uri) == CALENDAR_ID){
//            sqlBuilder.setProjectionMap(projection);
//            sqlBuilder.appendWhere(ID + "=?");
//            //?に入る値を指定
//            selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{uri.getLastPathSegment()});
//        }
//        Cursor c = sqlBuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
//        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case CALENDAR:
                break;
            case CALENDAR_ID:
                break;
            default:
                throw new IllegalStateException("Unknown URI" + uri);

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mDb.insert(TweetEvent.EVENTS_TABLE,null, values);
        Uri _uri = null;
        if(rowID > 0){
            _uri = ContentUris.withAppendedId(TweetEvent.CONTENT_ID_URI_BASE, rowID);
            getContext().getContentResolver().notifyChange(uri,null);

        }else{
            throw new SQLException("Failed to insert row into " + uri);
        }
        return _uri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        mDb.beginTransaction();
        try{
            SQLiteStatement insertStmt = mDb.compileStatement(
                    "INSERT INTO "
                    + TweetEvent.EVENTS_TABLE
                    + "("+TweetEvent.TWEET_ID+","+TweetEvent.MSG+"," + TweetEvent.TWEET_TIME +") VALUES(?,?,?);"
                    );

            for(ContentValues value :values){
                insertStmt.bindLong(1,value.getAsLong(TweetEvent.TWEET_ID));
                insertStmt.bindString(2, value.getAsString(TweetEvent.MSG));
                insertStmt.bindString(3, value.getAsString(TweetEvent.TWEET_TIME));
                insertStmt.executeInsert();
            }
            mDb.setTransactionSuccessful();
        } catch (SQLException e){
            e.getMessage();
        } finally {
            mDb.endTransaction();
        }

        return values.length;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        int num = sUriMatcher.match(uri);
        if(num == 1){
            count = mDb.delete(TweetEvent.EVENTS_TABLE, selection,selectionArgs);
        }else if(num == 2){
            String id = uri.getPathSegments().get(1);
            count = mDb.delete(TweetEvent.EVENTS_TABLE, TweetEvent.ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" +
                            selection + ')' : ""),
                    selectionArgs);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        int num = sUriMatcher.match(uri);
        if(num == 1){
            count = mDb.update(TweetEvent.EVENTS_TABLE, values, selection, selectionArgs);
        }else if(num == 2){
            count = mDb.update(TweetEvent.EVENTS_TABLE, values, TweetEvent.ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" +
                            selection + ')' : ""),
                    selectionArgs);
        }else{
            throw new IllegalArgumentException(
                    "Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public static int getCount(Context context){
        long count = DatabaseUtils.queryNumEntries(new DatabaseHelper(context).getReadableDatabase(), TweetEvent.EVENTS_TABLE);
        return (int) count;
    }

}
