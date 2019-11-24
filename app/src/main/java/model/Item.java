package model;

import java.io.Serializable;

/**
 * Created by canez on 07/10/14.
 */
public class Item implements Serializable{

    private int id;
    private String item;

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    private int listId;
    private String createDate;
    private int done;

    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", item='" + item + '\'' +
                ", listId=" + listId +
                ", createDate='" + createDate + '\'' +
                ", done=" + done +
                '}';
    }
}
