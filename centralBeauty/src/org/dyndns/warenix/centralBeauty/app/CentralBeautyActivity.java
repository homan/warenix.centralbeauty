package org.dyndns.warenix.centralBeauty.app;

import java.io.IOException;
import java.net.MalformedURLException;

import org.dyndns.warenix.centralBeauty.CentralBeauty;
import org.dyndns.warenix.centralBeauty.CentralBeautyMaster;
import org.dyndns.warenix.centralBeauty.R;
import org.dyndns.warenix.centralBeauty.app.PreviewGalleryFragment.PreviewGalleryListener;
import org.dyndns.warenix.centralBeauty.parser.AppleDailyParser;
import org.dyndns.warenix.centralBeauty.provider.CentralBeautyMetaData;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.actionbarcompat.ActionBarActivity;

public class CentralBeautyActivity extends ActionBarActivity implements
		PreviewGalleryListener {
	private static final String TAG = "TabletAppActivity";

	private PreviewGalleryFragment mPreviewGalleryFragment;
	private ImageFragment mImageFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mPreviewGalleryFragment = (PreviewGalleryFragment) getSupportFragmentManager()
				.findFragmentById(R.id.preview_gallery);
		if (mPreviewGalleryFragment != null) {
			mPreviewGalleryFragment.setListener(this);
		}

		mImageFragment = (ImageFragment) getSupportFragmentManager()
				.findFragmentById(R.id.image_fragment);

		updateTodayBeauty();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actionbar_download:
			CentralBeauty centralBeauty = mImageFragment
					.getCurrentlyDisplayedCentralBeauty();
			if (centralBeauty != null) {
				DownloadNotification notification = new DownloadNotification();
				notification.startDownloadNotification(this, centralBeauty);
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPreviewSelected(int position) {
		Cursor item = mPreviewGalleryFragment.getCursorItem(position);
		String url = item
				.getString(item
						.getColumnIndex(CentralBeautyMetaData.BaseColumns.previewImageUrlLarge));
		Log.d(TAG, url);

		Bundle b = new Bundle();
		b.putString(ImageFragment.BUNDLE_IMAGE_URL, url);
		b.putSerializable(ImageFragment.BUNDLE_CENTRAL_BEAUTY,
				CentralBeauty.newInstance(item));
		mImageFragment.loadImage(b);
	}

	void updateTodayBeauty() {

		new Thread() {
			public void run() {
				String url = "http://hkm.appledaily.com/list.php?category_guid=15307&category=weekly";

				try {
					CentralBeauty centralBeauty = new AppleDailyParser()
							.parse(url);
					if (centralBeauty != null) {
						Log.d("test", "extracted today num:"
								+ centralBeauty.num);

						CentralBeautyMaster master = new CentralBeautyMaster();
						CentralBeauty todayBeauty = master
								.getCentralBeautyOfDate(centralBeauty.num);
						if (todayBeauty == null) {
							// update
							master.update(centralBeauty);
						}
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onPreviewLoaded() {
		findViewById(R.id.loading).setVisibility(View.GONE);

		// load first image
		onPreviewSelected(0);
	}
}