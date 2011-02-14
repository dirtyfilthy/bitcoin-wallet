package net.dirtyfilthy.bitcoin.wallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class ExposedSQLiteCursor extends SQLiteCursor {
	private SQLiteQuery query;

	public ExposedSQLiteCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
			String editTable, SQLiteQuery query) {
		super(db, driver, editTable, query);
		this.query=query;
		// TODO Auto-generated constructor stub
	}


	// expose this 
	
	public SQLiteQuery getQuery() {
		return query;
	}
	
	public static class Factory implements SQLiteDatabase.CursorFactory {
		public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query){
			return new ExposedSQLiteCursor(db, driver, editTable, query);
		}
	}

}
