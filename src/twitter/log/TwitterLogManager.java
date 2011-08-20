package twitter.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXParseException;

import au.com.bytecode.opencsv.CSVWriter;

import twitter.manage.StatusXMLConverter;
import twitter.manage.TweetConfiguration;
import twitter.util.CurrentTime;
import twitter4j.Status;

public class TwitterLogManager {

	// ログを保存するディレクトリ名
	private static final String LOG_DIRECTORY = TweetConfiguration.LOG_DIRECTORY;
	// タイムラインを保存するディレクトリ名
	private static final String TIMELINE_DIRECTORY = TweetConfiguration.TIMELINE_DIRECTORY;
	// 文字コード
	private static final String CHARACTER_ENCODING = TweetConfiguration.CHARACTER_ENCODING;

	/**
	 *
	 */
	public TwitterLogManager() {

	}

	/**
	 * ログをファイルに保存する
	 *
	 * @param statuses
	 */
	public void add(List<Status> statuses) throws IOException {
		// ログディレクトリを作成
		File logDir = new File("./" + LOG_DIRECTORY);
		if (!logDir.exists()) {
			// ディレクトリが存在しないので作成する
			if (logDir.mkdir() == false) {
				throw new IOException(LOG_DIRECTORY + "ディレクトリを作成できませんでした．");
			}
		}
		//sql関係
		TwitterLogDao dao = new TwitterLogDao();
		try {
			//DB作成
			dao.connectDB();
			dao.createTable();
			//long time = System.currentTimeMillis();
			//DBにツイート保存
			List<TweetDBObject> objects = new ArrayList<TweetDBObject>();
			for(Status s : statuses ) {
				TweetDBObject o = StatusDBObjectConverter.convertStatusToDBObject(s);
				objects.add(o);
			}
			if( objects != null && objects.size() > 0 ) {
				dao.insert( objects );
			}
			/*time = System.currentTimeMillis() - time;
			System.out.println("DB INSERT TIME:" + time);*/
			dao.closeDB();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ログ全データ取得
	 * @return
	 */
	public List<Status> get() {
		TwitterLogDao dao = new TwitterLogDao();
		List<Status> tweet = new ArrayList<Status>();
		List<TweetDBObject> obj = null;
		try {
			obj = dao.get();
			if( obj != null ) {
				for( TweetDBObject o : obj ) {
					Status s = StatusDBObjectConverter.convertDBObjectToStatus(o);
					if( s != null ) {
						tweet.add(s);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return tweet;
	}

	/**
	 * 指定した日時のtweet取得
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public List<Status> get(int year, int month, int day) {
		//TODO: 日時指定をしてDBからデータ取得を行うようにする
		return get();
	}

	/**
	 * logをcsvとして保存
	 * @param filepath
	 * @param status
	 * @param showUsername
	 * @param showScreenName
	 * @param showText
	 * @param showUpdateTime
	 * @param showClient
	 * @param showUserDescription
	 * @param showFollowing
	 * @param showFollower
	 * @param showUpdateCount
	 * @param showUserURL
	 * @param showProfileImageURL
	 * @throws IOException
	 */
	public void outputCSVLog(String filepath, List<Status> status,
			boolean showUsername, boolean showScreenName,
			boolean showText,
			boolean showUpdateTime, boolean showClient,
			boolean showUserDescription,
			boolean showFollowing, boolean showFollower,
			boolean showUpdateCount, boolean showUserURL,
			boolean showProfileImageURL) throws IOException {
		// CSV保存
		CSVWriter writer = new CSVWriter(new FileWriter( filepath ));

		int col = 0;
		String[] title = new String[11];

		if( showUsername ) {
			title[col++] = "Username";
		}
		if( showScreenName ) {
			title[col++] = "ScreenName";
		}
		if( showText ) {
			title[col++] = "Tweet";
		}
		if( showUpdateTime ) {
			title[col++] = "UpdateTime";
		}
		if( showClient ) {
			title[col++] = "Source";
		}
		if( showUserDescription ) {
			title[col++] = "UserDescription";
		}
		if( showFollowing ) {
			title[col++] = "Following";
		}
		if( showFollower ) {
			title[col++] = "Follower";
		}
		if( showUpdateCount ) {
			title[col++] = "UpdateCount";
		}
		if( showUserURL ) {
			title[col++] = "URL";
		}
		if( showProfileImageURL ) {
			title[col++] = "UserImageURL";
		}
		//タイトル書き込み
		writer.writeNext(title);

		for (Status s : status) {
			//retweetの場合、retween者の情報を保存
			/*if (s.isRetweet()) {
				s = s.getRetweetedStatus();
			}*/
			String username = null;
			String screenName = null;
			int following = 0;
			int follower = 0;
			int updateCount = 0;
			String userDescription = null;
			String userURL = null;
			String profileImageURL = null;

			if( s.getUser() != null ) {
				username = s.getUser().getName();
				// screen名
				screenName = s.getUser().getScreenName();
				// ユーザフォロー数
				following = s.getUser().getFriendsCount();
				// ユーザフォロワー数
				follower  = s.getUser().getFollowersCount();
				// 更新数
				updateCount = s.getUser().getStatusesCount();
				// ユーザ紹介文
				userDescription = s.getUser().getDescription();
				// ユーザのURL
				userURL = "";
				try {
					userURL = s.getUser().getURL().toString();
				}catch(Exception e) {}
				// ユーザのprofile imageのURL
				profileImageURL = s.getUser().getProfileImageURL()
						.toString();
			}
			// メッセージ
			String text = s.getText();
			// 更新日
			String updateTime = DateFormat.getInstance().format(
					s.getCreatedAt());
			// ユーザが利用しているクライアント
			String client = s.getSource();
			//書きこむデータ
			col = 0;
			String[] data = new String[11];

			if( showUsername ) {
				data[col++] = username;
			}
			if( showScreenName ) {
				data[col++] = screenName;
			}
			if( showText ) {
				data[col++] = text;
			}
			if( showUpdateTime ) {
				data[col++] = updateTime;
			}
			if( showClient ) {
				data[col++] = client;
			}
			if( showUserDescription ) {
				data[col++] = userDescription;
			}
			if( showFollowing ) {
				data[col++] = following + "";
			}
			if( showFollower ) {
				data[col++] = follower + "";
			}
			if( showUpdateCount ) {
				data[col++] = updateCount + "";
			}
			if( showUserURL ) {
				data[col++] = userURL;
			}
			if( showProfileImageURL ) {
				data[col++] = profileImageURL;
			}
			writer.writeNext(data);
		}
		writer.close();
	}
}
