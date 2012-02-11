package org.dyndns.warenix.centralBeauty.provider;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.dyndns.warenix.centralBeauty.CentralBeauty;
import org.dyndns.warenix.centralBeauty.CentralBeautyMaster;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class CentralBeautyProvider extends ContentProvider {

	private static final String TAG = "CentralBeautyProvider";

	private static final int TYPE_LIST = 1;
	private static final int TYPE_ONE = 2;

	// Creates a UriMatcher object.
	private static UriMatcher sUriMatcher = null;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(CentralBeautyMetaData.AUTHORITY, "list", TYPE_LIST);
		sUriMatcher.addURI(CentralBeautyMetaData.AUTHORITY, "get", TYPE_ONE);
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case TYPE_LIST:
			return CentralBeautyMetaData.CONTENT_TYPE_CENTRAL_BEAUTY_LIST;
		case TYPE_ONE:
			return CentralBeautyMetaData.CONTENT_TYPE_CENTRAL_BEAUTY_ONE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		return null;
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch (sUriMatcher.match(uri)) {
		case TYPE_LIST:
			return queryAllCentralBeauty(uri, projection, selection,
					selectionArgs, sortOrder);
		case TYPE_ONE:
			return queryOneCentralBeuaty(uri, projection, selection,
					selectionArgs, sortOrder);
		}
		return null;
	}

	private Cursor queryAllCentralBeauty(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {

		MatrixCursor cursor = new MatrixCursor(
				CentralBeautyMetaData.MATRIX_CURSOR_COLUMNS);
		CentralBeautyMaster master = new CentralBeautyMaster();
		ArrayList<CentralBeauty> list;
		try {
			list = master.getCentralBeautyOfThisWeek();
			for (CentralBeauty centralBeauty : list) {
				cursor.addRow(new Object[] { centralBeauty.num,
						centralBeauty.num, centralBeauty.fullPageUrl,
						centralBeauty.previewImageUrl,
						centralBeauty.previewImageUrlLarge,
						centralBeauty.description });
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "queryAllCentralBeauty count:" + cursor.getCount());

		return cursor;
	}

	private Cursor queryOneCentralBeuaty(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		MatrixCursor cursor = new MatrixCursor(
				CentralBeautyMetaData.MATRIX_CURSOR_COLUMNS);
		CentralBeautyMaster master = new CentralBeautyMaster();
		ArrayList<CentralBeauty> list;
		try {
			list = master.getCentralBeautyOfThisWeek();

			CentralBeauty centralBeauty = list.get(0);
			cursor.addRow(new Object[] { centralBeauty.num, centralBeauty.num,
					centralBeauty.fullPageUrl, centralBeauty.previewImageUrl,
					centralBeauty.previewImageUrlLarge,
					centralBeauty.description });

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "queryOneCentralBeuaty count:" + cursor.getCount());
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
