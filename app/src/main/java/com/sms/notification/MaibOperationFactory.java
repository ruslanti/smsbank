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
    enum Key {OP, CARD, STATUS, SUMA, DISP, DATA, LOCATIE, OTHER};

    @Override
    public Operation getOperation(String msg) throws ParseException {
        String[] lines = msg.split(System.getProperty("line.separator"));
        for (String line: lines) {
            int pos = line.indexOf(':');
            if (pos <= 0)
                throw new ParseException("Could not parse: "+line, 0);
            String key = line.substring(0, pos);
            String value = line.substring(pos+1).trim();

            Operation operation = new Operation();

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
                    operation.suma = value;
                    break;
                case DISP:
                    operation.disp = value;
                    break;
                case DATA:
                    operation.data = formatter.parse(value);
                    break;
                case LOCATIE:
                    operation.desc = value;
                    break;
                default:
                    Log.d(MaibOperationFactory.class.toString(), "Unknown: "+key+ "="+value);
                    break;
            }
        }

        return null;
    }

    private Key parseKey(String name) {
        if (name.startsWith("Op"))
            return Key.OP;
        else if (name.startsWith("Card"))
            return Key.CARD;
        else if (name.startsWith("Status"))
            return Key.STATUS;
        else if (name.startsWith("Suma"))
            return Key.SUMA;
        else if (name.startsWith("Disp"))
            return Key.DISP;
        else if (name.startsWith("Data"))
            return Key.DATA;
        else if (name.startsWith("Locatie"))
            return Key.LOCATIE;
        else
            return Key.OTHER;
    }

    private Op parseOp(String value) throws ParseException {
        if (value.startsWith("Retragere"))
            return Op.RETRAGERE;
        if (value == "Alimentare")
            return Op.ALIMENTARE;
        if (value == "Sold")
            return Op.SOLD;
        if (value.startsWith("Marfuri"))
            return Op.ACHITARE;
        if (value == "Plata")
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
