package twitter.manage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import twitter.action.streaming.TweetStreamingListener;
import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StatusStream;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserStreamAdapter;
import twitter4j.auth.AccessToken;

/**
 * 指定したキーワードをサーチする際に利用
 * @author nishio
 *
 */
public class TweetSearchStream extends StatusAdapter implements Runnable{
	//streaming
	private TwitterStream twitterStream = null;
	//Filter query
	private FilterQuery filter = null;
	//status stream
	private StatusStream statusStream = null;
	//Thread
	private Thread workingThread = null;
	//tweet manager
	private TweetManager tweetManager = null;
	//検索ワードに対応したリスナー
	private Map<String, TweetStreamingListener> listeners = null;
	//指定したユーザに対応したリスナー
	private Map<Long, TweetStreamingListener> userListener = null;
	//指定したワードの最終更新id
	private Map<String, Long> lastUpdate = null;
	//指定したユーザの最終更新id
	private Map<Long, Long> userLastUpdate = null;

	/**
	 *
	 * @param consumerKey
	 * @param consumerSecret
	 * @param ac アクセストークン
	 * @param tweetManager
	 */
	public TweetSearchStream(String consumerKey, String consumerSecret, AccessToken ac, TweetManager tweetManager) {
		this.tweetManager = tweetManager;
		this.twitterStream = new TwitterStreamFactory().getInstance();
		this.twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
		this.twitterStream.setOAuthAccessToken(ac);

		filter = new FilterQuery();
		listeners = new HashMap<String, TweetStreamingListener>();
		userListener = new HashMap<Long, TweetStreamingListener>();
		lastUpdate = new HashMap<String, Long>();
		userLastUpdate = new HashMap<Long, Long>();
	}

	/**
	 * 指定した単語を検索対象に加える
	 * @param word
	 * @param listener
	 */
	public void addSearchWord(String word, TweetStreamingListener listener) {
		listeners.put(word, listener);
		updateFilter();
	}

	/**
	 * 指定した単語を検索対象から外す
	 * @param word
	 */
	public void removeSearchWord(String word) {
		listeners.remove(word);
		updateFilter();
	}

	/**
	 * 指定したユーザを検索対象に加える
	 * @param userid
	 * @param listener
	 */
	public void addSearchUser(Long userid, TweetStreamingListener listener) {
		userListener.put(userid, listener);
		updateFilter();
	}

	/**
	 * 指定したユーザを検索対象から外す
	 * @param userid
	 */
	public void removeSearchUser(Long userid) {
		userListener.remove(userid);
		updateFilter();
	}

	/**
	 * filterの更新
	 */
	private void updateFilter() {
		//指定したユーザの情報を取得するようにする
		Long[] users = userListener.keySet().toArray(new Long[0]);
		if( users != null ) {
			long[] usersLong = new long[users.length];
			for(int i=0; i < users.length; i++) {
				usersLong[i] = users[i];
			}
			filter.follow(usersLong);
		}
		//指定したワードの情報を取得するようにする
		String[] words = listeners.keySet().toArray(new String[0]);
		filter.track(words);
		try {
			if( statusStream != null ) {
				statusStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		workingThread = new Thread(this);
		workingThread.start();
	}

	/**
	 *指定したワードに対応するステータスを取得
	 */
	@Override
	public void onStatus(Status status) {
		//検索ワード情報振り分け
		Set<String> keys = listeners.keySet();
		synchronized (listeners) {
			for(String word : keys) {
				if( status.getText().contains( word.toString() ) ) {
					TweetStreamingListener listener = listeners.get(word);
					listener.update(status);
					//最終更新id
					lastUpdate.put(word, status.getId());
				}
			}
		}

		//ユーザ情報振り分け
		Set<Long> userKeys = userListener.keySet();
		synchronized(userListener) {
			for(long id : userKeys) {
				if( status.getUser().getId() == id ) {
					TweetStreamingListener listener = userListener.get(id);
					listener.update(status);
					//最終更新id
					userLastUpdate.put(id, status.getId());
				}
			}
		}
	}

	/**
	 * 最終更新ステータスのidの取得
	 * @param word
	 * @return
	 */
	public long getLastUpdateID(String word) {
		Long id = lastUpdate.get(word);
		if( id == null ) {
			return 0;
		}
		return id;
	}

	/**
	 * ユーザの最終更新ステータスidの取得
	 * @param userid
	 * @return
	 */
	public long getUserLastUpdateID(long userid) {
		Long id = userLastUpdate.get(userid);
		if( id == null ) {
			return 0;
		}
		return id;
	}

	/**
	 *
	 */
	@Override
	public void onException(Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void run() {
		try {
			statusStream = twitterStream.getFilterStream(filter);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		for(; statusStream != null; ) {
			try {
				statusStream.next(this);
			}catch(Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
