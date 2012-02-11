package org.dyndns.warenix.centralBeauty.app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.dyndns.warenix.centralBeauty.CentralBeauty;
import org.dyndns.warenix.centralBeauty.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DownloadNotification {

	public void startDownloadNotification(Context context,
			CentralBeauty centralBeauty) {

		new DownloadImageAsyncTask(context).execute(centralBeauty);
	}

	class DownloadImageAsyncTask extends
			AsyncTask<CentralBeauty, Integer, Void> {
		Context mContext;
		int notificationId;
		Notification notification;
		String full_local_file_path;

		public DownloadImageAsyncTask(Context context) {
			mContext = context;

		}

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
		protected Void doInBackground(CentralBeauty... args) {

			CentralBeauty centralBeauty = args[0];
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

					FileOutputStream fos = new FileOutputStream(
							full_local_file_path);
					BufferedOutputStream bfs = new BufferedOutputStream(fos,
							IO_BUFFER_SIZE);

					bfs.write(imageData, 0, imageData.length);
					bfs.flush();
					bfs.close();

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		public void onPostExecute(Void v) {
			Toast.makeText(mContext, "Download completed", Toast.LENGTH_SHORT)
					.show();
		}

		NotificationManager notificationManager;

		void showProgressBar(Notification notification, int progress,
				String full_local_file_path) {
			// configure the intent
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(full_local_file_path)),
					"image/png");
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					mContext, 0, intent, 0);

			notification.flags = notification.flags
					| Notification.FLAG_AUTO_CANCEL;
			notification.contentView = new RemoteViews(
					mContext.getPackageName(), R.layout.download_progress);
			notification.contentIntent = pendingIntent;
			notification.contentView.setImageViewResource(R.id.status_icon,
					R.drawable.photo_icon);
			notification.contentView.setTextViewText(R.id.status_text,
					"Download to " + full_local_file_path);
			notification.contentView.setProgressBar(R.id.status_progress, 100,
					0, false);

			if (notificationManager == null) {
				notificationManager = (NotificationManager) mContext
						.getSystemService(Context.NOTIFICATION_SERVICE);
			}
		}

		int getNextNotiifcationId() {
			return nextId++;
		}

	}

	static int nextId = 123;

}