package com.home.amngomes.controller.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.home.amngomes.controller.interfaces.DataChangedListener;
import com.home.amngomes.models.ExplorerFile;
import com.home.amngomes.ultrastarsongviewer.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FileExplorerAdapter extends RecyclerView.Adapter<FileExplorerAdapter.FileViewHolder> {

    private ArrayList<ExplorerFile> files = new ArrayList<>();
    private ArrayList<ExplorerFile> backupfiles = new ArrayList<>();
    private ArrayList<DataChangedListener> observers = new ArrayList<>();



    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_file, parent, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        holder.setFileName(files.get(position));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void setFiles(ArrayList<ExplorerFile> files){
        if (files != null) {
            this.files = files;
            this.backupfiles = files;
        }
    }

    public void search(String query){
        ArrayList<ExplorerFile> query_files = new ArrayList<>();
        for (ExplorerFile file : backupfiles) {
            if (file.name.toUpperCase().contains(query.toUpperCase())) {
                query_files.add(file);
            }
        }
        files = query_files;
        notifyDataSetChanged();
    }

    public void addObserver(DataChangedListener observer){
        observers.add(observer);
    }

    public void clear(){
        files.clear();
    }

    class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_file)
        TextView file;

        @BindView(R.id.file_icon)
        ImageView icon;

        ExplorerFile explorerFile = null;

        FileViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            ButterKnife.bind(this, view);
        }


        void setFileName(ExplorerFile explorerFile){
            this.explorerFile = explorerFile;
            file.setText(explorerFile.name);
            if (explorerFile.isDir()) {
                icon.setImageDrawable(itemView.getContext()
                        .getDrawable(R.drawable.ic_folder_black_24px));
            } else {
                icon.setImageDrawable(itemView.getContext()
                        .getDrawable(R.drawable.ic_insert_drive_file_black_24px));
            }
        }

        @Override
        public void onClick(final View view) {
            for (DataChangedListener observer : observers) {
                observer.update(explorerFile);
            }
        }
    }
}
