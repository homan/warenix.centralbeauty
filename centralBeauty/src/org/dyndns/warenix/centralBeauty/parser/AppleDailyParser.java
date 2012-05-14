package org.dyndns.warenix.centralBeauty.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.dyndns.warenix.centralBeauty.CentralBeauty;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.Utils;

import android.net.Uri;

public class AppleDailyParser {

	public static final String LOG_TAG = AppleDailyParser.class.getSimpleName();

	String fullPageUrl = "";
	String previewImageUrl = "";
	String previewImageUrlLarege = "";
	String description = "";

	public CentralBeauty parse(final String url) throws MalformedURLException,
			IOException {

		parseCentralBeautyPage(url);

		if (found) {

			parseDescription(fullPageUrl);

			Uri uri = Uri.parse(previewImageUrlLarege);
			List<String> segments = uri.getPathSegments();
			int num = Integer.parseInt(segments.get(segments.size() - 3));

			return new CentralBeauty(num, fullPageUrl, previewImageUrl,
					previewImageUrlLarege, description);
		}
		return null;
	}

	boolean found = false;

	void parseCentralBeautyPage(final String url) throws MalformedURLException,
			IOException {
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();

		TagNode node = cleaner.clean(new URL(url));

		// traverse whole DOM and update images to absolute URLs
		node.traverse(new TagNodeVisitor() {
			public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
				if (htmlNode instanceof TagNode) {
					TagNode tag = (TagNode) htmlNode;
					String tagName = tag.getName();
					if ("a".equals(tagName)) {
						String href = tag.getAttributeByName("href");
						if (href != null) {
							fullPageUrl = Utils.fullUrl(url, href);
							// Log.d(this.getClass().getName(), fullPageUrl);
						}
					}
					if ("img".equals(tagName)) {
						String src = tag.getAttributeByName("src");
						if (src != null) {
							previewImageUrl = Utils.fullUrl(url, src);
							// replace 92px with original image
							previewImageUrlLarege = previewImageUrl.replace(
									"92pix", "large");
							tag.setAttribute("src", Utils.fullUrl(url, src));
							// Log.d(LOG_TAG, previewImageUrl);
						}
					}
					if ("p".equals(tagName)) {
						String text = tag.getText().toString();
						if (text.equals("中環我至靚") || text.equals("中環我最靚")) {
							found = true;
							return false;
						}
					}
				}
				// tells visitor to continue traversing the DOM tree
				return true;
			}
		});

	}

	void parseDescription(final String url) throws MalformedURLException,
			IOException {
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();

		TagNode node = cleaner.clean(new URL(url));

		// traverse whole DOM and update images to absolute URLs
		node.traverse(new TagNodeVisitor() {
			public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
				if (htmlNode instanceof TagNode) {
					TagNode tag = (TagNode) htmlNode;
					String tagName = tag.getName();

					if ("p".equals(tagName)) {

						String text = tag.getText().toString()
								.replaceAll("[ \\r\\n]", "");
						if (text.length() > 0) {
							description = text;
							return false;
						}
					}
				}
				// tells visitor to continue traversing the DOM tree
				return true;
			}
		});

	}

}
