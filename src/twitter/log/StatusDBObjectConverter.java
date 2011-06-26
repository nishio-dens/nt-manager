package twitter.log;

import java.text.SimpleDateFormat;

import twitter4j.Status;
import twitter4j.User;

public class StatusDBObjectConverter {

	// 時間
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * StatusからDBに保存するためのオブジェクトに変換
	 * @param s
	 * @param following 自分がフォローしている相手かどうか
	 * @return
	 */
	public static TweetDBObject convertStatusToDBObject(Status s, boolean following) {
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
