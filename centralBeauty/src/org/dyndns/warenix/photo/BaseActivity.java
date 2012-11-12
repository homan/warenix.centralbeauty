package org.dyndns.warenix.photo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.dyndns.warenix.centralBeauty.CentralBeauty;
import org.dyndns.warenix.centralBeauty.CentralBeautyMaster;
import org.dyndns.warenix.centralBeauty.R;
import org.dyndns.warenix.centralBeauty.app.ImageFragment;
import org.dyndns.warenix.centralBeauty.app.PreviewGalleryFragment;
import org.dyndns.warenix.centralBeauty.app.PreviewGalleryFragment.PreviewGalleryListener;
import org.dyndns.warenix.centralBeauty.parser.AppleDailyParser;
import org.dyndns.warenix.centralBeauty.provider.CentralBeautyMetaData;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity implements
		PreviewGalleryListener {

	private int mTitleRes;
	protected Fragment mFrag;

	public BaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(mTitleRes);

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		mFrag = new PreviewGalleryFragment();
		((PreviewGalleryFragment) mFrag).setListener(this);
		t.replace(R.id.menu_frame, mFrag);
		t.commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.actionbar_home_width);

		// customize the ActionBar
		if (Build.VERSION.SDK_INT >= 11) {
			if (getActionBar() != null) {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}

		updateTodayBeauty();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class PagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> mFragments = new ArrayList<Fragment>();
		private ViewPager mPager;

		public PagerAdapter(FragmentManager fm, ViewPager vp) {
			super(fm);
			mPager = vp;
			mPager.setAdapter(this);
		}

		public void addTab(Fragment frag) {
			mFragments.add(frag);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}
	}

	@Override
	public void onPreviewSelected(int position) {
		Cursor item = ((PreviewGalleryFragment) mFrag).getCursorItem(position);
		if (item != null) {
			String url = item
					.getString(item
							.getColumnIndex(CentralBeautyMetaData.BaseColumns.previewImageUrlLarge));
			Bundle b = new Bundle();
			b.putString(ImageFragment.BUNDLE_IMAGE_URL, url);
			b.putSerializable(ImageFragment.BUNDLE_CENTRAL_BEAUTY,
					CentralBeauty.newInstance(item));
			((ImageFragment) getSupportFragmentManager().findFragmentByTag(
					"content_frame")).loadImage(b);

		}
	}

	@Override
	public void onPreviewLoaded() {
		onPreviewSelected(0);
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
}
