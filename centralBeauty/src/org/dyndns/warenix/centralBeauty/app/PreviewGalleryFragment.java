package org.dyndns.warenix.centralBeauty.app;

import org.dyndns.warenix.centralBeauty.R;
import org.dyndns.warenix.centralBeauty.adapter.CentralBeautyCursorAdapter;
import org.dyndns.warenix.centralBeauty.provider.CentralBeautyMetaData;
import org.dyndns.warenix.image.CachedWebImage;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PreviewGalleryFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "PreviewGalleryFragment";

	private static final int LOADER_ALL_BEAUTY = 1;

	private PreviewGalleryListener mPreviewGalleryListener;

	private CentralBeautyCursorAdapter adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");

		CachedWebImage.setCacheDir("centralBeauty");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		final View view = inflater.inflate(R.layout.preview_gallery, container,
				false);

		getLoaderManager().initLoader(LOADER_ALL_BEAUTY, null, this);

		GridView gridview = (GridView) view.findViewById(R.id.gridview);

		adapter = new CentralBeautyCursorAdapter(getActivity(), null, true);
		adapter.setAdapterView(gridview);
		gridview.setAdapter(adapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (mPreviewGalleryListener != null) {
					mPreviewGalleryListener.onPreviewSelected(position);
				}
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		// connect existing loader if available
		LoaderManager lm = getLoaderManager();
		if (lm.getLoader(LOADER_ALL_BEAUTY) != null) {
			lm.initLoader(LOADER_ALL_BEAUTY, null, this);
		}
	}

	public void setListener(PreviewGalleryListener listener) {
		mPreviewGalleryListener = listener;
	}

	public void refresh() {
	}

	public Cursor getCursorItem(int position) {
		return (Cursor) adapter.getItem(position);
	}

	public interface PreviewGalleryListener {
		public void onPreviewSelected(int position);
		public void onPreviewLoaded();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.i(TAG, "onCreateLoader");
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				CentralBeautyMetaData.URI_LIST_ALL, // Contact
				// URI
				CentralBeautyMetaData.MATRIX_CURSOR_COLUMNS, // Which
				// columns
				// to
				// return
				null, // Which rows to return
				null, // Where clause parameters
				null // Order by clause
		);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.i(TAG, "onLoadFinished");
		adapter.swapCursor(cursor);
		
		if (mPreviewGalleryListener != null){
			mPreviewGalleryListener.onPreviewLoaded();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.i(TAG, "onLoadReset");
		adapter.swapCursor(null);
	}
}
