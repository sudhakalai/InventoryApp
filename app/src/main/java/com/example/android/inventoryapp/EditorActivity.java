package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
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

    //Global variable declaration
    int stock;

    private boolean mMobileHasChanged = false;

    public static final int GET_FROM_GALLERY = 3;

    private Uri imageURI;

    private String supplierPhone;

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    //Setting onTouchListener

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

        //intialising the text views
        mOrderMoreButton = (Button) findViewById(R.id.order);
        mPlusButton = (Button) findViewById(R.id.plus_button);
        mMinusButton  = (Button) findViewById(R.id.minus_button);
        mNameEditText = (EditText) findViewById(R.id.product_name_edit);
        mPriceEditText = (EditText) findViewById(R.id.product_price_edit);
        mStockEditText = (EditText) findViewById(R.id.product_stock_edit);
        mSupplierNameEditText = (EditText) findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.supplier_website);
        mAddImageButton = (Button) findViewById(R.id.add_image);
        mImageView = (ImageView) findViewById(R.id.image);

        //Getting Uri from the intent from MainActivity
        Intent intent = getIntent();
        mCurrentMobileUri = intent.getData();

        //Reusing the same editor activity for Adding and editing a mobile
        if(mCurrentMobileUri == null){
            setTitle(getString(R.string.add_mobile));
            mOrderMoreButton.setVisibility(View.GONE);
            mPlusButton.setVisibility(View.GONE);
            mMinusButton.setVisibility(View.GONE);
        }else{
            setTitle(getString(R.string.edit_mobile));
            mAddImageButton.setVisibility(View.GONE);
            getLoaderManager().initLoader(EXISTING_MOBILE_LOADER, null, this);
        }

        //Attaching onTouchListeners to textviews
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mStockEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        //Setting onClickLister to Order More button
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

        //Setting onClickLister to Add image button
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
            mImageView.setImageURI(null);
            mImageView.setImageURI(imageURI);

        }
    }

    //This method save mobile info
    private void saveMobile(){

        //Getting text from text views
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

        //Attaching the values to ContentValues

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

            String photoString = imageURI.toString();
            values.put(MobileEntry.COLUMN_IMAGE, photoString);

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
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                } else {
                    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // User clicked "Discard" button , navigate to parent activity
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };
                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }

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

            //Getting Column index from cursor
            int nameColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_NAME);
            int priceColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_PRICE);
            int stockColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_STOCK);
            int supplierNameColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_SUPPLIER_PHONE);
            int imageColumnIndex = data.getColumnIndex(MobileEntry.COLUMN_IMAGE);

            //Extracting values from cursor
            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            stock = data.getInt(stockColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            supplierPhone = data.getString(supplierPhoneColumnIndex);
            String image = data.getString(imageColumnIndex);
            Uri imageUri = Uri.parse(image);

            //Increase stock
            mPlusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stock ++;
                    mStockEditText.setText(stock+"");
                }
            });

            //Decrease stock
            mMinusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(stock > 0){
                        stock --;
                        mStockEditText.setText(stock+"");
                    }
                }
            });

            //Setting Text
            mNameEditText.setText(name);
            mPriceEditText.setText(price+"");
            mStockEditText.setText(stock+"");
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);
            mImageView.setImageURI(null);

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
            mImageView.setImageURI(imageUri);


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                   Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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
