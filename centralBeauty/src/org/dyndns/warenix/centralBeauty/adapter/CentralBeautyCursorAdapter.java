package org.dyndns.warenix.centralBeauty.adapter;

import org.dyndns.warenix.centralBeauty.R;
import org.dyndns.warenix.centralBeauty.provider.CentralBeautyMetaData;
import org.dyndns.warenix.image.CachedWebImage;
import org.dyndns.warenix.image.WebImage;
import org.dyndns.warenix.image.WebImage.WebImageListener;
import org.dyndns.warenix.util.ImageUtil;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

public class CentralBeautyCursorAdapter extends CursorAdapter {

	static LayoutInflater sInflater;

	private static final String TAG = "CentralBeautyCursorAdapter";

	protected AdapterView<?> mAdapterView;

	public CentralBeautyCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		final int position = cursor.getPosition();

		viewHolder.imageView.setImageBitmap(null);

		WebImage webImageLoader = new CachedWebImage();
		webImageLoader
				.startDownloadImage(
						"",
						cursor.getString(cursor
								.getColumnIndex(CentralBeautyMetaData.BaseColumns.previewImageUrl)),
						viewHolder.imageView, null);
		webImageLoader.setWebImageListener(new WebImageListener() {

			@Override
			public void onImageSet(ImageView image, Bitmap bitmap) {

				int first = mAdapterView.getFirstVisiblePosition();
				int last = mAdapterView.getLastVisiblePosition();
				Log.i(TAG, String.format("current: %d  first:%d  last:%d",
						position, first, last));
				if (position >= first && position <= last) {
					viewHolder.imageView.setImageBitmap(bitmap);
					viewHolder.imageView.setAdjustViewBounds(true);
				} else {
					// clear image
					if (ImageUtil.recycleBitmap(bitmap)) {
						Log.i(TAG, "recycleImage()");
					}

				}

			}

			@Override
			public void onImageSet(ImageView image) {

			}
		});
	}

	static class ViewHolder {
		ImageView imageView;
	}

	@Override
	public View newView(Context context, Cursor arg1, ViewGroup arg2) {
		Log.i(TAG, "newView");
		View view = null;
		if (sInflater == null) {
			sInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		if (view == null) {
			view = sInflater.inflate(R.layout.image, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
			viewHolder.imageView
					.setBackgroundColor(R.drawable.preview_image_selector);
			view.setTag(viewHolder);
		}

		return view;
	}

	public void setAdapterView(AdapterView<?> adapterView) {
		mAdapterView = adapterView;
	}
}
