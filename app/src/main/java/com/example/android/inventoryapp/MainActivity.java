package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.MobileContract.MobileEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private MobileDbHelper mDbHelper;

    private static final int MOBILE_LOADER = 0;

    MobileCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intialising fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new MobileDbHelper(this);

        ListView listView = (ListView) findViewById(R.id.list);//Intitialising Listview

        View emptyView = findViewById(R.id.empty_view);// empty view

        listView.setEmptyView(emptyView);

        mCursorAdapter = new MobileCursorAdapter(this, null);

        listView.setAdapter(mCursorAdapter); //Attaching adapter to listview

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(MobileEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(MOBILE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        deleteAllPets();
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {MobileEntry._ID, MobileEntry.COLUMN_NAME, MobileEntry.COLUMN_PRICE, MobileEntry.COLUMN_STOCK};

        return new CursorLoader(this, MobileEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);
    }

    //Deletes All pets
    private void deleteAllPets (){
        int rowsDeleted = getContentResolver().delete(MobileEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + "rows deleted from database");
    }
}
