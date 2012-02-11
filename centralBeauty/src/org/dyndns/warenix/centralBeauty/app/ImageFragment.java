package org.dyndns.warenix.centralBeauty.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.dyndns.warenix.centralBeauty.CentralBeauty;
import org.dyndns.warenix.centralBeauty.R;
import org.dyndns.warenix.util.TouchUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageFragment extends Fragment {
	/**
	 * show this image
	 */
	public static final String BUNDLE_IMAGE_URL = "image_url";
	public static final String BUNDLE_CENTRAL_BEAUTY = "central_beauty";

	// currently display
	CentralBeauty mCentralBeauty;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.central_beauty, container, false);
		return view;
	}

	public void loadImage(Bundle b) {
		CentralBeauty centralBeauty = (CentralBeauty) b
				.getSerializable(BUNDLE_CENTRAL_BEAUTY);

		mCentralBeauty = centralBeauty;

		new DownloadImageTask(getView(), centralBeauty.num, centralBeauty,
				getActivity()).execute(centralBeauty.previewImageUrlLarge);
	}

	public CentralBeauty getCurrentlyDisplayedCentralBeauty() {
		return mCentralBeauty;
	}

	private static class DownloadImageTask extends
			AsyncTask<String, Integer, Bitmap> {

		ImageView m_vwImage;
		TextView m_vwLoad;
		TextView description;
		int mNum;

		CentralBeauty centralBeauty;
		Activity activity;
		final int SIZE = 20;

		public DownloadImageTask(final View v, int n,
				CentralBeauty centralBeauty, Activity activity) {
			m_vwImage = (ImageView) v.findViewById(R.id.image);
			m_vwLoad = (TextView) v.findViewById(R.id.progress);
			description = (TextView) v.findViewById(R.id.description);

			mNum = n;
			this.centralBeauty = centralBeauty;
			this.activity = activity;
		}

		protected void onPreExecute() {
			activity.setProgressBarVisibility(true);
			activity.setProgressBarIndeterminateVisibility(Boolean.TRUE);
			m_vwLoad.setVisibility(View.VISIBLE);
			onProgressUpdate(new Integer[] { 0 });
		}

		// This class definition states that DownloadImageTask will take String
		// parameters, publish Integer progress updates, and return a Bitmap
		protected Bitmap doInBackground(String... paths) {

			URL url;
			try {
				url = new URL(centralBeauty.previewImageUrlLarge);

				Bitmap result = null;

				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				int length = connection.getContentLength();
				InputStream is = (InputStream) url.getContent();
				byte[] imageData = new byte[length];
				int buffersize = (int) Math.ceil(length / (double) SIZE);

				// Read in the bytes
				int offset = 0;
				int numRead = 0;
				while (offset < imageData.length
						&& (numRead = is.read(imageData, offset,
								imageData.length - offset)) >= 0) {
					offset += numRead;
					int percentage = (int) (Math.ceil(SIZE * offset
							/ (double) imageData.length));
					publishProgress(percentage);
				}

				result = BitmapFactory.decodeByteArray(imageData, 0, length);
				connection.disconnect();
				// }
				return result;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			// This is a very simple load bar, with SIZE = 10, on step 5 this
			// would display: [===== ]
			String text = "Downloading\n[";
			for (int i = 0; i < progress[0]; i++) {
				text += '=';
			}
			for (int i = SIZE; i > progress[0]; i--) {
				text += "  ";
			}
			text += "]";
			m_vwLoad.setText(text);
		}

		protected void onPostExecute(final Bitmap result) {
			activity.setProgressBarIndeterminateVisibility(Boolean.FALSE);
			m_vwLoad.setVisibility(View.GONE);
			m_vwImage.setVisibility(View.VISIBLE);
			if (result == null) {
				m_vwImage.setImageResource(R.drawable.photo_icon);
			} else {
				m_vwImage.setImageBitmap(result);
				TouchUtil.setImageViewPinchToZoom(m_vwImage);
			}
			description.setText(Html.fromHtml(centralBeauty.description));
		}
	}

}
