package br.com.evknz.vol.free;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.internal.view.menu.MenuUnwrapper;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import adapters.ListaSelectionAdapter;
import model.AllDAO;
import model.ArchiveDAO;
import model.IListaDAO;
import model.InboxDAO;
import model.ObjetoLista;
import model.TrashDAO;

public class ListaFragment extends Fragment {

    private static final int SPEECH_REQUEST_CODE = 0;

    private List<ObjetoLista> itemList = null;
    private ListaSelectionAdapter mAdapter;
    private ListView lvLista = null;
    private IListaDAO listaDAO = null;
    private ObjetoLista objetoLista = null;
    private long affectedRows = 0;
    private List<ObjetoLista> lista = null;
    private Animation animUp = null;
    private Animation animDown = null;
    private Toolbar toolbar = null;
    private ActionMode actionMode = null;

    private List<ObjetoLista> found = null;

    private int mParam1;

    private OnFragmentInteractionListener mListener;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private AdView adView;

    public ListaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getInt("section_number");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_lista, container, false);

        adView = (AdView) v.findViewById(R.id.adView);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

        itemList = new ArrayList<ObjetoLista>();
        found = new ArrayList<ObjetoLista>();

        switch (mParam1){
            case 1: // inbox
                listaDAO = new InboxDAO(v.getContext());
                break;
            case 2: // archive
                listaDAO = new ArchiveDAO(v.getContext());
                break;
            case 3: // trash
                listaDAO = new TrashDAO(v.getContext());
                break;
            case 4: // all
                listaDAO = new AllDAO(v.getContext());
                break;
            default:
                break;
        }

        animUp = AnimationUtils.loadAnimation(v.getContext(), R.anim.push_up_in);
        animDown = AnimationUtils.loadAnimation(v.getContext(), R.anim.push_up_out);

        TextView tvEmpty = (TextView) v.findViewById(R.id.tvEmpty);

        lvLista = (ListView) v.findViewById(R.id.lvLista);
        lvLista.setEmptyView(tvEmpty);

        refreshList();

        lvLista.setSmoothScrollbarEnabled(true);
        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                objetoLista = lista.get((int)(id));

                Bundle args = new Bundle();
                args.putSerializable("LISTA_OBJ", objetoLista);
                args.putInt("section_number", mParam1);
                Intent intent = new Intent(getActivity(), VoiceActivity.class);
                intent.putExtras(args);
                startActivity(intent);

            }
        });

        lvLista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvLista.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                toolbar.setVisibility(View.GONE);
                inflater.inflate(R.menu.context_menu_list, MenuUnwrapper.unwrap(menu));

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
                        Toast.makeText(v.getContext(), positions + " itens marcados ", Toast.LENGTH_SHORT).show();
                        mAdapter.clearSelection();
                        mode.finish();
                        break;
                    case R.id.action_remove_item:
                        affectedRows = deleteSelectedItems(itemList);
                        mAdapter.clearSelection();
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
                ObjetoLista objetoLista = lista.get(position);
                if (checked) {
                    nr++;
                    mAdapter.setNewSelection(objetoLista.getId(), checked);
                    selectItems();
                } else {
                    nr--;
                    mAdapter.removeSelection(objetoLista.getId());
                    deselectItems(objetoLista);
                }
                mode.setTitle(nr + " ítens selecionados!");
            }
        });

        // carrega as propagandas
        loadAdMob();

        return v;
    }

    private void refreshList() {
        lista = listaDAO.list(new ObjetoLista());
        mAdapter = new ListaSelectionAdapter(getActivity().getBaseContext(), R.layout.lista_adapter, R.id.tvLista, lista);
        lvLista.setAdapter(mAdapter);
    }

    private void selectItems() {
        itemList = new ArrayList<ObjetoLista>();
        Set<Integer> checkedItemIds = mAdapter.getCurrentCheckedPosition();
        for (long l : checkedItemIds) {
            ObjetoLista objetoLista = listaDAO.getById(l);
            itemList.add(objetoLista);
        }
    }

    private void deselectItems(ObjetoLista objetoLista) {
        itemList.remove(objetoLista);
    }

    private long deleteSelectedItems(final List<ObjetoLista> itemList) {
        affectedRows = (long) 0;
        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Deletar")
                .setMessage("Tem certeza de que deseja remover os registros selecionados?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        affectedRows = listaDAO.deleteList(itemList);
                        Toast.makeText(getActivity(), affectedRows + " itens deletados ", Toast.LENGTH_SHORT).show();
                        refreshList();
                    }
                }).setNegativeButton("Não", null).show();

        return affectedRows;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean isOpened = mNavigationDrawerFragment.isDrawerOpen();
        menu.findItem(R.id.action_search).setVisible(!isOpened);
        menu.findItem(R.id.action_new).setVisible(!isOpened);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.voice_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CarregarEncontrados(newText.toLowerCase(Locale.ENGLISH));
                mAdapter = new ListaSelectionAdapter(getActivity(), R.layout.lista_items_adapter, R.id.tvItem, found);
                lvLista.setAdapter(mAdapter);

                return false;
            }
        });

    }

    public void CarregarEncontrados(String query) {

        String titulo = "";

        found.clear();
        for (int i = 0; i < lista.size(); i++) {
            if (query.length() <= lista.get(i).getListName().length()) {
                titulo = lista.get(i).getListName().toLowerCase(Locale.ENGLISH);
                if (titulo.contains(query)) {
                    found.add(lista.get(i));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        if (id == R.id.action_new) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAdMob(){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR) // All emulators
                .addTestDevice("00097237322f3f") // samsung
                .addTestDevice("04cd8de4d98a0cc6") // lg
                .addKeyword("list lista produtividade voice list").build();
        adView.loadAd(adRequest);
    }

}
