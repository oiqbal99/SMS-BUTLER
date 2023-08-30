package com.example.smsbulter;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class SMSReplier extends BroadcastReceiver{
    private static final String TAG = "SMSReplier";
    final  SmsManager sms;
    // MUST IMPLEMENT TOGGLE FEATURE;


    public SMSReplier(){
        sms = SmsManager.getDefault();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        LiteDatabaseManager liteDatabaseManager = new LiteDatabaseManager(context);
        Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onReceive: Message Received");

        if(MainActivity.getToggleState()){
            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Bundle bundle = intent.getExtras();
                SmsMessage [] msgs;
                if(bundle != null){
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];

                        for(int i = 0; i < msgs.length; i++){
                            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                            String msg_from = msgs[i].getOriginatingAddress();
                            //String msgBody = msgs[i].getMessageBody();

                            Log.w(TAG, "onReceive: sent message: " + liteDatabaseManager.getActiveMessage().getMessage());
                            //Local SMS messenger app object sending data (recipient, scAddress)
                            sms.sendTextMessage(msg_from, null, liteDatabaseManager.getActiveMessage().getMessage() ,null,null);

                            //Toast.makeText(context, "From: " + msg_from + " Body: "+ msgBody, Toast.LENGTH_SHORT).show();
                            liteDatabaseManager.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
