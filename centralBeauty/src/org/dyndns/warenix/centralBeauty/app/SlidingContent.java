package org.dyndns.warenix.centralBeauty.app;

import org.dyndns.warenix.centralBeauty.R;
import org.dyndns.warenix.photo.BaseActivity;

import android.os.Bundle;

public class SlidingContent extends BaseActivity {

	ImageFragment mImageFragment;

	public SlidingContent() {
		super(R.string.app_name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the Above View
		setContentView(R.layout.content_frame);
		mImageFragment = new ImageFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mImageFragment, "content_frame")
				.commit();

		setSlidingActionBarEnabled(true);
	}

}
