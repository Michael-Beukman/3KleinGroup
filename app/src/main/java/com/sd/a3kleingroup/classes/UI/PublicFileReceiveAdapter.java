package com.sd.a3kleingroup.classes.UI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;

import java.util.ArrayList;

//creating own custom adapter
public class PublicFileReceiveAdapter extends RecyclerView.Adapter<PublicFileReceiveAdapter.ViewHolder> {

    RecyclerView recyclerView;
    Context context;
    ArrayList<String> items = new ArrayList<>(); //the text
    ArrayList<String> urls = new ArrayList<>();

    public PublicFileReceiveAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls = urls;
    }

    //@NonNull
    //creating views for the recycler view items
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.public_friend_items, parent, false); //the view for our items
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //initialise the elements of individual items
        //the position will do for each individual position
        holder.fileName.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        //return the number of items
        return  items.size();
    }

        //purpose is to create individual recycler view items
        //will store these views
        public class ViewHolder extends RecyclerView.ViewHolder{
            //contents of the XML for the card
            TextView fileName;


            //constructor
            // representing individual items
            public ViewHolder(View itemView){
                super(itemView);
                fileName = itemView.findViewById(R.id.public_file_text_view_name);
                // TODO: 2020/04/30 add other pieces of info

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = recyclerView.getChildLayoutPosition(v); //gets this specific view
                        Intent intent = new Intent();
                        intent.setType(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(urls.get(position))); //this intent is to get the data
                        context.startActivity(intent); //launch browser to display corresponding file

                    }
                });
            }

        }



}
