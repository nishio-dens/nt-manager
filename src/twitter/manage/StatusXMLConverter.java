package twitter.manage;

import java.io.StringReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import twitter.util.HTMLEncode;
import twitter4j.Status;
import twitter4j.User;

/**
 * StatusをXMLに変換する
 * 
 * @author nishio
 * 
 */
public class StatusXMLConverter {

	// STATUS XML TAG
	private static final String ROOT_TAG = "root";
	private static final String TWEET_TAG = "tweet";
	private static final String DATE_TAG = "date";
	private static final String LOCATION_TAG = "location";
	private static final String ID_TAG = "id";
	private static final String CONTRIBUTOR_TAG = "contributor";
	private static final String CONTRIBUTOR_NAME_TAG = "contributorName";
	private static final String REPLY_TO_SCREEN_NAME_TAG = "replyToScreenName";
	private static final String REPLY_STATUS_ID_TAG = "replyStatusID";
	private static final String REPLY_USER_ID_TAG = "replyUserID";
	private static final String PLACE_TAG = "place";
	private static final String RETWEET_STATUS_TAG = "retweetStatus";
	private static final String TEXT_TAG = "text";
	private static final String FAVORITE_TAG = "favorite";
	private static final String RETWEET_TAG = "retweet";
	private static final String TRUNCATED_TAG = "truncated";
	private static final String SOURCE_TAG = "source";

	// USER XML TAG
	private static final String USER_TAG = "user";
	private static final String CREATED_AT_TAG = "created";
	private static final String DESCRIPTION_TAG = "description";
	private static final String FAVOURITES_TAG = "favourites";
	private static final String FOLLOWERS_TAG = "followers";
	private static final String FRIENDS_TAG = "friends";
	private static final String LANG_TAG = "lang";
	private static final String NAME_TAG = "name";
	private static final String PROFILE_BACKGROUND_COLOR_TAG = "profileBackgroundColor";
	private static final String PROFILE_BACKGROUND_IMAGE_URL_TAG = "profileBackgroundImageURL";
	private static final String PROFILE_IMAGE_URL = "profileImageURL";
	private static final String PROFILE_LINK_COLOR_TAG = "profileLinkColor";
	private static final String PROFILE_SIDEBAR_BORDER_COLOR_TAG = "profileSidebarBorderColor";
	private static final String PROFILE_SIDEBAR_FILL_COLOR_TAG = "profileSidebarFillColor";
	private static final String PROFILE_TEXT_COLOR_TAG = "profileTextColor";
	private static final String SCREEN_NAME = "screenName";
	private static final String STATUSES_COUNT_TAG = "statusesCount";
	private static final String TIMEZONE_TAG = "timeZone";
	private static final String URL_TAG = "url";
	private static final String UTCOFFSET_TAG = "utc";
	private static final String CONTRIBUTORS_ENABLE_TAG = "contributorsEnable";
	private static final String GEO_ENABLE_TAG = "geoEnable";
	private static final String PROFILE_BACKGROUND_TILED_TAG = "profileBackgroundTiled";
	private static final String PROTECTED_TAG = "protected";
	private static final String VERIFIED_TAG = "verified";

	// 時間
	private static final String DATE_PATTERN = "yyyy.MM.dd HH:mm:ss";

