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
}
