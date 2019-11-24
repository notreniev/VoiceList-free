package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by canez on 07/10/14.
 */
public class ItemDAO extends DAO<Item>{

    private static final String TABLE = "Item";

    public ItemDAO(Context ctx) {
        super(ctx);
    }

    @Override
    public long insert(Item vo) {
        long lastId = super.getDb().insert(TABLE, null, toContentValue(vo));
        return lastId;
    }

    @Override
    public long update(Item vo) {
        long numRows = getDb().update(TABLE, toContentValue(vo), "_id=?", new String[]{String.valueOf(vo.getId())});
        return numRows;
    }

    @Override
    public long delete(Item vo) {
        String[] whereArgs = new String[] { String.valueOf(vo.getId()) };
        int numRows = getDb().delete(TABLE, "_id=?", whereArgs);
        return numRows;
    }

    @Override
    public long deleteAll() {
        long numRows = getDb().delete(TABLE, "1", null);
        return numRows;
    }

    @Override
    public Cursor getAll(Item vo) {
        Cursor cursor = null;

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append(" _id");
        sb.append(", item ");
        sb.append(", list_id ");
        sb.append(", create_date ");
        sb.append(", done ");
        sb.append(" FROM Item ");
        sb.append(" WHERE list_id = ? ");
        sb.append("ORDER BY _id DESC ");

        String[] whereArgs = new String[] {String.valueOf(vo.getListId())};
        cursor = getDb().rawQuery(sb.toString(), whereArgs);

        cursor.moveToFirst();

        return cursor;
    }

    public long deleteList(List<Item> list) {
        int numRows = 0;
        for (Item item : list) {
            if (item.getId() > 0) {
                String[] whereArgs = new String[] { String.valueOf(item.getId()) };
                numRows += getDb().delete(TABLE, "_id=?", whereArgs);
            }
        }

        return numRows;
    }

    public long markList(List<Item> list) {
        int numRows = 0;
        for (Item item : list) {
            if (item.getDone() == 1){
                item.setDone(0);
            }else{
                item.setDone(1);
            }
            numRows += getDb().update(TABLE, toMarkedContentValue(item), "_id=?", new String[]{String.valueOf(item.getId())});
        }

        return numRows;
    }

    @Override
    public List<Item> list(Item vo) {
        List<Item> list = new ArrayList<Item>();

        Cursor cursor = null;

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append(" _id");
        sb.append(", item ");
        sb.append(", list_id ");
        sb.append(", create_date ");
        sb.append(", done ");
        sb.append(" FROM Item ");
        sb.append(" WHERE list_id = ? " );
        sb.append(" ORDER BY _id DESC");

        String[] whereArgs = new String[] { String.valueOf(vo.getListId()) };
        cursor = getDb().rawQuery(sb.toString(), whereArgs);

        Item item = null;
        while (cursor.moveToNext()){
            item = new Item();
            item.setId(cursor.getInt(0));
            item.setItem(cursor.getString(1));
            item.setListId(cursor.getInt(2));
            item.setCreateDate(cursor.getString(3));
            item.setDone(cursor.getInt(4));
            list.add(item);
        }

        return list;
    }

    @Override
    protected ContentValues toContentValue(Item vo) {
        ContentValues ctv = new ContentValues();
        ctv.put("item", vo.getItem());
        ctv.put("list_id", vo.getListId());
        String dataHoje = Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.MONTH)+ "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        ctv.put("create_date", dataHoje);
        ctv.put("done", vo.getDone());

        return ctv;
    }

    protected ContentValues toMarkedContentValue(Item vo) {
        ContentValues ctv = new ContentValues();
        ctv.put("_id", vo.getId());
        ctv.put("item", vo.getItem());
        ctv.put("list_id", vo.getListId());
        String dataHoje = Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.MONTH)+ "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        ctv.put("create_date", dataHoje);
        ctv.put("done", vo.getDone());



        return ctv;
    }


    @Override
    public Item getById(long id) {
        Cursor c = null;
        Item vo = null;

        String[] columns = new String[] { "_id", "item", "list_id", "create_date", "done" };
        String[] whereArgs = new String[] { Long.toString(id) };

        c = getDb().query(TABLE, columns, "_id=?", whereArgs, null, null, null,	null);

        vo = cursorToObject(c);

        return vo;
    }

    @Override
    public Item cursorToObject(Cursor c) {
        Item vo = null;

        if (c.moveToFirst()){
            vo = new Item();
            vo.setId(c.getInt(0));
            vo.setItem(c.getString(1));
            vo.setListId(c.getInt(2));
            vo.setCreateDate(c.getString(3));
            vo.setDone(c.getInt(4));
        }

        return vo;
    }


}
