package org.dyndns.warenix.centralBeauty;

import java.io.Serializable;

import org.dyndns.warenix.centralBeauty.provider.CentralBeautyMetaData;

import android.database.Cursor;

@SuppressWarnings("serial")
public class CentralBeauty implements Serializable {

	public int num = 0;
	public String fullPageUrl = "";
	public String previewImageUrl = "";
	public String previewImageUrlLarge = "";
	public String description = "";

	public CentralBeauty(int num, String fullPageUrl, String previewImageUrl,
			String previewImageUrlLarege, String description) {
		this.num = num;
		this.fullPageUrl = fullPageUrl;
		this.previewImageUrl = previewImageUrl;
		this.previewImageUrlLarge = previewImageUrlLarege;
		this.description = description;
	}

	public static CentralBeauty newInstance(Cursor cursor) {

		int num = cursor.getInt(cursor
				.getColumnIndex(CentralBeautyMetaData.BaseColumns.num));
		String fullPageUrl = cursor.getString(cursor
				.getColumnIndex(CentralBeautyMetaData.BaseColumns.fullPageUrl));
		String previewImageUrl = cursor
				.getString(cursor
						.getColumnIndex(CentralBeautyMetaData.BaseColumns.previewImageUrl));
		String previewImageUrlLarege = cursor
				.getString(cursor
						.getColumnIndex(CentralBeautyMetaData.BaseColumns.previewImageUrlLarge));
		String description = cursor.getString(cursor
				.getColumnIndex(CentralBeautyMetaData.BaseColumns.description));

		CentralBeauty centralBeauty = new CentralBeauty(num, fullPageUrl,
				previewImageUrl, previewImageUrlLarege, description);
		return centralBeauty;
	}

}
