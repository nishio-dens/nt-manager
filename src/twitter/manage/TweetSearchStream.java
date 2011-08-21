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
	//登録されている検索ワード
	private Set<String> filterWords = null;
	//status stream
	private StatusStream statusStream = null;
	//Thread
	private Thread workingThread = null;
	//tweet manager
	private TweetManager tweetManager = null;
	//検索ワードに対応したリスナー
	private Map<String, TweetStreamingListener> listeners = null;

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
		filterWords = new HashSet<String>();
		listeners = new HashMap<String, TweetStreamingListener>();
	}

	/**
	 * 指定した単語を検索対象に加える
	 * @param word
	 */
	public void addSearchWord(String word, TweetStreamingListener listener) {
		filterWords.add(word);
		listeners.put(word, listener);
		updateFilter();
	}

	/**
	 * 指定した単語を検索対象から外す
	 * @param word
	 */
	public void removeSearchWord(String word) {
		filterWords.remove(word);
		listeners.remove(word);
		updateFilter();
	}

	/**
	 * filterの更新
	 */
	private void updateFilter() {
		String[] words = filterWords.toArray(new String[0]);
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
		for(String word : listeners.keySet()) {
			if( status.getText().contains( word.toString() ) ) {
				TweetStreamingListener listener = listeners.get(word);
				listener.update(status);
			}
		}
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
		for(;;) {
			try {
				if( statusStream != null ) {
					statusStream.next(this);
				}
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}
	}

}
