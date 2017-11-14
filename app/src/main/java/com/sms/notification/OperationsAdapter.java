package com.sms.notification;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sms.notification.model.Operation;

import java.util.List;

public class OperationsAdapter extends RecyclerView.Adapter<OperationsAdapter.MyViewHolder> {

    private List<Operation> operationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView operation, card, suma, desc;

        public MyViewHolder(View view) {
            super(view);
            operation = (TextView) view.findViewById(R.id.operation);
            card = (TextView) view.findViewById(R.id.card);
            suma = (TextView) view.findViewById(R.id.suma);
            desc = (TextView) view.findViewById(R.id.desc);
        }
    }


    public OperationsAdapter(List<Operation> operationList) {
        this.operationList = operationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.operation_row, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d("Main", "position: "+position);
        Operation operation = operationList.get(position);
        Log.d("Main", operation.toString());
        holder.operation.setText(operation.op.toString());
        holder.card.setText(operation.card);
        if (operation.suma != null)
            holder.suma.setText(operation.suma.toString());
        else
            holder.suma.setText("0 MDL");
        switch (operation.op) {
            case PLATA:
            case ACHITARE:
                holder.suma.setTextColor(Color.RED);
                break;
            case ALIMENTARE:
                holder.suma.setTextColor(Color.GREEN);
                break;
            case RETRAGERE:
                holder.suma.setTextColor(Color.BLUE);
                break;
            default:
                break;
        }
        holder.desc.setText(operation.desc);
    }

    @Override
    public int getItemCount() {
        return operationList.size();
    }

    public void addOperations(List<Operation> borrowModelList) {
        this.operationList = borrowModelList;
        notifyDataSetChanged();
    }

}