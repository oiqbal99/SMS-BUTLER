package com.example.smsbulter;


import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.Toast;


public class AddNewResponse extends AppCompatActivity {

    Button mCancelButton, mAddResponseButton;
    LiteDatabaseManager mDataBaseHelper = new LiteDatabaseManager(AddNewResponse.this);
    private EditText mMessage;
    private CheckBox mActive;
    UserResponses newMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_response);

        mCancelButton = findViewById(R.id.cancel_btn);
        mAddResponseButton = findViewById(R.id.add_new_response_button);
        mMessage = findViewById(R.id.add_new_response_edit_text);
        mActive = findViewById(R.id.add_responses_checkbox);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void addMessage(View view){

        String myNewResponse = mMessage.getText().toString().trim();
        boolean isChecked = mActive.isChecked();

        if(TextUtils.isEmpty(myNewResponse)){
            mMessage.setError("Invalid Entry");
            return;
        }
        newMessage = new UserResponses(myNewResponse, isChecked);
        boolean insertData = mDataBaseHelper.addDataToDatabase(newMessage);
        if(insertData){
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Data Not Inserted", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mDataBaseHelper.close();
    }
}