package org.dyndns.warenix.centralBeauty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dyndns.warenix.centralBeauty.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CentralBeautyMaster {

	public static final String LOG_TAG = "CentralBeautyMaster";

	final String SERVER_BASE = "http://4.inviteyouall.appspot.com";

	public String apiCall(String url) throws ClientProtocolException,
			IOException {
		Log.d(LOG_TAG, "api call: " + url);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuffer sb = new StringBuffer("");
		String line = "";
		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		in.close();
		String result = sb.toString();
		return result;

	}

	public ArrayList<CentralBeauty> getCentralBeautyOfThisWeek()
			throws ClientProtocolException, IOException {
		String path = SERVER_BASE + "/centralbeauty/list/?fromNum=%d&toNum=%d";
		Date monday = DateUtil.getMondayOfThisWeek();
		Date today = DateUtil.getToday();
		String url = String.format(path, DateUtil.dateToNum(monday),
				DateUtil.dateToNum(today));

		String response = apiCall(url);
		Log.d(LOG_TAG, "response:" + response);

		ArrayList<CentralBeauty> result = null;
		try {
			JSONObject json = new JSONObject(response);

			JSONArray centralBeautyList = json
					.getJSONArray("centralBeautyList");
			Log.d(LOG_TAG, "list count:" + centralBeautyList.length());

			if (centralBeautyList.length() > 0) {
				result = new ArrayList<CentralBeauty>();
				for (int i = 0; i < centralBeautyList.length(); ++i) {
					String s = centralBeautyList.getString(i);
					JSONObject centralBeautyJSON = new JSONObject(s);

					int num = centralBeautyJSON.getInt("num");
					// String fullPageUrl = centralBeautyJSON
					// .getString("fullPageUrl");
					String previewImageUrl = centralBeautyJSON
							.getString("previewImage");
					String previewImageUrlLarege = centralBeautyJSON
							.getString("previewImageLarge");
					String description = centralBeautyJSON
							.getString("description");

					CentralBeauty centralBeauty = new CentralBeauty(num, null,
							previewImageUrl, previewImageUrlLarege, description);
					result.add(centralBeauty);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	public CentralBeauty getCentralBeautyOfDate(int num) {
		String path = SERVER_BASE + "/centralbeauty/get/?num=%d";
		String url = String.format(path, num);

		String response;
		try {
			response = apiCall(url);
			Log.d(LOG_TAG, "response:" + response);

			if (!response.equals("")) {
				JSONObject json = new JSONObject(response);
				String previewImageUrl = json.getString("previewImage");
				String previewImageUrlLarege = json
						.getString("previewImageLarge");
				String description = json.getString("description");
				int n = json.getInt("num");

				return new CentralBeauty(n, "", previewImageUrl,
						previewImageUrlLarege, description);
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void update(CentralBeauty centralBeauty) {
		String path = SERVER_BASE
				+ "/centralbeauty/put/?previewImage=%s&previewImageLarge=%s&description=%s&num=%d";
		try {
			String url = String.format(path, URLEncoder.encode(
					centralBeauty.previewImageUrl, "utf-8"), URLEncoder.encode(
					centralBeauty.previewImageUrlLarge, "utf-8"), URLEncoder
					.encode(centralBeauty.description, "utf-8"),
					centralBeauty.num, "utf-8");

			String response;

			response = apiCall(url);
			Log.d(LOG_TAG, "response:" + response);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
