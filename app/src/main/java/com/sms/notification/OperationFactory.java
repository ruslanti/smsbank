package com.sms.notification;

import com.sms.notification.model.Operation;

import java.text.ParseException;

/**
 * Created by ruslan on 11/8/17.
 */
public interface OperationFactory {
    public Operation getOperation(String msg) throws ParseException;
}
