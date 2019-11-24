package br.com.evknz.vol.free;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import model.AllDAO;
import model.ObjetoLista;

public class NewListActivity extends Activity {

	AllDAO listaDAO = null;
	ObjetoLista objetoLista = null;
	StringBuffer error = null;
	EditText listName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_list);

		error = new StringBuffer();
		listaDAO = new AllDAO(this);
        objetoLista = new ObjetoLista();

        if (getIntent().hasExtra("LISTA_OBJ")) {
            objetoLista = (ObjetoLista) getIntent().getExtras().getSerializable("LISTA_OBJ");
        }

        TextView title = (TextView) findViewById(R.id.action_bar_title);
		listName = (EditText) findViewById(R.id.edItem);

        if (objetoLista != null && objetoLista.getId() > 0){
            listName.setText(objetoLista.getListName());
            title.setText("Renomear lista");
        }

		if (savedInstanceState == null) {
		}
	}

	private ObjetoLista valida(View view) {
		error = new StringBuffer();

        objetoLista.setListName(listName.getText().toString());
        objetoLista.setCreateDate(String.valueOf(Calendar.getInstance()));

        if (objetoLista.getListName() == null || objetoLista.getListName().length() == 0){
            error.append("Nome da lista não pode ser vazio!");
        }

		return objetoLista;
	}

    public void salvar(View view){

        objetoLista = valida(view);

        if (error.length() == 0) {
            if (objetoLista.getId() == 0) {
                listaDAO.insert(objetoLista);
                Toast.makeText(getBaseContext(), "Lista criada com sucesso!", Toast.LENGTH_SHORT).show();
            }else{
                listaDAO.update(objetoLista);
                Toast.makeText(getBaseContext(), "Lista renomeada com sucesso!", Toast.LENGTH_SHORT).show();
            }
            finish();
        }else{
            Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    }


}
