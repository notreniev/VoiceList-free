package br.com.evknz.vol.free;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.plus.PlusShare;

public class NavigationDrawerActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, PlayStoreFragment.OnFragmentInteractionListener, ListaFragment.OnFragmentInteractionListener {

    private static final String TAG = NavigationDrawerActivity.class.getSimpleName();

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private static int section_number = 0;

    private CharSequence mTitle;
    private Toolbar toolbar;

    public Toolbar getToolbar() {
        return toolbar;
    }

    public static PlusShare.Builder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        insertFragment(position+1);
        setActivityTitle(position+1);
    }

    private void setActivityTitle(int position){
        switch (position) {
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
            case 4:
                mTitle = getString(R.string.title_section5);
                break;
            case 5:
                insertFragment(1);

                mTitle = getString(R.string.title_section2);
//                Intent intent = new Intent(this, PlayStoreActivity.class);
//                startActivityForResult(intent, 0);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=br.com.evknz.vol"));
                startActivity(intent);

                break;
        }

        setTitle(mTitle);
    }

    private void insertFragment(int position){
        getFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number+1) {
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
            case 4:
                mTitle = getString(R.string.title_section5);
                break;
        }

        setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.voice_list, menu);
            //restoreActionBar();
            return true;
        }else{
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_new) {
            Toast.makeText(getBaseContext(), "Nova lista", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), NewListActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_archive){
            Toast.makeText(getBaseContext(), "Arquivar", Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.action_trash){
            Toast.makeText(getBaseContext(), "Deletar", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment = new Fragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            if (sectionNumber <= 4) {
                ListaFragment listaFragment = new ListaFragment();
                fragment = listaFragment;
//            }else{
//                PlayStoreFragment playStoreFragment = new PlayStoreFragment();
//                fragment = playStoreFragment;
            }

            fragment.setArguments(args);
            section_number=sectionNumber;

            return fragment;
        }

            @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((NavigationDrawerActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