	/**
	 * StatusをXML文章に変換する
	 * 
	 * @param status
	 * @return
	 */
	public static String convertStatusToXML(Status status) {
		String[] contributor = status.getContributors();
		String date = null;
		if (status.getCreatedAt() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
			date = sdf.format(status.getCreatedAt());
		}
		String location = null;
		if (status.getGeoLocation() != null) {
			location = status.getGeoLocation().toString();
		}
		long id = status.getId();
		String replyToScreenName = status.getInReplyToScreenName();
		long replyStatusID = status.getInReplyToStatusId();
		long replyUserID = status.getInReplyToUserId();
		String place = null;
		if (status.getPlace() != null) {
			place = status.getPlace().toString();
		}
		Status retweetStatus = status.getRetweetedStatus();
		String text = status.getText();
		User user = status.getUser();
		boolean favorite = status.isFavorited();
		boolean retweet = status.isRetweet();
		boolean truncated = status.isTruncated();
		String source = status.getSource();

		// StatusをXMLに変換
		StringBuffer buf = new StringBuffer("");
		buf.append("<" + TWEET_TAG + ">\n");
		// Contributor
		if (contributor != null) {
			buf.append("<" + CONTRIBUTOR_TAG + ">");
			for (String c : contributor) {
				buf.append(createXMLTag(CONTRIBUTOR_NAME_TAG, HTMLEncode
						.encode(c)));
			}
			buf.append("</" + CONTRIBUTOR_TAG + ">");
		}
		// date
		if (date != null) {
			buf.append(createXMLTag(DATE_TAG, HTMLEncode.encode(date)));
		}
		// location
		if (location != null) {
			buf.append(createXMLTag(LOCATION_TAG, HTMLEncode.encode(location)));
		}
		// id
		buf.append(createXMLTag(ID_TAG, id));
		// replyToScreenName
		if (replyToScreenName != null) {
			buf.append(createXMLTag(REPLY_TO_SCREEN_NAME_TAG, HTMLEncode
					.encode(replyToScreenName)));
		}
		// replyStatusID
		buf.append(createXMLTag(REPLY_STATUS_ID_TAG, replyStatusID));
		// replyUserID
		buf.append(createXMLTag(REPLY_USER_ID_TAG, replyUserID));
		// place
		if (place != null) {
			buf.append(createXMLTag(PLACE_TAG, HTMLEncode.encode(place)));
		}
		// RetweetStatus
		if (retweetStatus != null) {
			buf.append(createXMLTag(RETWEET_STATUS_TAG,
					convertStatusToXML(retweetStatus)));
		}
		// text
		if (text != null) {
			buf.append(createXMLTag(TEXT_TAG, HTMLEncode.encode(text)));
		}

		// User Converter
		if (user != null) {
			buf.append(convertUserToXML(user));
		}

		// Source
		if (source != null) {
			buf.append(createXMLTag(SOURCE_TAG, HTMLEncode.encode(source)));
		}

		// favorite
		buf.append(createXMLTag(FAVORITE_TAG, favorite));
		// retweet
		buf.append(createXMLTag(RETWEET_TAG, retweet));
		// truncated
		buf.append(createXMLTag(TRUNCATED_TAG, truncated));

		buf.append("</" + TWEET_TAG + ">");
		return buf.toString();
	}

