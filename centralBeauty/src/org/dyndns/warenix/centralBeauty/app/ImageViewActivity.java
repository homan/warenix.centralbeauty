package org.dyndns.warenix.centralBeauty.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.dyndns.warenix.centralBeauty.R;
import org.dyndns.warenix.util.TouchUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewActivity extends Activity {

	public static String BUNDLE_BITMAP = "bitmap";
	public static String BUNDLE_URL = "url";

	ImageView image;

	Bitmap bitmap;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view);
		String url = getIntent().getStringExtra(BUNDLE_URL);

		image = (ImageView) findViewById(R.id.image);
		try {
			Bitmap bitmap = downloadImage(new URL(url));
			image.setImageBitmap(bitmap);
			TouchUtil.setImageViewPinchToZoom(image);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	Bitmap downloadImage(URL url) {
		int SIZE = 10;
		try {

			Bitmap result = null;

			// while (result == null) {
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
					&& (numRead = is.read(imageData, offset, imageData.length
							- offset)) >= 0) {
				offset += numRead;
				int percentage = (int) (Math.ceil(offset
						/ (double) imageData.length));
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

}
