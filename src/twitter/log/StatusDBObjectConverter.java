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
		result.setFollowing(following + "");
		
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
			
		}
		return result;
	}
}
