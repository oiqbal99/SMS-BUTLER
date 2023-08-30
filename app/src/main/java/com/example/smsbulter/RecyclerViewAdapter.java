package com.example.smsbulter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private final ArrayList<UserResponses> mTextMessages;
    private final Context mContext;
    private UserResponses selected;
    private final SwitchCompat mTest ;
    private final TextView mDisplayActiveMessage;
    private final CheckBox mDisplayActiveCheckBox;
    private final TextView mNoResponseSelected;
    private final LinearLayout mActiveResponseContainer;
    private final OnMessageContainerListener mOnMessageContainerListener;
    private final LiteDatabaseManager mDatabaseHelper;
    private int lastSelectedPosition = -1;

    public RecyclerViewAdapter(Context context,
                               OnMessageContainerListener onMessageContainerListener, LinearLayout activeMessageContainer, TextView noSelectedResponse ,
                               TextView displayActiveResponse, CheckBox displayActiveCheckBox,SwitchCompat test){
        mContext = context;
        mTest = test;
        mDisplayActiveMessage = displayActiveResponse;
        mDisplayActiveCheckBox = displayActiveCheckBox;
        mNoResponseSelected =noSelectedResponse;
        mActiveResponseContainer = activeMessageContainer;
        mDatabaseHelper = new LiteDatabaseManager(mContext);
        mTextMessages = mDatabaseHelper.getAllMessages();
        this.mOnMessageContainerListener = onMessageContainerListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
        ViewHolder holder  = new ViewHolder(view, mOnMessageContainerListener);

        return holder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.SMS_Message_Container.setText(mTextMessages.get(position).getMessage());
        holder.automatic.setChecked(mTextMessages.get(position).isActive());
        if(mTextMessages.get(position).isActive()){
            lastSelectedPosition = holder.getAdapterPosition();
        }



    }




    public void updateResponseAtIndex(){
        for(int i = 0; i < mTextMessages.size(); i++){
            Log.d(TAG, "updateResponseAtIndex: Changed and notified");
            mTextMessages.get(i).setActive(false);
        }
        notifyDataSetChanged();
    }

    private void updateCheckedMessage(int adapterPosition, boolean checked) {
        mTextMessages.get(adapterPosition).setActive(checked);
        if(lastSelectedPosition  !=  -1){updateLastChecked();}
        notifyItemChanged(adapterPosition);
    }

    public void updateLastChecked(){
        mTextMessages.get(lastSelectedPosition).setActive(false);
        notifyItemChanged(lastSelectedPosition);
    }


    @Override
    public int getItemCount() {
        return mTextMessages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox automatic;
        TextView SMS_Message_Container;
        RelativeLayout itemContainer;
        OnMessageContainerListener onMessageContainerListener;

        public ViewHolder(View itemView, OnMessageContainerListener onMessageContainerListener) {
            super(itemView);

            SMS_Message_Container = itemView.findViewById(R.id.sms_text_container);
            automatic = itemView.findViewById(R.id.auto_toggle);
            itemContainer = itemView.findViewById(R.id.item_holder_layout);
            this.onMessageContainerListener = onMessageContainerListener;
            itemView.setOnClickListener(this);



            automatic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //updateCheckedMessage(holder.getAdapterPosition(), holder.automatic.isChecked());
                    UserResponses currentResponse = mTextMessages.get(getAdapterPosition());
                    boolean newState = automatic.isChecked();
                    currentResponse.setActive(newState);
                    int messageId = mDatabaseHelper.getItemID(currentResponse);
                    mDatabaseHelper.changeActiveState(messageId, currentResponse);
                    updateCheckedMessage(getAdapterPosition(), automatic.isChecked());
                    if(automatic.isChecked()){
                        lastSelectedPosition = getAdapterPosition();
                    }

                    if (mTest.isChecked()){
                        selected = mDatabaseHelper.getActiveMessage();
                        if(selected == null){
                            mNoResponseSelected.setVisibility(View.VISIBLE);
                            mDisplayActiveMessage.setVisibility(View.GONE);
                            mDisplayActiveCheckBox.setVisibility(View.GONE);
                        }else{
                            mNoResponseSelected.setVisibility(View.INVISIBLE);
                            mDisplayActiveMessage.setVisibility(View.VISIBLE);
                            mDisplayActiveCheckBox.setVisibility(View.VISIBLE);
                            mDisplayActiveMessage.setText(selected.getMessage());
                            mDisplayActiveCheckBox.setChecked(selected.isActive());
                        }
                    }

                }
            });

            automatic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        lastSelectedPosition = -1;
                    }
                }
            });

        }

        @Override
        public void onClick(View v) {
                onMessageContainerListener.onMessageContainerClick(getAdapterPosition());
        }
    }

    public interface OnMessageContainerListener{
        void onMessageContainerClick(int position);
    }
}
