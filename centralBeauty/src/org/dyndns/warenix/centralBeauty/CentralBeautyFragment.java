package org.dyndns.warenix.centralBeauty;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.dyndns.warenix.util.TouchUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CentralBeautyFragment extends Fragment {
	CentralBeauty centralBeauty;
	int mNum;

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	public static CentralBeautyFragment newInstance(int num) {
		CentralBeautyFragment f = new CentralBeautyFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	public static CentralBeautyFragment newInstance(CentralBeauty centralBeauty) {
		CentralBeautyFragment f = new CentralBeautyFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putSerializable("centralBeauty", centralBeauty);
		f.setArguments(args);

		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNum = getArguments() != null ? getArguments().getInt("num") : 1;
		centralBeauty = getArguments() != null ? (CentralBeauty) getArguments()
				.get("centralBeauty") : null;
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.central_beauty, container, false);

		// new LoadTodayBeautyAsyncTask(v).execute();

		String url = "http://hkm.appledaily.com/list.php?category_guid=15307&category=weekly";
		new DownloadImageTask(v, mNum, centralBeauty, getActivity())
				.execute(url);

		// try {
		// CentralBeauty centralBeauty = new AppleDailyParser().parse(url);
		//
		// displayCentralBeauty(v, centralBeauty);
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return v;
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
		}

		// This class definition states that DownloadImageTask will take String
		// parameters, publish Integer progress updates, and return a Bitmap
		protected Bitmap doInBackground(String... paths) {

			URL url;
			try {
				// centralBeauty = new AppleDailyParser().parse(paths[0]);

				url = new URL(centralBeauty.previewImageUrlLarge);

				// url = new URL(imageList[mNum]);

				Bitmap result = null;

				// while (result == null) {
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				// The String parameter represents the URL of the file you
				// are
				// downloading, you could also just use URL for the Params
				// type
				int length = connection.getContentLength();
				InputStream is = (InputStream) url.getContent();
				byte[] imageData = new byte[length];
				int buffersize = (int) Math.ceil(length / (double) SIZE);
				// int downloaded = 0;
				// for (int i = 1; i < SIZE; i++) {
				// // This for loop splits the downloading into SIZE
				// // increments, so
				// // publishProgress() will be called SIZE times at equal
				// // intervals in the download
				// int read = is.read(imageData, downloaded, buffersize);
				// downloaded += read;
				// publishProgress(i);
				// // publishProgress() is called with the for loop
				// // counter, so
				// // (i
				// // / SIZE) represents the percentage completed
				// }
				// is.read(imageData, downloaded, length - downloaded);
				// publishProgress(SIZE);

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
				// When finished, return the resulting Bitmap, this will cause
				// the
				// Activity to call onPostExecute()
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			// You can make a much prettier load bar using a SurfaceView and
			// drawing progress or creating drawable resources and using an
			// ImageView
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
				// m_vwImage.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				//
				// Intent intent = new Intent(activity,
				// ImageViewActivity.class);
				// intent.putExtra(ImageViewActivity.BUNDLE_URL,
				// centralBeauty.previewImageUrlLarge);
				// activity.startActivity(intent);
				// }
				// });
			}
			description.setText(Html.fromHtml(centralBeauty.description));
			// Hide the load bar, show the image, and set the image display to
			// the resulting Bitmap
		}
	}

}
