package twitter.manage;

/**
 * 設定を保存しておく
 *
 * @author nishio
 *
 */
public class TweetConfiguration {
	// ログを保存するディレクトリ名
	public static final String LOG_DIRECTORY = "log";
	// キャッシュデータを保存するディレクトリ名
	public static final String CACHE_DIRECTORY = "cache";
	// タイムラインを保存するディレクトリ名
	public static final String TIMELINE_DIRECTORY = "timeline";
	// 文字コード
	public static final String CHARACTER_ENCODING = "utf-8";
	// Tweet更新間隔[sec]
	public static final long TWEET_UPDATE_PERIOD = 60;
	// 設定ファイルを保存するディレクトリ名
	public static final String PROPERTIES_DIRECTORY = "properties";
	// 取得したtweetidなどを保存するファイル名
	public static final String TWEET_INFO_FILENAME = "previnfo.properties";
	// 基本設定を保存するファイル名
	public static final String BASIC_SETTING_FILENAME = "setting.properties";
	// アカウント情報を保存するファイル名
	public static final String ACCOUNT_INFORMATION_FILENAME = "account.properties";
	// search twitterのURL
	public static final String SEARCH_TWITTER_HOSTNAME = "search.twitter.com";
	//データベース接続文字列
	public static final String DATABASE = "org.sqlite.JDBC";
	//ツイートを保存するデータベース名
	public static final String DATABASE_CONNECTION = "jdbc:sqlite:" + LOG_DIRECTORY + "/tweet.db";
	//ハッシュタグ認識のパターン
	//Thanks Real Beatさん。http://nobu666.com/2011/07/13/914.html
	public static final String HASHTAG_PATTERN =
		//"(?:#|＃)([\\p{InBasicLatin}_\\p{InHiragana}\\p{InKatakana}\\p{InCJKUnifiedIdeographs}]+)";
		"(?:#|\uFF03)([a-zA-Z0-9_\u3041-\u3094\u3099-\u309C\u30A1-\u30FA\u3400-\uD7FF\uFF10-\uFF19\uFF20-\uFF3A\uFF41-\uFF5A\uFF66-\uFF9E]+)";
}
