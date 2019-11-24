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
public class AllDAO extends DAO<ObjetoLista> implements IListaDAO {

    private static final String TABLE = "Lista";

    public AllDAO(Context ctx) {
        super(ctx);
    }

    @Override
    public long insert(ObjetoLista vo) {
        long lastId = super.getDb().insert(TABLE, null, toContentValue(vo));
        return lastId;
    }

    @Override
    public long update(ObjetoLista vo) {
        long numRows = getDb().update(TABLE, toContentValue(vo), "_id=?", new String[]{String.valueOf(vo.getId())});
        return numRows;
    }

    @Override
    public long delete(ObjetoLista vo) {
        String[] whereArgs = new String[] { String.valueOf(vo.getId()) };
        getDb().delete("Item", "list_id=?", whereArgs);
        int numRows = getDb().delete(TABLE, "_id=?", whereArgs);
        return numRows;
    }

    @Override
    public long deleteAll() {
        long numRows = getDb().delete(TABLE, "1", null);
        return numRows;
    }

    @Override
    public Cursor getAll(ObjetoLista vo) {
        Cursor cursor = null;

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append(" _id");
        sb.append(", list_name ");
        sb.append(", create_date ");
        sb.append(", update_date ");
        sb.append(", archived ");
        sb.append(", trashed ");
        sb.append(" FROM Lista ");
        sb.append("ORDER BY _id DESC ");

        String[] whereArgs = new String[] {};
        cursor = getDb().rawQuery(sb.toString(), whereArgs);

        cursor.moveToFirst();

        return cursor;
    }

    public int getTotalItems(int id) {
        Cursor cursor = null;

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append(" count(*) as total ");
        sb.append(" FROM Item ");
        sb.append(" WHERE list_id = ? ");

        String[] whereArgs = new String[] {String.valueOf(id)};
        cursor = getDb().rawQuery(sb.toString(), whereArgs);

        cursor.moveToFirst();

        return cursor.getInt(0);
    }


    public long deleteList(List<ObjetoLista> list) {
        int numRows = 0;
        String[] whereArgs = {};
        for (ObjetoLista objetoLista : list) {
            if (objetoLista.getId() > 0) {
                whereArgs = new String[] { String.valueOf(objetoLista.getId()) };
                getDb().delete("Item", "list_id=?", whereArgs);
                numRows += getDb().delete(TABLE, "_id=?", whereArgs);
            }
        }

        return numRows;
    }

    @Override
    public List<ObjetoLista> list(ObjetoLista vo) {
        List<ObjetoLista> list = new ArrayList<ObjetoLista>();

        Cursor cursor = null;

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append(" _id");
        sb.append(", list_name ");
        sb.append(", create_date ");
        sb.append(", update_date ");
        sb.append(", archived ");
        sb.append(", trashed ");
        sb.append(" FROM Lista ");
        sb.append(" WHERE 1=1 ");
        sb.append("ORDER BY _id DESC");

        String[] whereArgs = new String[] {};
        cursor = getDb().rawQuery(sb.toString(), whereArgs);

        ObjetoLista objetoLista = null;
        while (cursor.moveToNext()){
            objetoLista = new ObjetoLista();
            objetoLista.setId(cursor.getInt(0));
            objetoLista.setListName(cursor.getString(1));
            objetoLista.setCreateDate(cursor.getString(2));
            objetoLista.setUpdateDate(cursor.getString(3));
            objetoLista.setArchived(cursor.getInt(4));
            objetoLista.setTrashed(cursor.getInt(5));
            list.add(objetoLista);
        }

        return list;
    }

    @Override
    protected ContentValues toContentValue(ObjetoLista vo) {
        ContentValues ctv = new ContentValues();
        ctv.put("list_name", vo.getListName());
        String hoje = Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.MONTH)+ "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (vo.getId() > 0){ // update
            ctv.put("update_date", hoje);
        }else{ // insert
            ctv.put("create_date", hoje);
        }
        ctv.put("archived", vo.getArchived());
        ctv.put("trashed", vo.getTrashed());

        return ctv;
    }

    @Override
    public ObjetoLista getById(long id) {
        Cursor c = null;
        ObjetoLista vo = null;

        String[] columns = new String[] { "_id", "list_name", "create_date", "update_date", "archived", "trashed" };
        String[] whereArgs = new String[] { Long.toString(id) };

        c = getDb().query(TABLE, columns, "_id=?", whereArgs, null, null, null,	null);

        vo = cursorToObject(c);

        return vo;
    }

    @Override
    public void setRestored(ObjetoLista vo) {
        vo.setTrashed(0);
        vo.setArchived(0);
        update(vo);
    }

    @Override
    public void setArchived(ObjetoLista vo) {
        vo.setTrashed(0);
        vo.setArchived(1);
        update(vo);
    }

    @Override
    public void setTrashed(ObjetoLista vo) {
        vo.setArchived(0);
        vo.setTrashed(1);
        update(vo);
    }

    @Override
    public boolean isArchived(ObjetoLista vo) {
        return getById(vo.getId()).getArchived() == 1 ? true : false;
    }

    @Override
    public boolean isDeleted(ObjetoLista vo) {
        return getById(vo.getId()).getTrashed() == 1 ? true : false;
    }

    @Override
    public ObjetoLista cursorToObject(Cursor c) {
        ObjetoLista vo = null;

        if (c.moveToFirst()){
            vo = new ObjetoLista();
            vo.setId(c.getInt(0));
            vo.setListName(c.getString(1));
            vo.setCreateDate(c.getString(2));
            vo.setUpdateDate(c.getString(3));
            vo.setArchived(c.getInt(4));
            vo.setTrashed(c.getInt(5));
        }

        return vo;
    }



}
