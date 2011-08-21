package twitter.manage;

import twitter.action.streaming.TweetStreamingListener;
import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserStreamAdapter;
import twitter4j.auth.AccessToken;

public class TweetUserStreamManager extends UserStreamAdapter {

	//自分がfollowしているユーザの情報取得
	private TweetUserStream userStream;
	//Search情報
	private TweetSearchStream searchStream;
	//tweet manager
	private TweetManager tweetManager = null;

	/**
	 *
	 * @param consumerKey
	 * @param consumerSecret
	 * @param ac
	 * @param tweetManager
	 */
	public TweetUserStreamManager(String consumerKey, String consumerSecret, AccessToken ac, TweetManager tweetManager) {
		try {
			userStream = new TweetUserStream(consumerKey, consumerSecret, ac, tweetManager);
			searchStream = new TweetSearchStream(consumerKey, consumerSecret, ac, tweetManager);
			this.tweetManager = tweetManager;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * タイムラインを監視するリスナー登録
	 * @param listener
	 */
	public void setTimelineListener(TweetStreamingListener listener) {
		this.userStream.setTimelineListener(listener);
	}

	/**
	 * 検索ワードを監視するリスナー
	 * @param word
	 * @param listener
	 */
	public void setSearchListener(String word, TweetStreamingListener listener) {
		searchStream.addSearchWord(word, listener);
	}
}
