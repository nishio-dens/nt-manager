package twitter.manage;

import java.util.Date;

import twitter4j.Annotations;
import twitter4j.DirectMessage;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

/**
 * DirectMessageのユーザ情報等からステータスを生成
 *
 * @author nishio
 *
 */
public class DirectMessageUserStatus implements Status {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	// 受信したdirectMessageの情報
	private DirectMessage message = null;
	// ユーザ情報
	private User user = null;

	public DirectMessageUserStatus(DirectMessage directMessage, User user) {
		this.message = directMessage;
		this.user = user;
	}

	@Override
	public long[] getContributors() {
		return null;
	}

	@Override
	public Date getCreatedAt() {
		return message.getCreatedAt();
	}

	@Override
	public GeoLocation getGeoLocation() {
		return null;
	}

	@Override
	public long getId() {
		return user.getId();
	}

	@Override
	public String getInReplyToScreenName() {
		return ((SimpleUser) user).getStatusInReplyToScreenName();
	}

	@Override
	public long getInReplyToStatusId() {
		return ((SimpleUser) user).getStatusInReplyToUserId();
	}

	@Override
	public long getInReplyToUserId() {
		return 0;
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() {
		return user.getRateLimitStatus();
	}

	@Override
	public Status getRetweetedStatus() {
		return null;
	}

	@Override
	public String getSource() {
		return "Unknown";
	}

	@Override
	public String getText() {
		return message.getText();
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public boolean isFavorited() {
		return ((SimpleUser) user).isStatusFavorited();
	}

	@Override
	public boolean isRetweet() {
		return false;
	}

	@Override
	public boolean isTruncated() {
		return false;
	}

	public int compareTo(Status t) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Annotations getAnnotations() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public HashtagEntity[] getHashtagEntities() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public long getRetweetCount() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public URLEntity[] getURLEntities() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public UserMentionEntity[] getUserMentionEntities() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public boolean isRetweetedByMe() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public int getAccessLevel() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public MediaEntity[] getMediaEntities() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}