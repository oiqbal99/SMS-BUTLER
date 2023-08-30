package com.example.smsbulter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeleteRecyclerViewAdapter extends RecyclerView.Adapter<DeleteRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "DRecyclerViewAdapter";
    private final ArrayList<UserResponses> mTextMessages;
    private final ArrayList<Integer> IDs;
    private final LiteDatabaseManager mDbManager;
    Context mContext;


    public DeleteRecyclerViewAdapter(Context context, ArrayList<UserResponses> mTextMsgs){
        mTextMessages = mTextMsgs;
        mContext = context;
        mDbManager = new LiteDatabaseManager(context);
        IDs = fillIDArrayList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_item_layout,parent,false);
        ViewHolder holder  = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.delete_SMS_Message_Container.setText(mTextMessages.get(position).getMessage());
        holder.selectedForDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.selectedForDelete.isChecked()){
                    int itemID= mDbManager.getItemID(mTextMessages.get(position));
                    IDs.add(position,itemID);
                }
            }
        });

        holder.selectedForDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    IDs.remove(position);
                }
            }
        });

    }

    public ArrayList<Integer> fillIDArrayList(){
        ArrayList<Integer>itemIDs = new ArrayList<>();
        for (int i = 0; i < mTextMessages.size(); i++){
            itemIDs.add(i,0);
        }
        return itemIDs;
    }


    public ArrayList<Integer> getIDs (){
        return this.IDs;
    }

    @Override
    public int getItemCount() {
        return mTextMessages.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox selectedForDelete;
        TextView delete_SMS_Message_Container;
        RelativeLayout itemContainer;

        public ViewHolder(View itemView){
            super(itemView);
            delete_SMS_Message_Container = itemView.findViewById(R.id.delete_sms_text_container);
            selectedForDelete = itemView.findViewById(R.id.delete_toggle);
            itemContainer = itemView.findViewById(R.id.delete_item_holder_layout);
        }
    }


}
