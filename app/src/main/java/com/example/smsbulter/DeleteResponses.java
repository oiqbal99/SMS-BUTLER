package com.example.smsbulter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class DeleteResponses extends AppCompatActivity {

    private Button mDeleteBtn;
    private RecyclerView deleteRecyclerView;
    private static final String TAG = "DeleteResponses" ;
    private ArrayList<UserResponses> myMessages;
    private DeleteRecyclerViewAdapter adapter;

    private final LiteDatabaseManager mDatabaseHelper = new LiteDatabaseManager(DeleteResponses.this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_responses);
        deleteRecyclerView = findViewById(R.id.delete_recycler_view);
        mDeleteBtn = findViewById(R.id.delete_responses_button);
        initRecyclerView();
    }

    public void onDeleteClick(View view){
        ArrayList<Integer>deleteIDs = adapter.getIDs();
        if(deleteIDs.size() == 1){
            mDatabaseHelper.deleteOneItem(deleteIDs.get(0));
        }else if(deleteIDs.size() > 1){
            mDatabaseHelper.deleteMultipleItems(deleteIDs);
        }else{
            return;
        }
        mDatabaseHelper.close();
        finish();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initDeleteRecyclerView: init Recycler View");
        myMessages = mDatabaseHelper.getAllMessages();
        adapter = new DeleteRecyclerViewAdapter(DeleteResponses.this,myMessages);
        deleteRecyclerView.setAdapter(adapter);
        deleteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}