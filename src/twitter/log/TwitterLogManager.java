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
	 * @param s
	 * @throws IOException
	 */
	public void add(Status s) throws IOException {
		// ログディレクトリを作成
		File logDir = new File("./" + LOG_DIRECTORY);
		if (!logDir.exists()) {
			// ディレクトリが存在しないので作成する
			if (logDir.mkdir() == false) {
				throw new IOException(LOG_DIRECTORY + "ディレクトリを作成できませんでした．");
			}
		}
		// タイムライン保存用ディレクトリを作成
		String timelineDirName = "./" + LOG_DIRECTORY + "/"
				+ TIMELINE_DIRECTORY;
		File timelineDir = new File(timelineDirName);
		if (!timelineDir.exists()) {
			// ディレクトリが存在しないので作成する
			if (timelineDir.mkdir() == false) {
				throw new IOException(TIMELINE_DIRECTORY + "ディレクトリを作成できませんでした．");
			}
		}

		// 保存するデータのファイル名は年_月_日.log
		String filename = timelineDirName + "/" + CurrentTime.getCurrentYear()
				+ "_" + CurrentTime.getCurrentMonth() + "_"
				+ CurrentTime.getCurrentDay() + ".log";

		FileOutputStream fos = new FileOutputStream(filename, true);
		OutputStreamWriter osw = new OutputStreamWriter(fos, CHARACTER_ENCODING);
		BufferedWriter bw = new BufferedWriter(osw);

		// ファイル書き込みデータ
		StringBuffer writeData = new StringBuffer("");
		writeData.append(StatusXMLConverter.convertStatusToXML(s) + "\n");

		// ファイル書き込み
		bw.write(writeData.toString());
		// ファイルを閉じる
		bw.close();
		osw.close();
		fos.close();
	}

	/**
	 * ログをファイルに保存する
	 * 
	 * @param statuses
	 */
	/*public void add(List<Status> statuses) throws IOException {
		// ログディレクトリを作成
		File logDir = new File("./" + LOG_DIRECTORY);
		if (!logDir.exists()) {
			// ディレクトリが存在しないので作成する
			if (logDir.mkdir() == false) {
				throw new IOException(LOG_DIRECTORY + "ディレクトリを作成できませんでした．");
			}
		}
		// タイムライン保存用ディレクトリを作成
		String timelineDirName = "./" + LOG_DIRECTORY + "/"
				+ TIMELINE_DIRECTORY;
		File timelineDir = new File(timelineDirName);
		if (!timelineDir.exists()) {
			// ディレクトリが存在しないので作成する
			if (timelineDir.mkdir() == false) {
				throw new IOException(TIMELINE_DIRECTORY + "ディレクトリを作成できませんでした．");
			}
		}

		// 保存するデータのファイル名は年_月_日.log
		String filename = timelineDirName + "/" + CurrentTime.getCurrentYear()
				+ "_" + CurrentTime.getCurrentMonth() + "_"
				+ CurrentTime.getCurrentDay() + ".log";

		FileOutputStream fos = new FileOutputStream(filename, true);
		OutputStreamWriter osw = new OutputStreamWriter(fos, CHARACTER_ENCODING);
		BufferedWriter bw = new BufferedWriter(osw);

		// ファイル書き込みデータ
		StringBuffer writeData = new StringBuffer("");
		for (Status s : statuses) {
			writeData.append(StatusXMLConverter.convertStatusToXML(s) + "\n");
		}

		// ファイル書き込み
		bw.write(writeData.toString());
		// ファイルを閉じる
		bw.close();
		osw.close();
		fos.close();
	}*/
	
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
			dao.connectDB();
			dao.createTable();
			for(Status s : statuses ) {
				TweetDBObject o = StatusDBObjectConverter.convertStatusToDBObject(s, true);
				dao.insert( o );
			}
			dao.closeDB();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定した件数分のTweet情報を取得する
	 * 
	 * @param num
	 * @param year
	 * @param month
	 * @param day
	 * @throws IOException
	 * @throws SAXParseException
	 * 
	 */
	public List<Status> get(int num, int year, int month, int day)
			throws IOException, SAXParseException {
		// 読み出しログ名
		String timelineDirName = "./" + LOG_DIRECTORY + "/"
				+ TIMELINE_DIRECTORY;
		String filename = timelineDirName + "/" + year + "_" + month + "_"
				+ day + ".log";
		return get(num, filename);
	}

	/**
	 * 指定したlogのTweet情報を取得する
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @throws IOException
	 * @throws SAXParseException
	 * 
	 */
	public List<Status> get(int year, int month, int day) throws IOException,
			SAXParseException {
		// 読み出しログ名
		String timelineDirName = "./" + LOG_DIRECTORY + "/"
				+ TIMELINE_DIRECTORY;
		String filename = timelineDirName + "/" + year + "_" + month + "_"
				+ day + ".log";
		return get(filename);
	}

	/**
	 * 指定したファイルの件数分のTweet情報を取得する
	 * 
	 * @param num
	 * @param path
	 * @throws IOException
	 * @throws SAXParseException
	 * 
	 */
	public List<Status> get(int num, String path) throws IOException,
			SAXParseException {
		String filename = path;
		// tweet情報を保存するリスト
		List<Status> tweetData = get(path);

		int from = tweetData.size() - num;
		int to = tweetData.size();

		if (from < 0) {
			from = 0;
		}

		return tweetData.subList(from, to);
	}

	/**
	 * 指定したファイルの件数分のTweet情報を取得する
	 * 
	 * @param path
	 * @throws IOException
	 * @throws SAXParseException
	 * 
	 */
	public List<Status> get(String path) throws IOException, SAXParseException {
		String filename = path;
		// tweet情報を保存するリスト
		List<Status> tweetData = null;
		try {
			File f = new File(filename);
			byte[] b = new byte[(int) f.length()];
			FileInputStream fi = new FileInputStream(f);

			fi.read(b);
			// 読み取ったデータ
			String data = new String(b, CHARACTER_ENCODING);
			fi.close();

			data = "<root>" + data + "</root>";
			tweetData = StatusXMLConverter.XMLToStatus(data);

		} catch (NullPointerException e) {
			throw new IOException("ファイルが見つかりませんでした.");
		} catch (FileNotFoundException e) {
			throw new IOException("ファイルが見つかりませんでした.");
		}

		return tweetData;
	}

	/**
	 * 今日のtweet情報を取得する
	 * 
	 * @param num
	 * @return
	 * @throws SAXParseException
	 * @throws IOException
	 */
	public List<Status> get(int num) throws SAXParseException, IOException {
		int year = CurrentTime.getCurrentYear();
		int month = CurrentTime.getCurrentMonth();
		int day = CurrentTime.getCurrentDay();
		return this.get(num, year, month, day);
	}

	/**
	 * 今日のtweet情報を取得する
	 * 
	 * @param num
	 * @return
	 * @throws SAXParseException
	 * @throws IOException
	 */
	public List<Status> get() throws SAXParseException, IOException {
		int year = CurrentTime.getCurrentYear();
		int month = CurrentTime.getCurrentMonth();
		int day = CurrentTime.getCurrentDay();
		return this.get(year, month, day);
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
			if (s.isRetweet()) {
				s = s.getRetweetedStatus();
			}
			// ユーザ名
			String username = s.getUser().getName();
			// screen名
			String screenName = s.getUser().getScreenName();
			// メッセージ
			String text = s.getText();
			// 更新日
			String updateTime = DateFormat.getInstance().format(
					s.getCreatedAt());
			// ユーザが利用しているクライアント
			String client = s.getSource();
			// ユーザ紹介文
			String userDescription = s.getUser().getDescription();
			// ユーザフォロー数
			int following = s.getUser().getFriendsCount();
			// ユーザフォロワー数
			int follower = s.getUser().getFollowersCount();
			// 更新数
			int updateCount = s.getUser().getStatusesCount();
			// ユーザのURL
			String userURL = "";
			try {
				userURL = s.getUser().getURL().toString();
			}catch(Exception e) {}
			// ユーザのprofile imageのURL
			String profileImageURL = s.getUser().getProfileImageURL()
					.toString();
			
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
