package com.home.amngomes.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.home.amngomes.controller.Constants;
import com.home.amngomes.controller.RetrofitClient;
import com.home.amngomes.controller.adapters.ServerAdapter;
import com.home.amngomes.models.Server;
import com.home.amngomes.ultrastarsongviewer.R;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private ServerAdapter adapter;
    private LinearLayoutManager layoutManager;

    @BindView(R.id.file_recycler_view)
    SuperRecyclerView recyclerView;

    @BindView(R.id.server_ip)
    EditText serverIp;

    @OnClick(R.id.connect_btn)
    public void setIp(){
        Intent intent = new Intent(this, FileExplorerActivity.class);
        Bundle b = new Bundle();
        b.putString(Constants.Bundle.SERVER, serverIp.getText().toString());
        if (RetrofitClient.getInstance().updateServerIp(serverIp.getText().toString())){
            intent.putExtras(b);
            this.startActivity(intent);
        } else {
            Snackbar.make(serverIp, "Could not connect to server", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new ServerAdapter(this);
        recyclerView.setAdapter(adapter);
        new LoadServers().execute();
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                new LoadServers().execute();
            }
        }, 10);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                new LoadServers().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class LoadServers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            Server server = new Server();
            server.ip = RetrofitClient.getInstance().getIp();
            adapter.addServer(server);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            recyclerView.setRefreshing(false);
            recyclerView.hideMoreProgress();
        }
    }

}
