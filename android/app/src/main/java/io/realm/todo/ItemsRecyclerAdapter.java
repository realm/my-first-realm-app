package io.realm.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.annotation.Nullable;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by maximilianalexander on 1/17/18.
 */

class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.ViewHolder> {

    private RealmResults<Item> mResults;
    private LayoutInflater mLayoutInflater;
    private final OrderedRealmCollectionChangeListener<RealmResults<Item>> mChangeListener;

    ItemsRecyclerAdapter(Context context, RealmResults<Item> results) {
        super();
        mResults = results;
        mLayoutInflater = LayoutInflater.from(context);
        mChangeListener = new OrderedRealmCollectionChangeListener<RealmResults<Item>>() {
            @Override
            public void onChange(@NonNull RealmResults<Item> items, @Nullable OrderedCollectionChangeSet changeSet) {
                if (changeSet == null) {
                    notifyDataSetChanged();
                    return;
                }
                // For deletions, the adapter has to be notified in reverse order.
                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (int i = deletions.length - 1; i >= 0; i--) {
                    OrderedCollectionChangeSet.Range range = deletions[i];
                    notifyItemRangeRemoved(range.startIndex, range.length);
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    notifyItemRangeInserted(range.startIndex, range.length);
                }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    notifyItemRangeChanged(range.startIndex, range.length);
                }
            }
        };
        results.addChangeListener(mChangeListener);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.body_textview);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mResults.get(position);
        holder.text.setText(item.getBody());
    }

    void removeListener() {
        mResults.removeChangeListener(mChangeListener);
    }
}
