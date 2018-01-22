package io.realm.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import javax.annotation.Nullable;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.ViewHolder> {

    private RealmResults<Item> mResults;
    private Realm mRealm;
    private LayoutInflater mLayoutInflater;
    private final OrderedRealmCollectionChangeListener<RealmResults<Item>> mChangeListener;

    ItemsRecyclerAdapter(Context context, Realm realm) {
        super();
        mRealm = realm;
        mResults = mRealm
                .where(Item.class)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();
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
        mResults.addChangeListener(mChangeListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnClickListener {
        TextView textView;
        CheckBox checkBox;
        private Item mItem;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.body);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(this);
        }

        void setItem(Item item){
            this.mItem = item;
            this.textView.setText(item.getBody());
            this.checkBox.setChecked(item.getIsDone());
        }

        @Override
        public void onClick(View v) {
            Realm realm = this.mItem.getRealm();
            realm.beginTransaction();
            mItem.setIsDone(!this.mItem.getIsDone());
            realm.commitTransaction();
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
        holder.setItem(item);
    }

    void removeItemAtPosition(int position) {
        mRealm.beginTransaction();
        Item item = mResults.get(position);
        if (item != null) {
            item.deleteFromRealm();
        }
        mRealm.commitTransaction();

    }

    void removeListener() {
        mResults.removeChangeListener(mChangeListener);
    }
}
