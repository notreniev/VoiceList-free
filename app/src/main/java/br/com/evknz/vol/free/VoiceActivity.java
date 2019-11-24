package br.com.evknz.vol.free;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.MenuUnwrapper;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import adapters.ItemSelectionAdapter;
import model.AllDAO;
import model.ArchiveDAO;
import model.IListaDAO;
import model.InboxDAO;
import model.Item;
import model.ItemDAO;
import model.ObjetoLista;
import model.TrashDAO;
import utils.OnDetectScrollListener;
import utils.ScrollDetectableListView;

public class VoiceActivity extends ActionBarActivity {

    private static final int SPEECH_REQUEST_CODE = 0;

    private ObjetoLista objetoLista = null;
    private List<Item> itemList = null;
    private EditText edSpoke = null;
    private ScrollDetectableListView lvLista = null;
    private ItemSelectionAdapter mAdapter = null;
    private IListaDAO listaDAO = null;
    private ItemDAO itemDAO = null;
    private Item item = null;
    private long affectedRows = 0;
    private List<Item> lista = null;
    private Animation animUp = null;
    private Animation animDown = null;
    private ImageView ivMic = null;
    private Button btnAdd = null;
    private List<Item> found = null;
    private Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        animUp = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
        animDown = AnimationUtils.loadAnimation(this, R.anim.push_up_out);

        int position = getIntent().getExtras().getInt("section_number");
        objetoLista = (ObjetoLista) getIntent().getExtras().getSerializable("LISTA_OBJ");

        itemList = new ArrayList<Item>();
        found = new ArrayList<Item>();

        switch(position){
            case 1:
                listaDAO = new InboxDAO(this);
                break;
            case 2:
                listaDAO = new ArchiveDAO(this);
                break;
            case 3:
                listaDAO = new TrashDAO(this);
                break;
            case 4:
                listaDAO = new AllDAO(this);
                break;
        }

        setTitle(objetoLista.getListName());

        itemDAO = new ItemDAO(this);
        item = new Item();

