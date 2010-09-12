package twitter.manage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


import twitter.log.TwitterLogManager;
import twitter4j.DirectMessage;
import twitter4j.GeoLocation;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.AccessToken;

public class TweetManager {

    /**
     * DirectMessageのユーザ情報等からステータスを生成
     *
     * @author nishio
     *
     */
    private class DirectMessageUserStatus implements Status {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        // 受信したdirectMessageの情報
        private DirectMessage message = null;
        // ユーザ情報
        private User user = null;

        public DirectMessageUserStatus(DirectMessage directMessage, User user) {
            this.message = directMessage;
            this.user = user;
        }

        @Override
        public int compareTo(Status o) {
            return 0;
        }

        @Override
        public String[] getContributors() {
            return null;
        }

        @Override
        public Date getCreatedAt() {
            return message.getCreatedAt();
        }

        @Override
        public GeoLocation getGeoLocation() {
            return null;
        }

        @Override
        public long getId() {
            return user.getId();
        }

        @Override
        public String getInReplyToScreenName() {
            return user.getStatusInReplyToScreenName();
        }

        @Override
        public long getInReplyToStatusId() {
            return user.getStatusInReplyToUserId();
        }

        @Override
        public int getInReplyToUserId() {
            return 0;
        }

        @Override
        public Place getPlace() {
            return null;
        }

        @Override
        public RateLimitStatus getRateLimitStatus() {
            return user.getRateLimitStatus();
        }

        @Override
        public Status getRetweetedStatus() {
            return null;
        }

        @Override
        public String getSource() {
            return "Unknown";
        }

        @Override
        public String getText() {
            return message.getText();
        }

        @Override
        public User getUser() {
            return user;
        }

        @Override
        public boolean isFavorited() {
            return user.isStatusFavorited();
        }

        @Override
        public boolean isRetweet() {
            return false;
        }

        @Override
        public boolean isTruncated() {
            return false;
        }
    }
    private Twitter twitter = null;
    // クライアントのConsumer Key
    private static final String CONSUMER_KEY = "tbo5erit2M2ZzN6n8tEYcA";
    // クライアントのConsumer Secret 外部に漏れてはいけない
    private static final String CONSUMER_SECRET = "tODurbdySLYU1pKjtB3MQTDRBGy562dHzVf7d62mm8";
    // accessToken
    private String accessToken = "";
    // secretToken 外部に漏れてはいけない
    private String secretToken = "";
    // 設定ファイル保存ディレクトリ名
    private static final String PROPERTIES_DIRECTORY = TweetConfiguration.PROPERTIES_DIRECTORY;
    // 設定ファイル保存ファイル名
    private static final String TWEET_INFO_FILENAME = TweetConfiguration.TWEET_INFO_FILENAME;
    //アカウント情報保存ファイル名
    private static final String ACCOUNT_INFORMATION_FILENAME = TweetConfiguration.ACCOUNT_INFORMATION_FILENAME;
    // 一度に取得する最大のtweet数
    private final int MAX_TWEET_NUM = 99;
    // 前回取得したtweetの情報
    private long sinceTweetID = 0;
    // 前回取得したmentionのtweetID
    private long sinceMentionID = 0;
    // 前回取得したsendDirectMessageの情報
    private long sinceSendDirectMessageID = 0;
    // 前回取得したdirectMessageの情報
    private long sinceDirectMessageID = 0;
    // 設定ファイル
    private Properties property = null;
    //アカウント設定ファイル
    private Properties accountProperty = null;
    // ログ保存
    private TwitterLogManager logManager = null;

    public TweetManager() {
        logManager = new TwitterLogManager();
    }

    /**
     * 設定ファイルを読み込む
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void loadProperties() throws FileNotFoundException, IOException {
        if (property == null) {
            this.property = new Properties();
        }
        property.load(new FileInputStream("./" + PROPERTIES_DIRECTORY + "/"
                + TWEET_INFO_FILENAME));
        // 設定読み込み
        String sti = this.property.getProperty("sinceTweetID");
        /*String smi = this.property.getProperty("sinceMentionID");
        String ssdmi = this.property.getProperty("sinceSendDirectMessageID");
        String sdmi = this.property.getProperty("sinceDirectMessageID");*/