	/**
	 * UserをXMLに変換する
	 * 
	 * @param user
	 * @return
	 */
	public static String convertUserToXML(User user) {
		// UserをXMLに変換
		StringBuffer buf = new StringBuffer("");
		buf.append("<" + USER_TAG + ">\n");
		// createdAt
		if (user.getCreatedAt() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
			buf.append(createXMLTag(CREATED_AT_TAG, sdf.format(user
					.getCreatedAt())));
		}
		// description
		if (user.getDescription() != null) {
			buf.append(createXMLTag(DESCRIPTION_TAG, HTMLEncode.encode(user
					.getDescription())));
		}
		// favouritesCount
		buf.append(createXMLTag(FAVORITE_TAG, user.getFavouritesCount()));
		// follower
		buf.append(createXMLTag(FOLLOWERS_TAG, user.getFollowersCount()));
		// friend
		buf.append(createXMLTag(FRIENDS_TAG, user.getFriendsCount()));
		// id
		buf.append(createXMLTag(ID_TAG, user.getId()));
		// lang
		if (user.getLang() != null) {
			buf
					.append(createXMLTag(LANG_TAG, HTMLEncode.encode(user
							.getLang())));
		}
		// location
		if (user.getLocation() != null) {
			buf.append(createXMLTag(LOCATION_TAG, HTMLEncode.encode(user
					.getLocation())));
		}
		// name
		if (user.getName() != null) {
			buf
					.append(createXMLTag(NAME_TAG, HTMLEncode.encode(user
							.getName())));
		}
		// profileBackgroundColor
		if (user.getProfileBackgroundColor() != null) {
			buf.append(createXMLTag(PROFILE_BACKGROUND_COLOR_TAG, HTMLEncode
					.encode(user.getProfileBackgroundColor())));
		}
		// profileBackgroundImageURL
		if (user.getProfileBackgroundImageUrl() != null) {
			buf.append(createXMLTag(PROFILE_BACKGROUND_IMAGE_URL_TAG,
					HTMLEncode.encode(user.getProfileBackgroundImageUrl())));
		}
		// profileImageURL
		if (user.getProfileImageURL() != null) {
			buf.append(createXMLTag(PROFILE_IMAGE_URL, HTMLEncode.encode(user
					.getProfileImageURL().toString())));
		}
		// profile link color
		if (user.getProfileBackgroundColor() != null) {
			buf.append(createXMLTag(PROFILE_BACKGROUND_COLOR_TAG, HTMLEncode
					.encode(user.getProfileBackgroundColor())));
		}
		// profile sidebar border color
		if (user.getProfileSidebarBorderColor() != null) {
			buf.append(createXMLTag(PROFILE_SIDEBAR_BORDER_COLOR_TAG,
					HTMLEncode.encode(user.getProfileSidebarBorderColor())));
		}
		// profile sidebar fill color
		if (user.getProfileSidebarFillColor() != null) {
			buf.append(createXMLTag(PROFILE_SIDEBAR_FILL_COLOR_TAG, HTMLEncode
					.encode(user.getProfileSidebarFillColor())));
		}
		// profile text color
		if (user.getProfileTextColor() != null) {
			buf.append(createXMLTag(PROFILE_TEXT_COLOR_TAG, HTMLEncode
					.encode(user.getProfileTextColor())));
		}
		// screen name
		if (user.getScreenName() != null) {
			buf.append(createXMLTag(SCREEN_NAME, HTMLEncode.encode(user
					.getScreenName())));
		}
		// status
		if (user.getStatus() != null) {
			buf.append(convertStatusToXML(user.getStatus()));
		}
		// statuses count
		buf.append(createXMLTag(STATUSES_COUNT_TAG, user.getStatusesCount()));
		// timezone
		if (user.getTimeZone() != null) {
			buf.append(createXMLTag(TIMEZONE_TAG, HTMLEncode.encode(user
					.getTimeZone())));
		}
		// url
		if (user.getURL() != null) {
			buf.append(createXMLTag(URL_TAG, HTMLEncode.encode(user.getURL()
					.toString())));
		}
		// utc
		buf.append(createXMLTag(UTCOFFSET_TAG, user.getUtcOffset()));
		// cotributors enable
		buf.append(createXMLTag(CONTRIBUTORS_ENABLE_TAG, user
				.isContributorsEnabled()));
		// geo enable
		buf.append(createXMLTag(GEO_ENABLE_TAG, user.isGeoEnabled()));
		// profile background tiled
		buf.append(createXMLTag(PROFILE_BACKGROUND_TILED_TAG, user
				.isProfileBackgroundTiled()));
		// protected
		buf.append(createXMLTag(PROTECTED_TAG, user.isProtected()));
		// status favorite
		buf.append(createXMLTag(VERIFIED_TAG, user.isVerified()));

		buf.append("</" + USER_TAG + ">\n");
		return buf.toString();
	}

	/**
	 * 
	 * @param tag
	 * @param data
	 * @return
	 */
	private static String createXMLTag(String tag, String data) {
		return "<" + tag + ">" + data + "</" + tag + ">\n";
	}

	/**
	 * 
	 * @param tag
	 * @param data
	 * @return
	 */
	private static String createXMLTag(String tag, long data) {
		return "<" + tag + ">" + data + "</" + tag + ">\n";
	}

	/**
	 * 
	 * @param tag
	 * @param data
	 * @return
	 */
	private static String createXMLTag(String tag, int data) {
		return "<" + tag + ">" + data + "</" + tag + ">\n";
	}

	/**
	 * 
	 * @param tag
	 * @param data
	 * @return
	 */
	private static String createXMLTag(String tag, boolean data) {
		return "<" + tag + ">" + data + "</" + tag + ">\n";
	}

