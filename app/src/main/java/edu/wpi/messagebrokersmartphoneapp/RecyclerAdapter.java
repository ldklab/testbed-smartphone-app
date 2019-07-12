package edu.wpi.messagebrokersmartphoneapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> contentList = new ArrayList<>();
    private Context mContext;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView idView;
        TextView textView;
        Button button;
        ConstraintLayout parentLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            idView = itemView.findViewById(R.id.idView);
            textView = itemView.findViewById(R.id.contentTextView);
            button = itemView.findViewById(R.id.myButton);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public RecyclerAdapter(Context mContext, ArrayList<String> idList, ArrayList<String> contentList) {
        this.idList = idList;
        this.contentList = contentList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listeitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.idView.setText(idList.get(position));
        holder.textView.setText(contentList.get(position));

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MyDebug", "Click on button view id: " + idList.get(position) + " Position: " + position);
                Toast.makeText(mContext, idList.get(position), Toast.LENGTH_SHORT).show();
                removeItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }

    public void removeItem(int position) {
        idList.remove(position);
        contentList.remove(position);
        this.notifyDataSetChanged();
    }

}
