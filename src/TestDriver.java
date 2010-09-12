import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXParseException;

import twitter.log.TwitterLogManager;
import twitter.manage.TweetManager;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class TestDriver {
	private static final String CONSUMER_KEY = "tbo5erit2M2ZzN6n8tEYcA";
    private static final String CONSUMER_SECRET = "tODurbdySLYU1pKjtB3MQTDRBGy562dHzVf7d62mm8";

    /***
     * ユーザIDとパスワードで認証する。
     *
     * @param userId
     *            ユーザID
     * @param password
     *            パスワード
     * @return Twitterクラスのインスタンス<BR>
     *         認証に失敗した場合はnull
     */
    public static Twitter getInstance(String userId, String password) {
        Configuration conf = getConfiguration();

        TwitterFactory twitterfactory = new TwitterFactory(conf);
        Twitter twitter = twitterfactory.getInstance(userId, password);

        try {
            twitter.getOAuthAccessToken();
        } catch (TwitterException e) {
            return null;
        }

        return twitter;
    }

    /***
     * アクセストークンを使って認証する
     *
     * @param accessToken
     *            アクセストークン
     * @return Twitterクラスのインスタンス<BR>
     *         認証に失敗した場合はnull
     */
    public synchronized static Twitter getOAuthAuthorizedInstance(String token, String token_secret) {
        Configuration conf = getConfiguration();
        TwitterFactory twitterfactory = new TwitterFactory(conf);
        Twitter twitter = twitterfactory.getOAuthAuthorizedInstance(new AccessToken(token, token_secret));

        try {
            twitter.getId();
        } catch (TwitterException e) {
            return null;
        }

        return twitter;
    }

    /***
     * Configurationを生成する
     *
     * @return
     */
    private static Configuration getConfiguration() {
        ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        confbuilder.setOAuthConsumerKey(CONSUMER_KEY);
        confbuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
        return confbuilder.build();
    }


	public static void main(String[] args) {
		TweetManager tweetManager = new TweetManager();
        try {
            tweetManager.loginTwitter();
            List<Status> status = tweetManager.getNewSearchResult(24303121371L, "tvk");
            for( Status s : status ) {
                System.out.println( s );
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestDriver.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(TestDriver.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
	}
}