        try {
            this.sinceTweetID = Long.parseLong(sti);
            /*this.sinceMentionID = Long.parseLong(smi);
            this.sinceSendDirectMessageID = Long.parseLong(ssdmi);
            this.sinceDirectMessageID = Long.parseLong(sdmi);*/
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 設定ファイルを保存する
     *
     * @throws IOException
     */
    public void saveProperties() throws IOException {
        // 設定ファイルディレクトリを作成
        File logDir = new File("./" + PROPERTIES_DIRECTORY);
        if (!logDir.exists()) {
            // ディレクトリが存在しないので作成する
            if (logDir.mkdir() == false) {
                throw new IOException(PROPERTIES_DIRECTORY
                        + "ディレクトリを作成できませんでした．");
            }
        }
        if (property == null) {
            this.property = new Properties();
        }
        // since idを保存
        this.property.setProperty("sinceTweetID", sinceTweetID + "");
        /*this.property.setProperty("sinceMentionID", sinceMentionID + "");
        this.property.setProperty("sinceSendDirectMessageID",
                sinceSendDirectMessageID + "");
        this.property.setProperty("sinceDirectMessageID", sinceDirectMessageID
                + "");*/
        // プロパティのリストを保存
        property.store(new FileOutputStream("./" + PROPERTIES_DIRECTORY + "/"
                + TWEET_INFO_FILENAME), null);
    }

    /**
     * アカウント情報を読み込む
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void loadAccountProperties() throws FileNotFoundException, IOException {
        if (accountProperty == null) {
            this.accountProperty = new Properties();
        }
        accountProperty.load(new FileInputStream("./" + PROPERTIES_DIRECTORY + "/"
                + ACCOUNT_INFORMATION_FILENAME));
        // 設定読み込み
        this.accessToken = this.accountProperty.getProperty("accessToken");
        this.secretToken = this.accountProperty.getProperty("secretToken");
    }

    /**
     * アカウント情報を保存する
     * @throws IOException
     */
    public void saveAccountProperties() throws IOException {
        // 設定ファイルディレクトリを作成
        File logDir = new File("./" + PROPERTIES_DIRECTORY);
        if (!logDir.exists()) {
            // ディレクトリが存在しないので作成する
            if (logDir.mkdir() == false) {
                throw new IOException(PROPERTIES_DIRECTORY
                        + "ディレクトリを作成できませんでした．");
            }
        }
        if (accountProperty == null) {
            this.accountProperty = new Properties();
        }
        // since idを保存
        this.accountProperty.setProperty("accessToken", this.accessToken);
        this.accountProperty.setProperty("secretToken", this.secretToken);
        // プロパティのリストを保存
        accountProperty.store(new FileOutputStream("./" + PROPERTIES_DIRECTORY + "/"
                + ACCOUNT_INFORMATION_FILENAME), null);
    }

    /**
     * 受信したダイレクトメッセージを取得
     *
     * @param num
     * @return
     * @throws TwitterException
     */
    public List<Status> getDirectMessages(int num) throws TwitterException {
        List<DirectMessage> directMessages = twitter.getDirectMessages(new Paging(1, num));
        // Tweetの一覧
        List<Status> messageList = new LinkedList<Status>();

        if (directMessages != null && directMessages.size() > 0) {
            // tweetIDを保存しておく
            this.sinceDirectMessageID = directMessages.get(0).getId();
            // 一番新しいtweetを一番したに持ってくる
            for (DirectMessage message : directMessages) {
                User user = message.getSender();
                DirectMessageUserStatus status = new DirectMessageUserStatus(message, user);
                messageList.add(0, status);
            }
        }

        try {
            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    /**
     * 送信したダイレクトメッセージを取得
     *
     * @param num
     * @return
     * @throws TwitterException
     */
    public List<Status> getSendDirectMessages(int num) throws TwitterException {
        List<DirectMessage> directMessages = twitter.getSentDirectMessages(new Paging(1, num));
        // Tweetの一覧
        List<Status> messageList = new LinkedList<Status>();
        if (directMessages != null && directMessages.size() > 0) {
            // tweetIDを保存しておく
            this.sinceSendDirectMessageID = directMessages.get(0).getId();
            // 一番新しいtweetを一番したに持ってくる
            for (DirectMessage message : directMessages) {
                User user = message.getRecipient();
                DirectMessageUserStatus status = new DirectMessageUserStatus(message, user);
                messageList.add(0, status);
            }
        }
        try {
            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    /**
     * Mentionの取得
     *
     * @param num
     * @return
     * @throws TwitterException
     */
    public List<Status> getMentions(int num) throws TwitterException {
        List<Status> statuses = twitter.getMentions(new Paging(1, num));
        // Tweetの一覧
        List<Status> tweetList = new LinkedList<Status>();
        if (statuses != null && statuses.size() > 0) {
            // tweetIDを保存しておく
            this.sinceMentionID = statuses.get(0).getId();
            // 一番新しいtweetを一番したに持ってくる
            for (Status status : statuses) {
                tweetList.add(0, status);
            }
        }
        try {
            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweetList;
    }

    /**
     * まだ取得していないDirectMessageを取得
     *
     * @return
     * @throws TwitterException
     */
    public List<Status> getNewDirectMessages() throws TwitterException {
        if (this.sinceDirectMessageID == 0) {
            return getDirectMessages(MAX_TWEET_NUM);
        }
        List<DirectMessage> directMessages = twitter.getDirectMessages(new Paging(sinceDirectMessageID));
        // もし20件データ取得したらもっとデータがある可能性がある
        if (directMessages.size() >= 20) {
            List<DirectMessage> ndata = twitter.getDirectMessages(new Paging(1,
                    MAX_TWEET_NUM));
            ndata = ndata.subList(21, ndata.size());
            for (DirectMessage s : ndata) {
                // すでに取得したことのあるデータだったとき
                if (s.getId() <= sinceDirectMessageID) {
                    break;
                }
                directMessages.add(s);
            }
        }
        // Tweetの一覧
        List<Status> messageList = new LinkedList<Status>();
        if (directMessages != null && directMessages.size() > 0) {
            // tweetIDを保存しておく
            this.sinceDirectMessageID = directMessages.get(0).getId();
            // 一番新しいtweetを一番したに持ってくる
            for (DirectMessage message : directMessages) {
                Status status = message.getSender().getStatus();
                messageList.add(0, status);
            }
        }
        try {
            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    /**
     * まだ取得していないSendDirectMessageを取得
     *
     * @return
     * @throws TwitterException
     */
    public List<Status> getNewSendDirectMessages() throws TwitterException {
        if (this.sinceSendDirectMessageID == 0) {
            return getSendDirectMessages(MAX_TWEET_NUM);
        }
        List<DirectMessage> directMessages = twitter.getDirectMessages(new Paging(sinceSendDirectMessageID));
        // もし20件データ取得したらもっとデータがある可能性がある
        if (directMessages.size() >= 20) {
            List<DirectMessage> ndata = twitter.getSentDirectMessages(new Paging(1, MAX_TWEET_NUM));
            ndata = ndata.subList(21, ndata.size());
            for (DirectMessage s : ndata) {
                // すでに取得したことのあるデータだったとき
                if (s.getId() <= sinceSendDirectMessageID) {
                    break;
                }
                directMessages.add(s);
            }
        }
        // Tweetの一覧
        List<Status> messageList = new LinkedList<Status>();
        if (directMessages != null && directMessages.size() > 0) {
            // tweetIDを保存しておく
            this.sinceSendDirectMessageID = directMessages.get(0).getId();
            // 一番新しいtweetを一番したに持ってくる
            for (DirectMessage message : directMessages) {
                Status status = message.getRecipient().getStatus();
                messageList.add(0, status);
            }
        }
        try {
            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    /**
     * まだ取得していないMention情報を取得
     *
     * @return
     * @throws TwitterException
     */
    public List<Status> getNewMentionData() throws TwitterException {
        // 一度もデータを取得していないとき
        if (sinceMentionID == 0) {
            return getMentions(MAX_TWEET_NUM);
        }
        // getFriendsTimelineではRetweetは表示されないので注意
        List<Status> statuses = twitter.getMentions(new Paging(sinceMentionID));
        // もし20件データ取得したらもっとデータがある可能性がある
        if (statuses.size() >= 20) {
            List<Status> ndata = twitter.getMentions(new Paging(1,
                    MAX_TWEET_NUM));
            ndata = ndata.subList(21, ndata.size());
            for (Status s : ndata) {
                // すでに取得したことのあるデータだったとき
                if (s.getId() <= sinceMentionID) {
                    break;
                }
                statuses.add(s);
            }
        }

        // Tweetの一覧
        List<Status> tweetList = new LinkedList<Status>();
        if (statuses != null && statuses.size() > 0) {
            // tweetIDを保存しておく
            sinceMentionID = statuses.get(0).getId();

            // 一番新しいtweetを一番したに持ってくる
            for (Status status : statuses) {
                tweetList.add(0, status);
            }
        }
        try {
            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweetList;
    }

    /**
     * まだ取得していないtweet情報を取得
     *
     * @return
     * @throws TwitterException
     */
    public List<Status> getNewTimelineData() throws TwitterException {
        // 一度もデータを取得していないとき
        if (sinceTweetID == 0) {
            return getTimeline(MAX_TWEET_NUM);
        }
        // getFriendsTimelineではRetweetは表示されないので注意
        List<Status> statuses = twitter.getHomeTimeline(new Paging(sinceTweetID));
        // もし20件データ取得したらもっとデータがある可能性がある
        if (statuses.size() >= 20) {
            List<Status> ndata = twitter.getHomeTimeline(new Paging(1,
                    MAX_TWEET_NUM));
            ndata = ndata.subList(21, ndata.size());
            for (Status s : ndata) {
                // すでに取得したことのあるデータだったとき
                if (s.getId() <= sinceTweetID) {
                    break;
                }
                statuses.add(s);
            }
        }
        // Tweetの一覧
        List<Status> tweetList = new LinkedList<Status>();
        if (statuses != null && statuses.size() > 0) {
            // tweetIDを保存しておく
            sinceTweetID = statuses.get(0).getId();

            // 一番新しいtweetを一番したに持ってくる
            for (Status status : statuses) {
                tweetList.add(0, status);
            }
        }
        // ログ保存
        try {
            for (Status s : tweetList) {
                logManager.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 設定ファイル保存
        try {
            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweetList;
    }

    /**
     * タイムラインを取得 一番新しいツイートは要素の一番下に追加
     *
     * @param num
     *            指定した数だけtweetを取得
     * @return
     * @throws TwitterException
     */
    public List<Status> getTimeline(int num) throws TwitterException {
        // getFriendsTimelineではRetweetは表示されないので注意
        List<Status> statuses = twitter.getHomeTimeline(new Paging(1, num));
        // Tweetの一覧
        List<Status> tweetList = new LinkedList<Status>();
        if (statuses != null && statuses.size() > 0) {
            // tweetIDを保存しておく
            sinceTweetID = statuses.get(0).getId();

            // 一番新しいtweetを一番したに持ってくる
            for (Status status : statuses) {
                tweetList.add(0, status);
            }
        }
        // ログ保存
        try {
            for (Status s : tweetList) {
                logManager.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweetList;
    }

    /**
     * 指定したワードを含むtweetを返す
     * @param num 指定した数だけtweetを取得
     * @param searchWord 検索したい単語
     * @return
     */
    public List<Status> getSearchResult(int num, String searchWord) {
        Query query = new Query(searchWord);
        //取得するツイート最大数
        query.setRpp(this.MAX_TWEET_NUM);
        //取得するページ番号
        query.setPage(1);
        //検索結果
        QueryResult queryResult = null;
        try {
            queryResult = twitter.search(query);
        } catch (TwitterException ex) {
            Logger.getLogger(TweetManager.class.getName()).log(
                    Level.SEVERE, "Twitter searchに失敗しました", ex);
            ex.printStackTrace();
        }

        if( queryResult != null ) {
            for(Tweet tweet: queryResult.getTweets() ) {
                System.out.println( tweet );
            }
        }
        return null;
    }

    /**
     * Twitterへログイン
     */
    public void loginTwitter() throws FileNotFoundException, IOException {
        twitter = new TwitterFactory().getInstance();
        //アカウント情報を読み込む
        loadAccountProperties();
        // ConsumerKeyなどを設定
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        // ここにユーザのアクセストークンを入れる
        AccessToken ac = new AccessToken(accessToken, secretToken);
        twitter.setOAuthAccessToken(ac);
        // 設定ファイルを読み込む
        try {
            loadProperties();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configurationを生成する
     *
     * @return
     */
    private Configuration getTwitterConfiguration() {
        ConfigurationBuilder confbuilder = new ConfigurationBuilder();
        confbuilder.setOAuthConsumerKey(CONSUMER_KEY);
        confbuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
        return confbuilder.build();
    }


    /**
     * 初回時アクセストークンを取得する際に利用する
     * @param username
     * @param password
     * @throws TwitterException
     */
    public void getAccessToken(String username, String password) throws TwitterException {
        //consumer key secretを設定
        Configuration conf = this.getTwitterConfiguration();
        twitter = new TwitterFactory(conf).getInstance(username, password);
        //access token取得
        AccessToken oAuthAccessToken = twitter.getOAuthAccessToken();
        this.accessToken = oAuthAccessToken.getToken();
        this.secretToken = oAuthAccessToken.getTokenSecret();
        
        try {
            //取得したOAuthのトークンを保存
            saveAccountProperties();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * メッセージをつぶやく
     *
     * @param message
     */
    public void tweet(String message) {
        twitter4j.Status status;
        try {
            status = twitter.updateStatus(message);
        } catch (TwitterException e) {
            // TODO: エラー処理
            e.printStackTrace();
        }
    }

    /**
     * ダイレクトメッセージを送信
     *
     * @param screenName
     * @param text
     * @throws TwitterException
     */
    public void sendDirectMessage(String screenName, String text)
            throws TwitterException {
        twitter.sendDirectMessage(screenName, text);
    }

    /**
     * 指定したステータスIDの発言をRetweet
     *
     * @param statusID
     * @throws TwitterException
     */
    public void retweet(long statusID) throws TwitterException {
        twitter.retweetStatus(statusID);
    }

    /**
     * API制限数を取得
     *
     * @return
     * @throws TwitterException
     */
    public RateLimitStatus getRateLimitStatus() throws TwitterException {
        return twitter.getRateLimitStatus();
    }
}
