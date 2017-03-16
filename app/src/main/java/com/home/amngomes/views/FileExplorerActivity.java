package com.home.amngomes.views;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.home.amngomes.controller.RetrofitClient;
import com.home.amngomes.controller.adapters.FileExplorerAdapter;
import com.home.amngomes.controller.interfaces.DataChangedListener;
import com.home.amngomes.models.ExplorerFile;
import com.home.amngomes.ultrastarsongviewer.R;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FileExplorerActivity extends AppCompatActivity implements DataChangedListener {

    private FileExplorerAdapter adapter;
    String currentDirectory = "C:\\";

    @BindView(R.id.file_recycler_view)
    SuperRecyclerView recyclerView;

    @BindView(R.id.left_drawer)
    ListView drawerList;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentDirectory = (String) savedInstanceState.get("CURRENT_DIR");
        }
        setContentView(R.layout.activity_file_explorer);
        ButterKnife.bind(this);

        new GetDisksTask().execute();
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        adapter = new FileExplorerAdapter();
        adapter.addObserver(this);
        recyclerView.setAdapter(adapter);
        new LoadFiles().execute();
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                new LoadFiles().execute();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT_DIR", currentDirectory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_explorer, menu);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.search(searchView.getQuery().toString());
                return false;
            }
        });
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Object o) {
        if (((ExplorerFile)o).isDir()) {
            new LoadFiles(((ExplorerFile)o).name).execute();
        } else {
            Log.e("Opening file", ((ExplorerFile)o).name);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(RetrofitClient.getInstance()
                            .getFilePath(currentDirectory, ((ExplorerFile)o).name)),
                    ((ExplorerFile)o).mime);
            startActivity(intent);
        }
    }

    private class LoadFiles extends AsyncTask<Void, Void, Void> {

        LoadFiles(){
        }

        LoadFiles(String dir){
            currentDirectory += "/" + dir;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            try {
                adapter.setFiles(RetrofitClient.getInstance().getService()
                        .getDirectories(currentDirectory).execute().body());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            recyclerView.setRefreshing(false);
            recyclerView.hideMoreProgress();
            setTitle();
        }
    }

    private class GetDisksTask extends AsyncTask<Void, Void, Void> {
        ArrayList<String> disks = new ArrayList<>();

        GetDisksTask(){}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            try {
                disks = RetrofitClient.getInstance().getService()
                        .getDisks().execute().body();
                Log.e("disks", disks.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            drawerList.setAdapter(new ArrayAdapter<>(FileExplorerActivity.this,
                    R.layout.row_disk, disks));
            // Set the list's click listener
            drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    currentDirectory = (String) adapterView.getAdapter().getItem(i);
                    new LoadFiles().execute();
                    drawerLayout.closeDrawers();
                }
            });
        }
    }

    public void setTitle(){
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(currentDirectory);
    }

    @Override
    public void onBackPressed() {
        Log.e("Current directory: ", currentDirectory);
        if (currentDirectory.lastIndexOf("/") == -1) {
            super.onBackPressed();

        } else {
            currentDirectory = currentDirectory.substring(0, currentDirectory.lastIndexOf("/"));
            new LoadFiles().execute();
        }
    }
}
