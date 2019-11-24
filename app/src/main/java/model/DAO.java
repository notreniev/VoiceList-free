package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import db.DatabaseHelper;


public abstract class DAO<T>{

	private Context context;
	
	public DAO(Context ctx) {
		this.context = ctx;
	}
	
	/**
	 * Método insert
	 * 
	 * Retorna um long com valor
	 * do último ID inserido
	 */
	public abstract long insert(T vo);
	
	/**
	 * Método update
	 * 
	 * Retorna um longe com o número
	 * de linhas afetadas
	 */
	public abstract long update(T vo);

	/**
	 * Método delete
	 * 
	 * Retorna um long com o número
	 * de linhas afetadas
	 */
	public abstract long delete(T vo);
	
	/**
	 * Método deleteAll
	 * 
	 * Retorna o número de linhas afetadas
	 */
	public abstract long deleteAll();
	
	/**
	 * Método getAll
	 * 
	 * Retorna um Cursor com
	 * todas as linhas da consulta
	 */
	public abstract Cursor getAll(T vo);
	
	public abstract List<T> list(T vo);
	
	protected abstract ContentValues toContentValue(T vo);

    public abstract T cursorToObject(Cursor c);

	/**
	 * Método getById
	 * 
	 * Retorna um objeto pesquisa através do
	 * parâmetro ID informado
	 */
	public abstract T getById(long id);

	public SQLiteDatabase getDb() {
		return DatabaseHelper.getInstance(context);
	}	
	
}
