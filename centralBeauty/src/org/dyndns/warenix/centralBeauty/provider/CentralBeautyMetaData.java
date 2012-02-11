package org.dyndns.warenix.centralBeauty.provider;

import android.net.Uri;

public class CentralBeautyMetaData {
	public static final String AUTHORITY = "org.dyndns.warenix.centralBeauty.provider.CentralBeautyProvider";

	// uri
	/**
	 * list all central beauty
	 */
	public static final Uri URI_LIST_ALL = Uri.parse("content://" + AUTHORITY
			+ "/list");
	public static final Uri URI_LIST_ONE = Uri.parse("content://" + AUTHORITY
			+ "/get");

	// content type
	public static final String CONTENT_TYPE_CENTRAL_BEAUTY_LIST = "vnd.android.cursor.dir/vnd.org.dyndns.warenix.centralBeauty";
	public static final String CONTENT_TYPE_CENTRAL_BEAUTY_ONE = "vnd.android.cursor.item/vnd.org.dyndns.warenix.centralBeauty";

	// matrixcursor columns
	public static final String[] MATRIX_CURSOR_COLUMNS = new String[] { "_id",
			"num", "fullPageUrl", "previewImageUrl", "previewImageUrlLarge",
			"description" };

	public static class BaseColumns {
		public static final String ID = "_id";
		public static final String num = "num";
		public static final String fullPageUrl = "fullPageUrl";
		public static final String previewImageUrl = "previewImageUrl";
		public static final String previewImageUrlLarge = "previewImageUrlLarge";
		public static final String description = "description";
	}
}
