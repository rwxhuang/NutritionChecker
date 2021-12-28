package com.nutrition.checker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder>
{
    private Context context;
    private OnItemClickListener onItemClickListener;
    public boolean wasClicked;
    public ArrayList<Label> data;
    public HashMap<String, Bitmap> images;

    public Adapter(ArrayList mData, Context context, HashMap<String, Bitmap> image)
    {
        this.data = mData;
        this.context = context;
        this.wasClicked = false;
        this.images = image;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.items, parent, false);
        return new MyViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        final MyViewHolder holders = holder;
        Label model = data.get(position);

        holder.title.setText(model.getName());
        holder.desc.setText((""+model.getCarbs()));
        holder.score.setText("Score: "+model.getScore());
        holder.imgView.setImageBitmap(images.get(model.getName()));
        /*
       byte[] imageAsBytes = Base64.decode(model.getBase64String().getBytes(), Base64.DEFAULT);
       holder.imgView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
         */
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener =  onItemClickListener;
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener
    {
        TextView title, desc, score;
        ImageView imgView;
        OnItemClickListener onItemClickListener;
        public MyViewHolder(View itemView, OnItemClickListener onItemClickListener)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            score = itemView.findViewById(R.id.score);
            imgView = itemView.findViewById(R.id.img);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v)
        {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

}
