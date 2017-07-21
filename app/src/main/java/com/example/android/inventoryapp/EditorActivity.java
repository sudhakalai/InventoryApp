package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.MobileContract.MobileEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Sudha on 19-Jul-17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_MOBILE_LOADER = 0;
    private Uri mCurrentMobileUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mStockEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    private Button mAddImageButton;
    private ImageView mImageView;
    private Button mPlusButton;
    private Button mMinusButton;
    private Button mOrderMoreButton;

    int stock;

    private boolean mMobileHasChanged = false;

    public static final int GET_FROM_GALLERY = 3;

    private Uri imageURI;

    private String supplierPhone;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mMobileHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentMobileUri = intent.getData();

        mOrderMoreButton = (Button) findViewById(R.id.order);
        mPlusButton = (Button) findViewById(R.id.plus_button);
        mMinusButton  = (Button) findViewById(R.id.minus_button);

        if(mCurrentMobileUri == null){
            setTitle(getString(R.string.add_mobile));
            mOrderMoreButton.setVisibility(View.GONE);
            mPlusButton.setVisibility(View.GONE);
            mMinusButton.setVisibility(View.GONE);
        }else{
            setTitle(getString(R.string.edit_mobile));
            getLoaderManager().initLoader(EXISTING_MOBILE_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.product_name_edit);
        mPriceEditText = (EditText) findViewById(R.id.product_price_edit);
        mStockEditText = (EditText) findViewById(R.id.product_stock_edit);
        mSupplierNameEditText = (EditText) findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.supplier_website);
        mAddImageButton = (Button) findViewById(R.id.add_image);
        mImageView = (ImageView) findViewById(R.id.image);



        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mStockEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        mOrderMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String phoneNumber = mSupplierPhoneEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivity(intent);
                }

            }
        });


        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            imageURI = data.getData();

        }
    }

    private void saveMobile(){
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String stockString = mStockEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        if(mCurrentMobileUri== null && TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(stockString) &&
                TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneString)){
            return;
        }

        ContentValues values = new ContentValues();

            values.put(MobileEntry.COLUMN_NAME, nameString);

        int price = 0;
        if (!TextUtils.isEmpty(priceString)){
            price = Integer.parseInt(priceString);
        }



        values.put(MobileEntry.COLUMN_PRICE, price);

        if(!TextUtils.isEmpty(stockString)){
            stock = Integer.parseInt(stockString);
        }


        values.put(MobileEntry.COLUMN_STOCK, stock);
        values.put(MobileEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(MobileEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        if (imageURI != null) {

            ByteArrayOutputStream byteOT = new ByteArrayOutputStream();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOT);
            byte[] photo = byteOT.toByteArray();

            values.put(MobileEntry.COLUMN_IMAGE, photo);
        }



        if(mCurrentMobileUri == null){
            Uri newUri = getContentResolver().insert(MobileEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_mobile_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_mobile_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            int rowsAffected = getContentResolver().update(mCurrentMobileUri, values, null, null);

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_mobile_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_mobile_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentMobileUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_save:
                saveMobile();
                finish();
                return true;
            case R.id.home:

                if (!mMobileHasChanged) {
                    finish();
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                finish();
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!mMobileHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[]projection = {
                MobileEntry._ID,
                MobileEntry.COLUMN_NAME,
                MobileEntry.COLUMN_PRICE,
                MobileEntry.COLUMN_STOCK,
                MobileEntry.COLUMN_SUPPLIER_NAME,
                MobileEntry.COLUMN_SUPPLIER_PHONE,
                MobileEntry.COLUMN_IMAGE
        };

        return new CursorLoader(this, mCurrentMobileUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }
        if(data.moveToFirst()){

            int nameColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_NAME);
            int priceColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_PRICE);
            int stockColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_STOCK);
            int supplierNameColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_SUPPLIER_PHONE);
            int imageColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_IMAGE);

            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            stock = data.getInt(stockColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            supplierPhone = data.getString(supplierPhoneColumnIndex);
            byte[] imageArray = data.getBlob(imageColumnIndex);

            mPlusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stock ++;
                    mStockEditText.setText(stock+"");
                }
            });

            mMinusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(stock > 0){
                        stock --;
                        mStockEditText.setText(stock+"");
                    }
                }
            });

            mNameEditText.setText(name);
            mPriceEditText.setText(price+"");
            mStockEditText.setText(stock+"");
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);

            if(imageArray != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
                mImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, mImageView.getWidth(), mImageView.getHeight(), false));
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mStockEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mImageView.setImageBitmap(null);

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteMobile();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMobile() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentMobileUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentMobileUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_mobile_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_mobile_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
