package model;

import android.database.Cursor;

import java.util.List;

/**
 * Created by canez on 24/10/14.
 */
public interface IListaDAO {

    public List<ObjetoLista> list(ObjetoLista vo);
    public long insert(ObjetoLista vo);
    public long update(ObjetoLista vo);
    public long delete(ObjetoLista vo);
    public long deleteAll();
    public Cursor getAll(ObjetoLista vo);
    public long deleteList(List<ObjetoLista> list);
    public ObjetoLista getById(long id);

    void setRestored(ObjetoLista vo);

    void setArchived(ObjetoLista vo);
    void setTrashed(ObjetoLista vo);

    boolean isArchived(ObjetoLista vo);
    boolean isDeleted(ObjetoLista vo);
}
