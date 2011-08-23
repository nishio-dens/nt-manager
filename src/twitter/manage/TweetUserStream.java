package twitter.manage;

import java.util.ArrayList;
import java.util.logging.Logger;

import twitter.action.streaming.TweetStreamingListener;
import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserStreamAdapter;
import twitter4j.auth.AccessToken;

/**
 * ユーザにかかわるツイートを取得
 * @author nishio
 *
 */
public class TweetUserStream extends UserStreamAdapter{
	//streaming
	private TwitterStream twitterStream = null;
	//timeline監視listener
	private TweetStreamingListener timelineListener = null;
	//mention監視
	private TweetStreamingListener mentionListener = null;
	//mention通知
    private TweetNotifyManager mentionNotifyManager = null;
	//ログインユーザ名
	private String loginUsername = null;
	//tweet manager
	private TweetManager tweetManager = null;

	/**
	 *
	 * @param consumerKey
	 * @param consumerSecret
	 * @param ac アクセストークン
	 * @param tweetManager
	 */
	public TweetUserStream(String consumerKey, String consumerSecret, AccessToken ac, TweetManager tweetManager) {
		this.tweetManager = tweetManager;
		this.twitterStream = new TwitterStreamFactory().getInstance();
		this.twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
		this.twitterStream.setOAuthAccessToken(ac);
		this.twitterStream.addListener(this);
		this.twitterStream.user();

	}

	/**
	 * タイムライン監視
	 * @param timelineListener
	 */
	public void setTimelineListener(TweetStreamingListener timelineListener) {
		this.timelineListener = timelineListener;
	}

	/**
	 * メンション監視
	 * @param mentionListener
	 */
	public void setMentionListener(TweetStreamingListener mentionListener) {
		this.mentionListener = mentionListener;
		loginUsername = tweetManager.getLoginUserScreenName();
	}

	/**
	 * 通知バー
	 * @param notifyManager nullならmentionでは通知しない
	 */
	public void setMentionNotifyManager(TweetNotifyManager notifyManager) {
		this.mentionNotifyManager = notifyManager;
	}


	@Override
	public void onStatus(Status status) {
		/*if( status.isRetweetedByMe() ) {
			System.out.println( status.getUser().getScreenName() + " Retweet my message");
		}
		System.out.println("@" + status.getUser().getScreenName() + " - "
				+ status.getText());*/

		//タイムライン監視
		if( this.timelineListener != null ) {
			this.timelineListener.update(status);
			this.tweetManager.setSinceTweetID(status.getId());
		}
		//mention監視
		if( this.mentionListener != null ) {
			if( status.getText().contains( loginUsername ) ) {
				this.mentionListener.update(status);
				this.tweetManager.setSinceMentionID(status.getId());
				//mentionのバルーン通知
				if( mentionNotifyManager != null ) {
					mentionNotifyManager.showNotifyMessage( status );
				}
			}
		}
	}

	@Override
	public void onException(Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onDirectMessage(DirectMessage directmessage) {
		System.out.println("Recipient: "
				+ directmessage.getRecipientScreenName() + " from "
				+ "Sender   : " + directmessage.getSenderScreenName()
				+ " text     : " + directmessage.getText() );
	}

	@Override
	public void onFavorite(User source, User target, Status favoritedStatus) {
		System.out.println(source.getScreenName() + " favorited "
				+ target.getScreenName() + "'s Status. StatusId: "
				+ favoritedStatus.getId() );
		System.out.println("FAV MESSAGE:" + favoritedStatus.getText());
	}

	@Override
	public void onFriendList(long[] friendIds) {
		/*String tempString = "FriendIds(Up to 2000): \n";
		int max = 2000;
		int count = friendIds.length;

		if (count > 0) {
			if (count < max) {
				max = count;
			}

			for (int i = 0; i < max; i++) {
				tempString += friendIds[i] + " ";
			}
			System.out.println(tempString);
		}*/
	}
}
