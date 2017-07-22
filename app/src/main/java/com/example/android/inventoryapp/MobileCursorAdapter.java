package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Kalaimaran on 20-Jul-17.
 */

public class MobileCursorAdapter extends CursorAdapter {



    public MobileCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);
        final TextView stockTextView = (TextView) view.findViewById(R.id.product_stock);

        int nameColumnIndex = cursor.getColumnIndex(MobileContract.MobileEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(MobileContract.MobileEntry.COLUMN_PRICE);
        int stockColumnIndex = cursor.getColumnIndex(MobileContract.MobileEntry.COLUMN_STOCK);

        String mobileName = cursor.getString(nameColumnIndex);
        int priceValue = cursor.getInt(priceColumnIndex);
        final int stockValue = cursor.getInt(stockColumnIndex);

        nameTextView.setText(mobileName);
        priceTextView.setText("£ "+priceValue);
        stockTextView.setText(stockValue + "");

        final Uri uri = ContentUris.withAppendedId(MobileContract.MobileEntry.CONTENT_URI, cursor.getInt(cursor.getColumnIndexOrThrow(MobileContract.MobileEntry._ID)));

        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stockValue > 0) {
                    int stockValueLocal = stockValue;
                    stockValueLocal--;
                    stockTextView.setText(stockValueLocal + "");
                    ContentValues values = new ContentValues();
                    values.put(MobileContract.MobileEntry.COLUMN_STOCK, stockValueLocal);
                    context.getContentResolver().update(uri, values, null, null);

                }

            }
        });

    }
}
