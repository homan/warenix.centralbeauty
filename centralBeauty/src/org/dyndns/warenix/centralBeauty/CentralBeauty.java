package org.dyndns.warenix.centralBeauty;

import java.io.Serializable;

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

}
