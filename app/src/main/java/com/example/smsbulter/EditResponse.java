package com.example.smsbulter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class EditResponse extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int itemID;
    private String oldResponse;
    private boolean givenState;
    private CheckBox mEditCheckBox;
    private EditText mEditMessage;
    private final LiteDatabaseManager mDataBaseManager = new LiteDatabaseManager(this);
    private boolean changeActiveState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_response);

        mEditMessage = findViewById(R.id.edit_response_edit_text);
        mEditCheckBox = findViewById(R.id.edit_responses_checkbox);
        getIncomingIntent();

    }

    private void getIncomingIntent(){
        Log.d(TAG, "Edit Activity: getIncomingIntent");
        Intent intent = getIntent();
        if(intent.hasExtra(MainActivity.MESSAGE_CONTENT) && intent.hasExtra(MainActivity.MESSAGE_POS) && intent.hasExtra(MainActivity.MESSAGE_STATE)){
            Log.d(TAG, "Edit Activity: has Extras");
            itemID = intent.getIntExtra(MainActivity.MESSAGE_POS, -1);
            givenState = intent.getBooleanExtra(MainActivity.MESSAGE_STATE,false);
            changeActiveState = givenState;
            mEditCheckBox.setChecked(givenState);
            Log.d(TAG, "getIncomingIntent: item itemID from getIntExtra: " + itemID);
            oldResponse = intent.getStringExtra(MainActivity.MESSAGE_CONTENT);
            mEditMessage.setText(oldResponse);
        }
    }

    public void onSaveClick(View View){
        String editedResponse = mEditMessage.getText().toString().trim();
        if(TextUtils.isEmpty(editedResponse)){
            mEditMessage.setError("Invalid Entry");
            return;
        }
        UserResponses newEditedResponse = new UserResponses(editedResponse, changeActiveState);
        mDataBaseManager.editChosenResponse(itemID, oldResponse, newEditedResponse);
        mDataBaseManager.close();
        Toast.makeText(this, "Body is now not empty", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onCancelClick(View view){
        finish();
    }

}