	/**
	 * XML文章からStatusエレメントを取得
	 * 
	 * @param xmlData
	 * @return
	 * @throws SAXParseException
	 */
	public static List<Status> XMLToStatus(String xmlData)
			throws SAXParseException {
		if (xmlData == null) {
			return null;
		}
		try {
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			Document doc = null;
			// 入力ストリーム作成
			StringReader sr = new StringReader(xmlData);
			InputSource is = new InputSource(sr);

			doc = builder.parse(is);

			// ルート要素になっている子ノードを取得
			Element root = doc.getDocumentElement();
			// System.out.println("ルート要素名 : " + root.getTagName());
			// status要素を取得
			NodeList comments = root.getElementsByTagName(TWEET_TAG);
			ArrayList<Status> ret = new ArrayList<Status>();

			for (int i = 0; i < comments.getLength(); i++) {
				// page要素を取得
				Element element = (Element) comments.item(i);
				// 各要素を取得
				String date = getChildren(element, DATE_TAG);
				String location = getChildren(element, LOCATION_TAG);
				String id = getChildren(element, ID_TAG);
				// TODO: contributor
				String replyToScreenName = getChildren(element,
						REPLY_TO_SCREEN_NAME_TAG);
				String replyStatusID = getChildren(element, REPLY_STATUS_ID_TAG);
				String replyUserID = getChildren(element, REPLY_STATUS_ID_TAG);
				String place = getChildren(element, PLACE_TAG);
				// String user = getChildren(element, USER_TAG);
				NodeList userNode = element.getElementsByTagName(USER_TAG);
				String retweetStatus = getChildren(element, RETWEET_STATUS_TAG);
				String text = getChildren(element, TEXT_TAG);
				String favorite = getChildren(element, FAVORITE_TAG);
				String retweet = getChildren(element, RETWEET_TAG);
				String truncated = getChildren(element, TRUNCATED_TAG);
				String source = getChildren(element, SOURCE_TAG);
				// Status
				SimpleStatus status = new SimpleStatus();
				// 時間データ変換
				SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
				try {
					if (date != null) {
						status.setCreatedAt(dateFormat.parse(date));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				// TODO: Geo location
				// status.setGeoLocation(null);
				// id
				if (id != null) {
					status.setId(Long.parseLong(id));
				}
				// replyToScreenName
				status.setInReplyToScreenName(replyToScreenName);
				if (replyStatusID != null) {
					status.setInReplyToStatusId(Long.parseLong(replyStatusID));
				}
				if (replyUserID != null) {
					status.setInReplyToUserId(Long.parseLong(replyUserID));
				}
				// TODO: Place
				// TODO: RetweetStatus ここは実装可能
				if (retweetStatus != null) {

				}
				status.setText(text);
				if (favorite != null) {
					status.setFavorited(Boolean.parseBoolean(favorite));
				}
				if (retweet != null) {
					status.setRetweet(Boolean.parseBoolean(retweet));
				}
				if (truncated != null) {
					status.setTruncated(Boolean.parseBoolean(truncated));
				}
				// ユーザタグ
				if (userNode != null) {
					User u = XMLNodeToUser(userNode);
					status.setUser(u);
				}

				// Source
				status.setSource(source);

				ret.add(status);
			}

			return ret;

		} catch (SAXParseException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * NodeListからUSERを取得
	 * 
	 * @param xmlData
	 * @return
	 * @throws SAXParseException
	 */
	private static User XMLNodeToUser(NodeList node) throws SAXParseException {
		// ユーザ情報
		SimpleUser ret = null;
		try {
			for (int i = 0; i < node.getLength(); i++) {
				ret = new SimpleUser();
				// page要素を取得
				Element element = (Element) node.item(i);
				// 各要素を取得
				String createdAt = getChildren(element, CREATED_AT_TAG);
				String description = getChildren(element, DESCRIPTION_TAG);
				String favorites = getChildren(element, FAVOURITES_TAG);
				String followers = getChildren(element, FOLLOWERS_TAG);
				String friends = getChildren(element, FRIENDS_TAG);
				String lang = getChildren(element, LANG_TAG);
				String name = getChildren(element, NAME_TAG);
				String profileBackgroundColor = getChildren(element,
						PROFILE_BACKGROUND_COLOR_TAG);
				String profileBackgroundImageURL = getChildren(element,
						PROFILE_BACKGROUND_IMAGE_URL_TAG);
				String profileImageURL = getChildren(element, PROFILE_IMAGE_URL);
				String profileLinkColor = getChildren(element,
						PROFILE_LINK_COLOR_TAG);
				String profileSidebarBorderColor = getChildren(element,
						PROFILE_SIDEBAR_BORDER_COLOR_TAG);
				String profileSidebarFillColor = getChildren(element,
						PROFILE_SIDEBAR_FILL_COLOR_TAG);
				String profileTextColor = getChildren(element,
						PROFILE_TEXT_COLOR_TAG);
				String screenName = getChildren(element, SCREEN_NAME);
				String statusesCount = getChildren(element, STATUSES_COUNT_TAG);
				String timeZone = getChildren(element, TIMEZONE_TAG);
				String url = getChildren(element, URL_TAG);
				String utcOffset = getChildren(element, UTCOFFSET_TAG);
				String contributorEnable = getChildren(element,
						CONTRIBUTORS_ENABLE_TAG);
				String geoEnable = getChildren(element, GEO_ENABLE_TAG);
				String profileBackgroundTiled = getChildren(element,
						PROFILE_BACKGROUND_TILED_TAG);
				String protectedTag = getChildren(element, PROTECTED_TAG);
				String verified = getChildren(element, VERIFIED_TAG);
				String id = getChildren(element, ID_TAG);

				// 時間データ変換
				SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
				try {
					if (createdAt != null) {
						ret.setStatusCreatedAt(dateFormat.parse(createdAt));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				// 情報設定
				ret.setDescription(description);
				if (favorites != null) {
					Integer v = Integer.parseInt(favorites);
					ret.setFavouritesCount(v);
				}
				if (followers != null) {
					Integer v = Integer.parseInt(followers);
					ret.setFollowersCount(v);
				}
				if (friends != null) {
					Integer v = Integer.parseInt(friends);
					ret.setFriendsCount(v);
				}
				ret.setLang(lang);
				ret.setName(name);
				ret.setProfileBackgroundColor(profileBackgroundColor);
				ret.setProfileBackgroundImageUrl(profileBackgroundImageURL);
				if (profileImageURL != null) {
					ret.setProfileImageURL(new URL(profileImageURL));
				}
				ret.setProfileLinkColor(profileLinkColor);
				ret.setProfileSidebarBorderColor(profileSidebarBorderColor);
				ret.setProfileSidebarFillColor(profileSidebarFillColor);
				ret.setProfileTextColor(profileTextColor);
				ret.setScreenName(screenName);
				if (statusesCount != null) {
					Integer v = Integer.parseInt(statusesCount);
					ret.setStatusesCount(v);
				}
				ret.setTimeZone(timeZone);
				if (url != null) {
					ret.setURL(new URL(url));
				}
				if (utcOffset != null) {
					Integer v = Integer.parseInt(utcOffset);
					ret.setUtcOffset(v);
				}
				if (contributorEnable != null) {
					Boolean v = Boolean.parseBoolean(contributorEnable);
					ret.setContributorsEnabled(v);
				}
				if (geoEnable != null) {
					Boolean v = Boolean.parseBoolean(geoEnable);
					ret.setGeoEnabled(v);
				}
				if (profileBackgroundTiled != null) {
					Boolean v = Boolean.parseBoolean(profileBackgroundTiled);
					ret.setProfileBackgroundTiled(v);
				}
				if (protectedTag != null) {
					Boolean v = Boolean.parseBoolean(protectedTag);
					ret.setProtected(v);
				}
				if (verified != null) {
					Boolean v = Boolean.parseBoolean(verified);
					ret.setVerified(v);
				}
				if (id != null) {
					Integer v = Integer.parseInt(id);
					ret.setId(v);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 指定されたエレメントから子要素の内容を取得
	 * 
	 * @param element
	 *            指定エレメント
	 * @param tagName
	 *            指定タグ名
	 * @return 取得した内容
	 */
	private static String getChildren(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		Element cElement = (Element) list.item(0);
		if (cElement == null || cElement.getFirstChild() == null) {
			return null;
		}
		return cElement.getFirstChild().getNodeValue();
	}
}
