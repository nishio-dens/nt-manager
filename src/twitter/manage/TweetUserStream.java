package twitter.manage;

import java.util.ArrayList;
import java.util.logging.Logger;

import twitter.action.streaming.TweetStreamingListener;
import twitter4j.ConnectionLifeCycleListener;
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
	//direct message監視
	private TweetStreamingListener directMessageListener = null;
	//mention通知
	private TweetNotifyManager mentionNotifyManager = null;
	//directmessage通知
	private TweetNotifyManager directMessageNotifyManager = null;
	//fav通知を行うかどうか
	private TweetFavNotifyManager favNotifyManager = null;
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
		loginUsername = tweetManager.getLoginUserScreenName();
	}

	/**
	 * streaming開始
	 */
	public void start() {
	    this.twitterStream.user();
	}

	/**
	 * streaming停止
	 */
	public void stop() {
	    this.twitterStream.cleanUp();
	}

	/**
	 * コネクションが接続されたときに呼び出される
	 * @param listener
	 */
	public void addConnectionLifeCycleListener(ConnectionLifeCycleListener listener) {
	    this.twitterStream.addConnectionLifeCycleListener(listener);
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
	}

	/**
	 * mention通知バー
	 * @param notifyManager nullならmentionでは通知しない
	 */
	public void setMentionNotifyManager(TweetNotifyManager notifyManager) {
		this.mentionNotifyManager = notifyManager;
	}

	/**
	 * お気に入り登録通知
	 */
	public void setFavNotifyManager(TweetFavNotifyManager notifyManager) {
	    this.favNotifyManager = notifyManager;
	}

	/**
	 * direct message通知バー
	 * @param notifyManager
	 */
	public void setDirectMessageNotifyManager(TweetNotifyManager notifyManager) {
		this.directMessageNotifyManager = notifyManager;
	}

	/**
	 * ダイレクトメッセージ監視
	 * @param directMessageListener
	 */
	public void setDirectMessageListener(TweetStreamingListener directMessageListener) {
		this.directMessageListener = directMessageListener;
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
			if( loginUsername != null && status.getText().contains( loginUsername ) && !status.isRetweet() ) {
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

	/**
	 * ダイレクトメッセージの通知
	 * @param directmessage
	 */
	@Override
	public void onDirectMessage(DirectMessage directmessage) {
		if( this.directMessageListener != null ) {
			User user = directmessage.getSender();
			DirectMessageUserStatus status = new DirectMessageUserStatus(
					directmessage, user);
			this.directMessageListener.update(status);
			if( directMessageNotifyManager != null ) {
				directMessageNotifyManager.showNotifyMessage(status);
			}
		}
	}

	/**
	 * お気に入り登録通知
	 * @param source
	 * @param target
	 * @param favoritedStatus
	 */
	@Override
	public void onFavorite(User source, User target, Status favoritedStatus) {
	    if( favNotifyManager != null ) {
	    	if( target.getScreenName().equals(this.loginUsername) ) {
	    		favNotifyManager.showNotifyMessage(source, target, favoritedStatus);
	    	}
	    }
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
