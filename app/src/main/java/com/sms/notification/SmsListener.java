package com.sms.notification;

/**
 * Created by ruslan on 11/21/17.
 */

public interface SmsListener {
    void messageReceived(String messageText);
}
