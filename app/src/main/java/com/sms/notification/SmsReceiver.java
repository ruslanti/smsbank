package com.sms.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by ruslan on 11/21/17.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        SmsMessage smsMessage = msgs[0];

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String origin = sharedPref.getString("phone_number", "102");

        String sender = smsMessage.getDisplayOriginatingAddress();
        if (sender.compareTo(origin) == 0) {
            String messageBody = smsMessage.getMessageBody();
            Log.d("Main ", messageBody);
            if (mListener != null)
                mListener.messageReceived(messageBody);
        } else
            Log.d("Main", "ignore sms from "+sender);
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public static void unbindListiners() {
        mListener = null;
    }
}

