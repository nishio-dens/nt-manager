package twitter.manage;

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

	/**
	 *
	 * @param consumerKey
	 * @param consumerSecret
	 * @param ac
	 */
	public TweetUserStreamManager(String consumerKey, String consumerSecret, AccessToken ac) {
		try {
			userStream = new TweetUserStream(consumerKey, consumerSecret, ac);
			searchStream = new TweetSearchStream(consumerKey, consumerSecret, ac);
			searchStream.addSearchWord("#anime");
			/*searchStream.addSearchWord("#manga");
			searchStream.addSearchWord("#1kari");*/
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