        ivMic = (ImageView) findViewById(R.id.ivMic);
        ivMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edSpoke.setHint("Fale o nome do item");
                displaySpeechRecognizer();
            }
        });

        edSpoke = (EditText) findViewById(R.id.edSpoke);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setItem(edSpoke.getText().toString());
                if (item.getItem() != null && item.getItem().length() > 0) {
                    if (objetoLista != null) {
                        item.setListId(objetoLista.getId());
                    }
                    if (item.getId() == 0) {
                        itemDAO.insert(item);
                    } else {
                        itemDAO.update(item);
                    }

                    item = new Item();
                    refreshList();
                } else {
                    Toast.makeText(getBaseContext(), "O campo texto não pode ser vazio!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lvLista = (ScrollDetectableListView) findViewById(R.id.lista);

        refreshList();

        lvLista.setSmoothScrollbarEnabled(true);
        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                item = lista.get((int)id);
                item.setListId(objetoLista.getId());
                edSpoke.setText(item.getItem());
            }
        });

        lvLista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvLista.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                toolbar.setVisibility(View.GONE);
                inflater.inflate(R.menu.context_menu_item, MenuUnwrapper.unwrap(menu));
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                StringBuilder sb = new StringBuilder();
                Set<Integer> positions = mAdapter.getCurrentCheckedPosition();
                for (Integer pos : positions) {
                    sb.append(" " + pos + ",");
                }
                switch (item.getItemId()) {
                    case R.id.action_mark:
                        affectedRows = markSelectedItems(itemList);
                        mAdapter.clearSelection();
                        edSpoke.setText("");
                        mode.finish();
                        break;
                    case R.id.action_remove_item:
                        affectedRows = deleteSelectedItems(itemList);
                        mAdapter.clearSelection();
                        edSpoke.setText("");
                        mode.finish();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                toolbar.setVisibility(View.VISIBLE);
                mAdapter.clearSelection();
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Item item = lista.get(position);
                if (checked) {
                    nr++;
                    mAdapter.setNewSelection(item.getId(), checked);
                    selectItems();
                } else {
                    nr--;
                    mAdapter.removeSelection(item.getId());
                    deselectItems(item);
                }
                mode.setTitle(nr + " ítens selecionados!");
            }


        });
        lvLista.setOnDetectScrollListener(new OnDetectScrollListener() {
            @Override
            public void onUpScrolling() {
                ivMic.setVisibility(View.VISIBLE);
                ivMic.startAnimation(animUp);
            }

            @Override
            public void onDownScrolling() {
                ivMic.startAnimation(animDown);
                ivMic.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onBottomScrolled() {

            }

            @Override
            public void onTopScrolled() {

            }
        });
    }

    private void refreshList() {
        if (objetoLista != null) {
            item.setListId(objetoLista.getId());
        }
        lista = itemDAO.list(item);
        mAdapter = new ItemSelectionAdapter(getBaseContext(), R.layout.lista_items_adapter, R.id.tvItem, lista);
        lvLista.setAdapter(mAdapter);
        edSpoke.setText("");
    }

    private void selectItems() {
        itemList = new ArrayList<Item>();
        Set<Integer> checkedItemIds = mAdapter.getCurrentCheckedPosition();
        for (long l : checkedItemIds) {
            Item item = itemDAO.getById(l);
            itemList.add(item);
        }
    }

    private long markSelectedItems(final List<Item> itemList) {
        affectedRows = (long) 0;
        affectedRows = itemDAO.markList(itemList);
        Toast.makeText(getBaseContext(), affectedRows + " itens afetados ", Toast.LENGTH_SHORT).show();
        refreshList();

        return affectedRows;
    }

    private void deselectItems(Item item) {
        itemList.remove(item);
    }

    private long deleteSelectedItems(final List<Item> itemList) {
        affectedRows = (long) 0;
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Deletar")
                .setMessage("Tem certeza de que deseja remover os registros selecionados?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        affectedRows = itemDAO.deleteList(itemList);
                        Toast.makeText(getBaseContext(), affectedRows + " itens deletados: ",  Toast.LENGTH_SHORT).show();
                        refreshList();
                    }
                }).setNegativeButton("Não", null).show();

        return affectedRows;
    }

    public void voiceStart(View view){
        displaySpeechRecognizer();
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            edSpoke.setText(results.get(0));

            if (results.get(0).equalsIgnoreCase("cancelar") || results.get(0).equalsIgnoreCase("fim")) {
                finish();
            }else {
                item.setItem(results.get(0));
                if (item.getItem() != null && item.getItem().length() > 0) {
                    item.setListId(objetoLista.getId());
                    itemDAO.insert(item);
                    refreshList();
                    displaySpeechRecognizer();
                } else {
                    Toast.makeText(getBaseContext(), "O campo texto não pode ser vazio!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.voice_item, menu);

        boolean isArchived = listaDAO.isArchived(objetoLista);
        boolean isDeleted = listaDAO.isDeleted(objetoLista);

        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_archive).setVisible(!isArchived);
        menu.findItem(R.id.action_trash).setVisible(!isDeleted);
        menu.findItem(R.id.action_restore).setVisible((isArchived||isDeleted));

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CarregarEncontrados(newText.toLowerCase(Locale.ENGLISH));
                mAdapter = new ItemSelectionAdapter(getBaseContext(), R.layout.lista_items_adapter, R.id.tvItem, found);
                lvLista.setAdapter(mAdapter);

                return false;
            }
        });

        return true;
    }

    public void CarregarEncontrados(String query) {

        String titulo = "";

        found.clear();
        for (int i = 0; i < lista.size(); i++) {
            if (query.length() <= lista.get(i).getItem().length()) {
                titulo = lista.get(i).getItem().toLowerCase(Locale.ENGLISH);
                if (titulo.contains(query)) {
                    found.add(lista.get(i));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        if (id == R.id.action_archive){
            new AlertDialog.Builder(this).setIcon(R.drawable.ic_action_archive_yellow).setTitle("Arquivar")
                    .setMessage("Você tem certeza que deseja arquivar esta lista?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listaDAO.setArchived(objetoLista);
                            Toast.makeText(getBaseContext(), "A lista arquivada com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }).setNegativeButton("Não", null).show();

        }else if (id == R.id.action_trash){
            new AlertDialog.Builder(this).setIcon(R.drawable.ic_action_trash_yellow).setTitle("Deletar")
                    .setMessage("Você tem certeza que deseja deletar esta lista?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listaDAO.setTrashed(objetoLista);
                            Toast.makeText(getBaseContext(), "A lista movida para a lixeira!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }).setNegativeButton("Não", null).show();

        }else if (id == R.id.action_restore){
            new AlertDialog.Builder(this).setIcon(R.drawable.ic_action_inbox_yellow).setTitle("Restaurar")
                    .setMessage("Você tem certeza que deseja restaurar esta lista para o inbox?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listaDAO.setRestored(objetoLista);
                            Toast.makeText(getBaseContext(), "Lista movida para a pasta Inbox!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }).setNegativeButton("Não", null).show();

        }else if (id == R.id.action_rename){
            Bundle args = new Bundle();
            args.putSerializable("LISTA_OBJ", objetoLista);

            Log.d("ITEM", "objetoLista: " + objetoLista);

            Toast.makeText(getBaseContext(), "Renomear lista", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), NewListActivity.class);
            intent.putExtras(args);
            startActivity(intent);
        }

    return super.onOptionsItemSelected(item);
    }
}
