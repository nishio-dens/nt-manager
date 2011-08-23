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
	 * タイムライン監視リスナーを削除
	 */
	public void stopTimelineListener() {
		this.userStream.setTimelineListener(null);
	}

	/**
	 * Mentionを監視するリスナー
	 * @param listener
	 * @param notifyManager メッセージをバルーン通知するためのマネージャー, nullなら通知しない
	 */
	public void setMentionListener(TweetStreamingListener listener, TweetNotifyManager notifyManager) {
		this.userStream.setMentionListener(listener);
		this.userStream.setMentionNotifyManager(notifyManager);
	}

	/**
	 * Mentionを監視するリスナーをストップ
	 */
	public void stopMentionListener() {
		this.userStream.setMentionListener(null);
		this.userStream.setMentionNotifyManager(null);
	}

	/**
	 * 検索ワードを監視するリスナー
	 * @param word
	 * @param listener
	 */
	public void setSearchListener(String word, TweetStreamingListener listener) {
		searchStream.addSearchWord(word, listener);
	}

	/**
	 * 検索ワードを監視するリスナーを停止
	 * @param word
	 */
	public void stopSearchListener(String word) {
		searchStream.removeSearchWord(word);
	}

	/**
	 * 指定したワードの最終更新status IDを取得
	 * @param word
	 * @return
	 */
	public long getSearchLastUpdateID(String word) {
		return searchStream.getLastUpdateID(word);
	}
}
