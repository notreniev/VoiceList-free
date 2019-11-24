package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

	private static String TAG = DatabaseHelper.class.getSimpleName();
	private static SQLiteDatabase mInstance = null;
	private static String DATABASE_NAME = "voicelist.db";
	private static int DATABASE_VERSION = 1;
	
	public static SQLiteDatabase getInstance(Context ctx){
		if (mInstance == null)
			mInstance = new DatabaseHelper(ctx.getApplicationContext()).getWritableDatabase();
		
		return mInstance;
	}

	private DatabaseHelper(Context ctx){
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "Criando o banco de dados");
		
		StringBuffer sb = new StringBuffer();

        sb.append("CREATE TABLE [Lista] ( ");
        sb.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sb.append("[list_name] STRING, ");
        sb.append("[create_date] TEXT, ");
        sb.append("[update_date] TEXT, ");
        sb.append("[archived] INTEGER NOT NULL DEFAULT 0, ");
        sb.append("[trashed] INTEGER NOT NULL DEFAULT 0); ");
        db.execSQL(sb.toString());

        sb.setLength(0);

		sb.append("CREATE TABLE [Item] ( ");
		sb.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sb.append("[item] STRING NOT NULL, ");
        sb.append("[list_id] INTEGER NOT NULL, ");
        sb.append("[create_date] TEXT, ");
		sb.append("[done] INTEGER NOT NULL DEFAULT 0); ");
		db.execSQL(sb.toString());
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Atualizando o banco de dados");
		
		//db.execSQL("DROP TABLE IF EXISTS Item;");

		onCreate(db);
	}

}
