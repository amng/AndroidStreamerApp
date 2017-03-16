package com.home.amngomes.controller.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.home.amngomes.controller.Constants;
import com.home.amngomes.models.Server;
import com.home.amngomes.ultrastarsongviewer.R;
import com.home.amngomes.views.FileExplorerActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {

    private ArrayList<Server> servers = new ArrayList<>();

    public String getNextRange() {
        return "items=" + servers.size() + "-" + (servers.size()+20);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ServerAdapter(Context mContext) {
        servers = new ArrayList<>();
    }

    public void addServer(Server server) {
        servers.add(server);
    }

    public void clear(){
        servers.clear();
    }


    @Override
    public void onBindViewHolder(final ServerViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setServerIpAddress(servers.get(position).ip);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ServerViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_server, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ServerViewHolder vh = new ServerViewHolder(v);
        v.setTag(vh);
        return vh;
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return servers.size();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ServerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        @BindView(R.id.server_ip)
        TextView serverIp;

        String serverIpAddress = "";

        ServerViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            ButterKnife.bind(this, view);
        }


        void setServerIpAddress(String serverIpAddress){
            this.serverIpAddress = serverIpAddress;
            serverIp.setText(serverIpAddress);
        }

        @Override
        public void onClick(final View view) {
            Intent intent = new Intent(view.getContext(), FileExplorerActivity.class);
            Bundle b = new Bundle();
            b.putString(Constants.Bundle.SERVER, serverIpAddress);
            intent.putExtras(b);
            view.getContext().startActivity(intent);
        }
    }



}