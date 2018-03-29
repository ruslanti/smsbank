package com.sms.notification;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sms.notification.model.Operation;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

public class OperationsAdapter extends GroupsRecyclerViewAdapter {

    private static final int TITLE_VIEW_TYPE = 0;
    private static final int MODEL_VIEW_TYPE = 1;

    private static class GroupTitle extends Group<String, TitleViewHolder> {

        GroupTitle(@NonNull List<String> items) {
            super(TITLE_VIEW_TYPE, items);
        }

        @Override
        public void onBindViewHolder(TitleViewHolder holder, int position) {
            holder.title.setText(getItems().get(position));
        }

        @Override
        public TitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     @NonNull LayoutInflater inflater) {
            View v = inflater.inflate(R.layout.operation_title, parent, false);
            return new TitleViewHolder(v);
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;

        TitleViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
        }
    }

    public static class OperationViewHolder extends RecyclerView.ViewHolder {
        public TextView operation, card, suma, desc;

        public OperationViewHolder(View view) {
            super(view);
            operation = view.findViewById(R.id.operation);
            card = view.findViewById(R.id.card);
            suma = view.findViewById(R.id.suma);
            desc = view.findViewById(R.id.desc);
        }
    }

    private static class GroupOperations extends GroupsRecyclerViewAdapter.Group<Operation, OperationViewHolder> {

        GroupOperations(@NonNull List<Operation> items) {
            super( MODEL_VIEW_TYPE, items);
        }

        @Override
        public void onBindViewHolder(OperationViewHolder holder, int position) {
            Operation operation = getItems().get(position);
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
        public OperationViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     @NonNull LayoutInflater inflater) {
            View v = inflater.inflate(R.layout.operation_row, parent, false);
            return new OperationViewHolder(v);
        }
    }

    public void addOperations(List<Operation> borrowModelList) {
        clearGroups();
        int fromIndex = 0;
        String fromData = null;
        for (int index = 0; index < borrowModelList.size(); index++) {
            Operation o = borrowModelList.get(index);
            String data = DateFormat.getDateInstance(DateFormat.MEDIUM).format(o.data);
            if (!data.equals(fromData)) {
                if (fromIndex != index) {
                    addGroup(new GroupOperations(borrowModelList.subList(fromIndex, index)));
                }
                fromData = data;
                addGroup(new GroupTitle(Arrays.asList(fromData)));
                fromIndex = index;
            }

        }
        addGroup(new GroupOperations(borrowModelList.subList(fromIndex, borrowModelList.size())));
        notifyDataSetChanged();
    }

}