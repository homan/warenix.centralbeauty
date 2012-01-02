package org.dyndns.warenix.centralBeauty.app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.dyndns.warenix.centralBeauty.CentralBeauty;
import org.dyndns.warenix.centralBeauty.CentralBeautyFragment;
import org.dyndns.warenix.centralBeauty.CentralBeautyMaster;
import org.dyndns.warenix.centralBeauty.DateUtil;
import org.dyndns.warenix.centralBeauty.R;
import org.dyndns.warenix.centralBeauty.parser.AppleDailyParser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v4.view.Window;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class CentralBeautyActivity extends FragmentActivity {
	MyAdapter mAdapter;
	ViewPager mPager;
	int currentPage = 0;
	static ArrayList<CentralBeauty> centralBeautyList;

	// static int NUM_ITEMS = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_ITEM_TEXT
				| Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork() // or
		// // .detectAll()
		// // for
		// // all
		// // detectable
		// // problems
		// .penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().penaltyLog()
		// .penaltyDeath().build());

		new FetchCentralBeautyOfThisWeekAsyncTask().execute();

		new UpdateTodayCentralBeautyAsyncTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem downloadItem = menu.add(" ").setIcon(R.drawable.download_icon);
		downloadItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		downloadItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (centralBeautyList == null) {
					return false;
				}

				new DownloadImageAsyncTask().execute();

				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	public static class MyAdapter extends FragmentPagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			if (centralBeautyList == null) {
				return 0;
			}
			return centralBeautyList.size();
		}

		@Override
		public Fragment getItem(int position) {
			return CentralBeautyFragment.newInstance(centralBeautyList
					.get(position));
		}
	}

	class FetchCentralBeautyOfThisWeekAsyncTask extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			Date monday = DateUtil.getMondayOfThisWeek();
			Log.d("centralbeauty", "" + DateUtil.dateToNum(monday));

			try {
				centralBeautyList = new CentralBeautyMaster()
						.getCentralBeautyOfThisWeek();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			mAdapter = new MyAdapter(getSupportFragmentManager());

			mPager = (ViewPager) findViewById(R.id.pager);
			mPager.setAdapter(mAdapter);
			mPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					setTitle(getResources().getString(R.string.app_name)
							+ " - " + centralBeautyList.get(position).num);
					currentPage = position;
				}
			});
		}
	}

	static class UpdateTodayCentralBeautyAsyncTask extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "http://hkm.appledaily.com/list.php?category_guid=15307&category=weekly";

			try {
				CentralBeauty centralBeauty = new AppleDailyParser().parse(url);
				if (centralBeauty != null) {
					Log.d("test", "extracted today num:" + centralBeauty.num);

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
			return null;
		}

	}// configure the notification

	class DownloadImageAsyncTask extends AsyncTask<Void, Integer, Void> {

		int notificationId;
		Notification notification;
		String full_local_file_path;

		protected void onProgressUpdate(Integer... progress) {
			if (progress[0] == 100) {
				notification.contentView.setTextViewText(R.id.status_text,
						"Tap me to view " + full_local_file_path);
				notification.contentView.setViewVisibility(
						R.id.status_progress, View.GONE);
			} else {
				notification.contentView.setProgressBar(R.id.status_progress,
						100, progress[0], false);
			}
			notificationManager.notify(notificationId, notification);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			CentralBeauty centralBeauty = centralBeautyList.get(currentPage);

			if (centralBeauty != null) {
				// Create one directory
				String saveInDir = "centralbeauty";
				String sdDrive = Environment.getExternalStorageDirectory()
						.getAbsolutePath();

				String fullLocalDirPath = String.format("%s/%s", sdDrive,
						saveInDir);
				Log.d("warenix", "saved in " + fullLocalDirPath);
				boolean success = (new File(fullLocalDirPath)).mkdirs();
				if (success) {
					Log.i("warenix",
							String.format("created dir[%s]", fullLocalDirPath));
				}

				String saveAsFilename = centralBeauty.num + ".jpg";
				full_local_file_path = String.format("%s/%s", fullLocalDirPath,
						saveAsFilename);

				// configure the notification
				notificationId = getNextNotiifcationId();
				notification = new Notification(R.drawable.photo_icon,
						"Downloading beauty", System.currentTimeMillis());
				showProgressBar(notification, 0, full_local_file_path);

				// downloadToSDCard
				int IO_BUFFER_SIZE = 512;

				URL url;
				try {
					url = new URL(centralBeauty.previewImageUrlLarge);

					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					int length = connection.getContentLength();
					InputStream is = (InputStream) url.getContent();
					byte[] imageData = new byte[length];

					// Read in the bytes
					int offset = 0;
					int numRead = 0;

					while (offset < imageData.length
							&& (numRead = is.read(imageData, offset,
									imageData.length - offset)) >= 0) {
						offset += numRead;
						int percentage = (int) (Math.ceil(100 * offset
								/ (double) imageData.length));
						publishProgress(percentage);
					}

					BufferedOutputStream out = null;
					FileOutputStream fos = new FileOutputStream(
							full_local_file_path);
					BufferedOutputStream bfs = new BufferedOutputStream(fos,
							IO_BUFFER_SIZE);

					bfs.write(imageData, 0, imageData.length);
					bfs.flush();
					bfs.close();

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}

		public void onPostExecute(Void v) {
			Toast.makeText(CentralBeautyActivity.this, "Download completed",
					Toast.LENGTH_SHORT).show();
		}

	}

	NotificationManager notificationManager;

	void showProgressBar(Notification notification, int progress,
			String full_local_file_path) {
		// configure the intent
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(full_local_file_path)),
				"image/png");
		final PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, 0);

		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
		notification.contentView = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.download_progress);
		notification.contentIntent = pendingIntent;
		notification.contentView.setImageViewResource(R.id.status_icon,
				R.drawable.photo_icon);
		notification.contentView.setTextViewText(R.id.status_text,
				"Download to " + full_local_file_path);
		notification.contentView.setProgressBar(R.id.status_progress, 100, 0,
				false);

		if (notificationManager == null) {
			notificationManager = (NotificationManager) getApplicationContext()
					.getSystemService(
							getApplicationContext().NOTIFICATION_SERVICE);
		}
	}

	static int nextId = 123;

	int getNextNotiifcationId() {
		return nextId++;
	}
}