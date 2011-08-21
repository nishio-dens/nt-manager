package twitter.manage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
	}

	/**
	 * 指定した単語を検索対象に加える
	 * @param word
	 */
	public void addSearchWord(String word) {
		filterWords.add(word);
		updateFilter();
	}

	/**
	 * 指定した単語を検索対象から外す
	 * @param word
	 */
	public void removeSearchWord(String word) {
		filterWords.remove(word);
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
	 *
	 */
	@Override
	public void onStatus(Status status) {
		System.out.println("Search Message -- @" + status.getUser().getScreenName() + " - "
				+ status.getText());
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
