package com.example.android.inventoryapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sudha on 18-Jul-17.
 */

public class MobileContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOBILE = "mobile";

    public static final class MobileEntry implements BaseColumns{

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of mobiles.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOBILE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single mobile.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOBILE;

        public static String _ID = BaseColumns._ID;
        public static String COLUMN_NAME = "name";
        public static String COLUMN_PRICE = "price";
        public static String COLUMN_STOCK = "stock";
        public static String COLUMN_SUPPLIER_NAME ="supplier_name";
        public static String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static String COLUMN_IMAGE = "image";

        public static String TABLE_NAME = "mobile";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOBILE);
    }
}
