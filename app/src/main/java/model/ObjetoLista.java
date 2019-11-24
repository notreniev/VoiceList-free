package model;

import java.io.Serializable;

/**
 * Created by canez on 07/10/14.
 */
public class ObjetoLista implements Serializable{

    private int id;
    private String listName;
    private String createDate;
    private String updateDate;
    private int archived;
    private int trashed;
    private int totalItems;



    public ObjetoLista() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getArchived() {
        return archived;
    }

    public void setArchived(int archived) {
        this.archived = archived;
    }

    public int getTrashed() {
        return trashed;
    }

    public void setTrashed(int trashed) {
        this.trashed = trashed;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    @Override
    public String toString() {
        return "ObjetoLista{" +
                "id=" + id +
                ", listName='" + listName + '\'' +
                ", createDate='" + createDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", archived=" + archived +
                ", trashed=" + trashed +
                ", totalItems=" + totalItems +
                '}';
    }
}
