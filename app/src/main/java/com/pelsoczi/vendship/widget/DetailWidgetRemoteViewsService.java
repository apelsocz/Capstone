package com.pelsoczi.vendship.widget;


import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pelsoczi.vendship.R;
import com.pelsoczi.vendship.data.VendorContract;

public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    public static final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();

    private static final String[] VENDOR_COLUMNS = VendorContract.VendorEntry.PROJECTION_COLUMNS;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {
                // not interested
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(
                        VendorContract.VendorEntry.CONTENT_URI,
                        VENDOR_COLUMNS,
                        null,
                        null,
                        VendorContract.VendorEntry._ID + " ASC"
                );

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views =
                        new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

                views.setTextViewText(R.id.vendor_name,
                        data.getString(VendorContract.VendorEntry.INDEX_COLUMN_VENDOR_NAME));
                views.setTextViewText(R.id.vendor_category,
                        data.getString(VendorContract.VendorEntry.INDEX_COLUMN_VENDOR_CATEGORIES));

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}