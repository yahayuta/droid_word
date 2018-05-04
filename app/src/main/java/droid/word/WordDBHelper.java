package droid.word;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * WordDBHelper
 * @author yasupong
 */
public class WordDBHelper extends SQLiteOpenHelper {

	public static String DB_NAME = "Word";
	public static int DB_VERSON = 1;
	public static String DB_TABLE_WORD = "Word";
	public static String DB_TABLE_STATUS = "Status";

	public WordDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSON);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL( "create table if not exists " + DB_TABLE_WORD + 
						"(date text primary key," +
						"word1 text," +
						"word2 text" +
						")" );
		arg0.execSQL( "create table if not exists " + DB_TABLE_STATUS + 
				"(word text primary key," +
				"currentindex text," +
				"reverse text" +
				")" );
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		arg0.execSQL( "drop table if exists " + DB_TABLE_WORD );
		arg0.execSQL( "drop table if exists " + DB_TABLE_STATUS );
		onCreate(arg0);
	}
}
