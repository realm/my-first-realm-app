package io.realm.todo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ItemsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ItemsRecyclerAdapter mItemsRecyclerAdapter;
    private Realm mRealm;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        mRealm = Realm.getDefaultInstance();
        mRecyclerView = findViewById(R.id.recycler_view);
        RealmResults<Item> items = mRealm
                .where(Item.class)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();

        mItemsRecyclerAdapter = new ItemsRecyclerAdapter(this, items);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mItemsRecyclerAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText taskEditText = new EditText(ItemsActivity.this);

                AlertDialog dialog = new AlertDialog.Builder(ItemsActivity.this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                Realm realm = ItemsActivity.this.mRealm;
                                realm.beginTransaction();

                                Item item = new Item();
                                item.setBody(task);
                                realm.insert(item);

                                realm.commitTransaction();

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mItemsRecyclerAdapter.removeListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
