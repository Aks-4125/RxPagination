package com.example.akashb.rxpagination;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Akash on 11/11/17.
 */

public class RxPagingAdapter extends RecyclerView.Adapter<RxPagingAdapter.ItemHolder> {

    private List<String> itemArrayList = new ArrayList<>();

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ItemHolder(item);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.tvItemName.setText(itemArrayList.get(holder.getLayoutPosition()));
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }


    class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvItemName)
        TextView tvItemName;

        ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    void addItems(List<String> items) {
        this.itemArrayList.addAll(items);
    }

}
