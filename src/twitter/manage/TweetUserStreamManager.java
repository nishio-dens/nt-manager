package twitter.manage;

import twitter.action.streaming.TweetStreamingListener;
import twitter4j.ConnectionLifeCycleListener;
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
	 * Direct messageを監視するリスナー登録
	 * @param listener
	 * @param notifyManager
	 */
	public void setDirectMessageListener(TweetStreamingListener listener, TweetNotifyManager notifyManager) {
		this.userStream.setDirectMessageListener(listener);
		this.userStream.setDirectMessageNotifyManager(notifyManager);
	}

	/**
	 * directmessageを監視するリスナーを削除
	 */
	public void stopDirectMessageListener() {
		this.userStream.setDirectMessageListener(null);
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
	 * 指定したユーザを監視するリスナー
	 * @param userid
	 * @param listener
	 */
	public void setUserListener(long userid, TweetStreamingListener listener) {
		searchStream.addSearchUser(userid, listener);
	}

	/**
	 * 指定したユーザを監視するリスナーを停止
	 * @param userid
	 */
	public void stopUserListener(long userid) {
		searchStream.removeSearchUser(userid);
	}

	/**
	 * 指定したワードの最終更新status IDを取得
	 * @param word
	 * @return
	 */
	public long getSearchLastUpdateID(String word) {
		return searchStream.getLastUpdateID(word);
	}

	/**
	 * 指定したユーザの最終更新status idを取得
	 * @param userid
	 * @return
	 */
	public long getUserLastUpdateID(long userid) {
		return searchStream.getUserLastUpdateID(userid);
	}
	
	/**
	 * コネクションに変化が起きた時に呼び出される
	 * @param listener 
	 */
	public void addCollectionLifeCycleListener(ConnectionLifeCycleListener listener) {
	    this.userStream.addConnectionLifeCycleListener(listener);
	}
	
	/**
	 * streaming開始
	 */
	public void start() {
	    Runnable runner = new Runnable() {
		@Override
		public void run() {
		    userStream.start();
		}
	    };
	    new Thread(runner).start();
	}
	
	/**
	 * streaming停止
	 */
	public void stop() {
	    Runnable runner = new Runnable() {
		@Override
		public void run() {
		    userStream.stop();
		}
	    };
	    new Thread(runner).start();
	}
}
