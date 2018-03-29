package com.sms.notification;

import android.util.Log;

import com.sms.notification.model.Op;
import com.sms.notification.model.Operation;
import com.sms.notification.model.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by ruslan on 11/8/17.
 */

public class MaibOperationFactory implements OperationFactory {
    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
    enum Key {OP, CARD, STATUS, SUMA, DISP, DATA, LOCATIE, SUPORT, OTHER};

    private AmountFactory amountFactory = new AmountFactory();

    @Override
    public Operation getOperation(String msg) throws ParseException {
        Operation operation = new Operation();
        String[] lines = msg.split(System.getProperty("line.separator"));
        for (String line: lines) {
            int pos = line.indexOf(':');
            if (pos <= 0)
                throw new ParseException("Could not parse: "+line, 0);
            String key = line.substring(0, pos);
            String value = line.substring(pos+1).trim();

            switch(parseKey(key)) {
                case OP:
                    operation.op = parseOp(value);
                    break;
                case CARD:
                    operation.card = value;
                    break;
                case STATUS:
                    operation.status = parseStatus(value);
                    break;
                case SUMA:
                    operation.suma = amountFactory.getAmount(value);
                    break;
                case DISP:
                    operation.disp = amountFactory.getAmount(value).amount;
                    break;
                case DATA:
                    operation.data = formatter.parse(value);
                    break;
                case LOCATIE:
                    operation.desc = value;
                    break;
                case SUPORT:
                    break;
                default:
                    Log.d(MaibOperationFactory.class.toString(), "Unknown: "+key+ "="+value);
                    break;
            }
        }

        return operation;
    }

    private Key parseKey(String name) {
        if (name.startsWith("Op"))
            return Key.OP;
        if (name.startsWith("Card"))
            return Key.CARD;
        if (name.startsWith("Statut"))
            return Key.STATUS;
        if (name.startsWith("Suma"))
            return Key.SUMA;
        if (name.startsWith("Disp"))
            return Key.DISP;
        if (name.startsWith("Data"))
            return Key.DATA;
        if (name.startsWith("Locatie"))
            return Key.LOCATIE;

        return Key.OTHER;
    }

    private Op parseOp(String value) throws ParseException {
        if (value.startsWith("Retragere"))
            return Op.RETRAGERE;
        if (value.startsWith("Alimentare"))
            return Op.ALIMENTARE;
        if (value.startsWith("Sold"))
            return Op.SOLD;
        if (value.startsWith("Marfuri"))
            return Op.ACHITARE;
        if (value.startsWith("Plata"))
            return Op.PLATA;

        throw new ParseException("Invalid operation: "+value, 0);
    }

    private Status parseStatus(String value) throws ParseException {
        if (value.startsWith("Respins"))
            return Status.RESPINS;
        if (value.startsWith("Reusit"))
            return Status.REUSIT;
        throw new ParseException("Invalid statut: "+value, 0);
    }
}
