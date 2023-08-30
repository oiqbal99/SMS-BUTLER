package com.example.smsbulter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnMessageContainerListener {

    private static final int rCode = 1000;
    private static final String TAG = "MainActivity";
    private RecyclerViewAdapter adapter;
    private ImageButton mAddButton, mDeleteButton;
    private Button mAddResponseBtn;
    private SwitchCompat mAutoReply;
    private TextView mNoSelection;
    private TextView mSelectedActiveMessage;
    private CheckBox mSelectedActiveCheckBox;
    private UserResponses selectedResponse;
    private LinearLayout mActiveMessageContainer;
    public static final String MESSAGE_POS = "com.example.smsbutler.MESSAGE_POS";
    public static final String MESSAGE_CONTENT = "com.example.smsbutler.MESSAGE_CONTENT";
    public static final String MESSAGE_STATE = "com.example.smsbutler.MESSAGE_STATE";
    private final LiteDatabaseManager mDatabaseHelper = new LiteDatabaseManager(MainActivity.this);
    private ArrayList<UserResponses> allResultsInDatabase;
    public static boolean isOn;

    public MainActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, rCode);
        }



        sendPermission();
        mDeleteButton = findViewById(R.id.main_activity_delete_btn);
        mAddButton =  findViewById(R.id.main_activity_add_btn);
        mActiveMessageContainer = findViewById(R.id.active_message_container);
        mAddResponseBtn = findViewById(R.id.local_add_button);
        mAutoReply = findViewById(R.id.automatic_reply_state);
        mSelectedActiveMessage = findViewById(R.id.active_response_display_text_view);
        mNoSelection = findViewById(R.id.no_selected_active_responses);
        mSelectedActiveCheckBox = findViewById(R.id.active_response_display_checkbox);
        //Get the selected response on application launch
        selectedResponse = mDatabaseHelper.getActiveMessage();

        Log.d(TAG, "initTextMessagesAndSwitches");
        initRecyclerView();
        //Read all messages stored
    }

    public void sendPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Sending permission granted", Toast.LENGTH_SHORT).show();
            }
            else{
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, rCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String [] permissions, @NonNull int [] grantResults){
        if(requestCode == rCode){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onActiveMessageContainerClick(View View){
        UserResponses mActiveResponse  = mDatabaseHelper.getActiveMessage();
        if(mActiveResponse != null ){
            int activeID = mDatabaseHelper.getItemID(mActiveResponse);
            if(activeID > -1){
                Intent intent = new Intent(MainActivity.this, EditResponse.class);
                intent.putExtra(MESSAGE_POS, activeID);
                intent.putExtra(MESSAGE_CONTENT, mActiveResponse.getMessage());
                intent.putExtra(MESSAGE_STATE, mActiveResponse.isActive());
                startActivity(intent);
                Log.d(TAG, "onActiveMessageContainerClick: No item with ID: "+ activeID + " in database");
            }
        }

    }

    public void onAddImageIconClick(View view){
        //Sends Array List if ArrayList has at-least one element
        Intent intent = new Intent(MainActivity.this, AddNewResponse.class);
        //Start add new response element
        startActivity(intent);
    }


    public void onActiveCheckBoxClick(View view){
        selectedResponse = mDatabaseHelper.getActiveMessage();
        int activeId = mDatabaseHelper.getItemID(selectedResponse);
        Log.d(TAG, "onActiveCheckBoxClick: Selected response: " + selectedResponse.getMessage()+ " "+ selectedResponse.isActive());
        selectedResponse.setActive(false);
        mDatabaseHelper.changeActiveState(activeId, selectedResponse);
        adapter.updateResponseAtIndex();
        showNoSelection();
    }



    public void showNoSelection(){
        mNoSelection.setVisibility(View.VISIBLE);
        mSelectedActiveCheckBox.setVisibility(View.GONE);
        mSelectedActiveMessage.setVisibility(View.GONE);
    }

    public void showActiveSelection(){
        mNoSelection.setVisibility(View.GONE);
        mSelectedActiveCheckBox.setVisibility(View.VISIBLE);
        mSelectedActiveMessage.setVisibility(View.VISIBLE);
        mSelectedActiveCheckBox.setChecked(selectedResponse.isActive());
        mSelectedActiveMessage.setText(selectedResponse.getMessage());
    }

    public void onAutoSwitch(View v){
        if(mAutoReply.isChecked()){
            Log.d(TAG, "onAutoSwitch: switch on " + getToggleState());
            isOn = true;
            mActiveMessageContainer.setVisibility(View.VISIBLE);
            selectedResponse = mDatabaseHelper.getActiveMessage();
            if(selectedResponse == null){
               showNoSelection();
            }else{
               showActiveSelection();
            }
        }
        else{
            isOn = false;
            mActiveMessageContainer.setVisibility(View.GONE);
        }
    }

    public static boolean getToggleState(){
        return isOn;
    }

    public void updateAfterResume(){
        selectedResponse = mDatabaseHelper.getActiveMessage();
        if(mAutoReply.isChecked()){
            if(selectedResponse == null){
                showNoSelection();
            }else{
                showActiveSelection();
            }
        }

    }

    @Override
    protected void onResume(){
        initRecyclerView();
        updateAfterResume();
        super.onResume();

    }

    public void reAdjustView(ArrayList<UserResponses>res){
        if(res.isEmpty()){
            mAddResponseBtn.setVisibility(View.VISIBLE);
            mAddButton.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.GONE);
        }else{
            mAddResponseBtn.setVisibility(View.GONE);
            mAddButton.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.VISIBLE);
        }

    }

    public void onDeleteButtonClick(View view){
        Intent intent = new Intent(MainActivity.this, DeleteResponses.class);
        startActivity(intent);
    }

    public void addButtonWhenEmpty(View view){
        Intent intent = new Intent(this, AddNewResponse.class);
        startActivity(intent);
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init Recycler View");
        allResultsInDatabase = mDatabaseHelper.getAllMessages();
        reAdjustView(allResultsInDatabase);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(this, this, mActiveMessageContainer, mNoSelection,mSelectedActiveMessage,mSelectedActiveCheckBox, mAutoReply);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseHelper.close();
    }


    @Override
    public void onMessageContainerClick(int position) {
        UserResponses chosenResponse = allResultsInDatabase.get(position);
        Log.d(TAG, "onMessageContainerClick: chosen message is ---> " + chosenResponse.getMessage());
        int itemID = mDatabaseHelper.getItemID(chosenResponse);
        Log.d(TAG, "onContainerClick: Method Was clicked");
        if(itemID > -1){
            Intent intent = new Intent(MainActivity.this, EditResponse.class);
            intent.putExtra(MESSAGE_POS, itemID);
            intent.putExtra(MESSAGE_CONTENT, chosenResponse.getMessage());
            intent.putExtra(MESSAGE_STATE, chosenResponse.isActive());
            startActivity(intent);
            Log.d(TAG, "onMessageContainerClick: No item with ID: "+ itemID + " in database");
        }

        Log.d(TAG, "onMessageContainerClick: No item with ID: "+ itemID + " in database");
        mDatabaseHelper.close();
    }
}