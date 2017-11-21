package com.sms.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ruslan on 11/21/17.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        SmsMessage smsMessage = msgs[0];

        String sender = smsMessage.getDisplayOriginatingAddress();
        //if (sender.compareTo("102") == 0) {
            String messageBody = smsMessage.getMessageBody();
            Log.d("Main ", messageBody);
            Toast.makeText(context, "MessageReceiver : " + messageBody, Toast.LENGTH_LONG).show();
            if (mListener != null)
                mListener.messageReceived(messageBody);
        //} else
          //  Log.d("Main", "ignore sms from "+sender);
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public static void unbindListiners() {
        mListener = null;
    }
}

