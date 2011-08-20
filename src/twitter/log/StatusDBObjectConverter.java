package twitter.log;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.w3c.dom.NodeList;

import twitter.manage.SimpleStatus;
import twitter.manage.SimpleUser;
import twitter4j.Status;
import twitter4j.User;

public class StatusDBObjectConverter {

	// 時間
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * DBのオブジェクトからStatusに変換
	 * @param obj
	 * @return
	 */
	public static Status convertDBObjectToStatus(TweetDBObject obj) {
		if( obj == null ) {
			return null;
		}

		SimpleStatus status = new SimpleStatus();
		status.setId( obj.getId() );

		//DBから情報読み込み
		String date = obj.getCreated();
		//String location = obj.getLocation();
		// TODO: reply to screen name実装
		String replyToScreenName = "";
		long replyStatusID = obj.getReplyStatusID();
		long replyUserID = obj.getReplyUserID();
		//String place = obj.getLocation();
		String text = obj.getText();
		String favorite = obj.getFavorite();
		String retweet = obj.getRetweet();
		String truncated = obj.getTruncated();
		String source = obj.getSource();

		// created at
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
		try {
			if (date != null) {
				status.setCreatedAt(dateFormat.parse(date));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if( text != null ) {
			status.setText( obj.getText() );
		}
		// replyToScreenName
		status.setInReplyToScreenName(replyToScreenName);
		status.setInReplyToStatusId(replyStatusID);
		status.setInReplyToUserId(replyUserID);
		// TODO: Place
		if (favorite != null) {
			status.setFavorited(Boolean.parseBoolean(favorite));
		}
		if (retweet != null) {
			status.setRetweet(Boolean.parseBoolean(retweet));
		}
		if (truncated != null) {
			status.setTruncated(Boolean.parseBoolean(truncated));
		}
		// Source
		status.setSource(source);

		//User information
		String description = obj.getDescription();
		long followers = obj.getFollowers();
		long friends = obj.getFriend();
		String lang = obj.getLang();
		String name = obj.getName();
		String profileBackgroundColor = obj.getProfileBackgroundColor();
		String profileBackgroundImageURL = obj.getProfileBackgroundImageURL();
		String profileImageURL = obj.getProfileImageURL();
		String profileSidebarBorderColor = obj.getProfileSidebarBorderColor();
		String profileSidebarFillColor = obj.getProfileSidebarFillColor();
		String profileTextColor = obj.getProfileTextColor();
		String screenName = obj.getScreenName();
		long statusesCount = obj.getStatusesCount();
		String timeZone = obj.getTimeZone();
		String url = obj.getUrl();
		long utcOffset = obj.getUtc();
		String contributorEnable = obj.getContributorsEnable();
		String geoEnable = obj.getGeoEnable();
		String profileBackgroundTiled = obj.getProfileBackgroundTiled();
		String verified = obj.getVerified();

		SimpleUser ret = new SimpleUser();

		// 情報設定
		ret.setDescription(description);
		ret.setFollowersCount((int)followers);
		ret.setFriendsCount((int)friends);
		ret.setLang(lang);
		ret.setName(name);
		ret.setProfileBackgroundColor(profileBackgroundColor);
		ret.setProfileBackgroundImageUrl(profileBackgroundImageURL);
		if (profileImageURL != null) {
			try {
				ret.setProfileImageURL(new URL(profileImageURL));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		ret.setProfileSidebarBorderColor(profileSidebarBorderColor);
		ret.setProfileSidebarFillColor(profileSidebarFillColor);
		ret.setProfileTextColor(profileTextColor);
		ret.setScreenName(screenName);
		ret.setStatusesCount((int)statusesCount);
		ret.setTimeZone(timeZone);
		if (url != null) {
			try {
				ret.setURL(new URL(url));
			} catch (MalformedURLException e) {
				//e.printStackTrace();
			}
		}
		ret.setUtcOffset((int)utcOffset);
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
		if (verified != null) {
			Boolean v = Boolean.parseBoolean(verified);
			ret.setVerified(v);
		}
		status.setUser(ret);

		return status;
	}

	/**
	 * StatusからDBに保存するためのオブジェクトに変換
	 * @param s
	 * @return
	 */
	public static TweetDBObject convertStatusToDBObject(Status s) {
		TweetDBObject result = new TweetDBObject();

		result.setId( s.getId() );

		String date = null;
		if (s.getCreatedAt() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
			date = sdf.format(s.getCreatedAt());
		}
		result.setDate( date );

		result.setReplyStatusID( s.getInReplyToStatusId() );
		result.setReplyUserID( s.getInReplyToUserId() );
		result.setText( s.getText() );

		//ユーザ情報
		User u = s.getUser();
		if( u != null ) {

			String userCreated = null;
			if (u.getCreatedAt() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
				userCreated = sdf.format(s.getCreatedAt());
			}
			result.setCreated( userCreated );
			result.setDescription( u.getDescription() );
			result.setUserFavorite( u.getFavouritesCount() );
			result.setFollowers( u.getFollowersCount() );
			result.setFriend( u.getFriendsCount() );
			result.setUserId( u.getId() );
			result.setLang( u.getLang() );
			result.setLocation( u.getLocation() );
			result.setName( u.getName() );
			result.setProfileBackgroundColor( u.getProfileBackgroundColor() );
			result.setProfileBackgroundImageURL( u.getProfileBackgroundImageUrl() );
			result.setProfileImageURL( u.getProfileImageURL().toString() );
			result.setProfileSidebarBorderColor( u.getProfileSidebarBorderColor() );
			result.setProfileSidebarFillColor( u.getProfileSidebarFillColor() );
			result.setProfileTextColor( u.getProfileTextColor() );
			result.setScreenName( u.getScreenName() );
			result.setStatusesCount( u.getStatusesCount() );
			result.setTimeZone( u.getTimeZone() );
			if( u.getURL() != null ) {
				result.setUrl( u.getURL().toString() );
			}else {
				result.setUrl("");
			}
			result.setUtc( u.getUtcOffset() );
			result.setContributorsEnable( u.isContributorsEnabled() + "" );
			result.setGeoEnable( u.isGeoEnabled() + "");
			result.setProfileBackgroundTiled( u.isProfileBackgroundTiled() + "");
			result.setIsProtected( u.isProtected() + "");
			result.setVerified( u.isVerified() + "");
		}
		result.setSource( s.getSource() );
		result.setFavorite( s.isFavorited() + "");
		result.setRetweet( s.isRetweet() + "");
		result.setTruncated( s.isTruncated() + "");

		return result;
	}
}
