package twitter.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import twitter.manage.TweetConfiguration;

/**
 * Tweetログ保存用データベースへのアクセス
 * @author nishio
 *
 */
public class TwitterLogDao {
	//データベース接続文字列
	private static final String DATABASE = TweetConfiguration.DATABASE;
	private static final String DATABASE_CONNECTION = TweetConfiguration.DATABASE_CONNECTION;
	
	//データベース接続
	private Connection databaseConnection = null;
	
	//データベーステーブルの定義
	private static final String createTweetTableSql =
		"CREATE TABLE IF NOT EXISTS TWEET(" +
		"		id INTEGER PRIMARY KEY,"    +
		"		date TEXT," + 
		"		replyStatusID INTEGER," + 
		"		replyUserID INTEGER," + 
		"		text TEXT," + 
		"		created TEXT," + 
		"		description TEXT," + 
		"		userFavorite INTEGER," + 
		"		followers INTEGER," + 
		"		friend INTEGER," + 
		"		userId INTEGER," + 
		"		lang TEXT," + 
		"		location TEXT," + 
		"		name TEXT," + 
		"		profileBackgroundColor TEXT," + 
		"		profileBackgroundImageURL TEXT," + 
		"		profileImageURL TEXT," + 
		"		profileSidebarBorderColor TEXT," + 
		"		profileSidebarFillColor TEXT," + 
		"		profileTextColor TEXT," + 
		"		screenName TEXT," + 
		"		statusesCount INTEGER," + 
		"		timeZone TEXT," + 
		"		url TEXT," + 
		"		utc INTEGER," + 
		"		contributorsEnable TEXT," + 
		"		geoEnable TEXT," + 
		"		profileBackgroundTiled TEXT," + 
		"		isProtected TEXT," + 
		"		verified TEXT," + 
		"		source TEXT," + 
		"		favorite TEXT," + 
		"		retweet TEXT," + 
		"		truncated TEXT)"  ;
	
	//データ挿入SQL
	private static final String insertTweetDataSql = 
		"INSERT INTO TWEET VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	
	//UPDATE
	private static final String updateTweetDataSql = 
		"UPDATE TWEET SET " +
		"		date = ?," + 
		"		replyStatusID = ?," + 
		"		replyUserID = ?," + 
		"		text  = ?," + 
		"		created = ?," + 
		"		description = ?," + 
		"		userFavorite = ?," + 
		"		followers = ?," + 
		"		friend = ?," + 
		"		userId = ?," + 
		"		lang = ?," + 
		"		location = ?," + 
		"		name = ?," + 
		"		profileBackgroundColor = ?," + 
		"		profileBackgroundImageURL = ?," + 
		"		profileImageURL = ?," + 
		"		profileSidebarBorderColor = ?," + 
		"		profileSidebarFillColor = ?," + 
		"		profileTextColor = ?," + 
		"		screenName = ?," + 
		"		statusesCount = ?," + 
		"		timeZone = ?," + 
		"		url = ?," + 
		"		utc = ?," + 
		"		contributorsEnable = ?," + 
		"		geoEnable = ?," + 
		"		profileBackgroundTiled = ?," + 
		"		isProtected = ?," + 
		"		verified = ?," + 
		"		source = ?," + 
		"		favorite = ?," + 
		"		retweet = ?," + 
		"		truncated = ? " +
		"WHERE id = ?;";
	
	//データ取得SQL
	private static final String selectTweetDataSql = 
		"SELECT * FROM TWEET;";
	
	/**
	 * データベース接続
	 */
	public void connectDB() {
        try {
			Class.forName( DATABASE );
	        this.databaseConnection = DriverManager.getConnection( DATABASE_CONNECTION );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * データベースの接続を終了する
	 */
	public void closeDB() {
		if( this.databaseConnection != null ) {
			try {
				this.databaseConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * テーブルを作成する
	 */
	public void createTable() {
		Statement statement;
		try {
			statement = this.databaseConnection.createStatement();
			statement.execute( createTweetTableSql );
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ツイートデータをDBに格納する
	 * @param dbobject
	 * @throws SQLException
	 */
	public void insert( List<TweetDBObject> dbobject ) throws SQLException {
		Connection con = DriverManager.getConnection(DATABASE_CONNECTION);		
		QueryRunner qr = new QueryRunner();
		//トランザクション
		con.setAutoCommit(false);
		for( TweetDBObject o : dbobject ) {
			//データ挿入
			try {
				int count = 0;
				//updateする
				count = qr.update(con, updateTweetDataSql,
						o.getDate(),
						o.getReplyStatusID(),
						o.getReplyUserID(),
						o.getText(),
						o.getCreated(),
						o.getDescription(),
						o.getUserFavorite(),
						o.getFollowers(),
						o.getFriend(),
						o.getUserId(),
						o.getLang(),
						o.getLocation(),
						o.getName(),
						o.getProfileBackgroundColor(),
						o.getProfileBackgroundImageURL(),
						o.getProfileImageURL(),
						o.getProfileSidebarBorderColor(),
						o.getProfileSidebarFillColor(),
						o.getProfileTextColor(),
						o.getScreenName(),
						o.getStatusesCount(),
						o.getTimeZone(),
						o.getUrl(),
						o.getUtc(),
						o.getContributorsEnable(),
						o.getGeoEnable(),
						o.getProfileBackgroundTiled(),
						o.getIsProtected(),
						o.getVerified(),
						o.getSource(),
						o.getFavorite(),
						o.getRetweet(),
						o.getTruncated(),
						o.getId() );
				//updateできない場合insertする
				if( count == 0 ) {
					qr.update(con, insertTweetDataSql, 
							o.getId(),
							o.getDate(),
							o.getReplyStatusID(),
							o.getReplyUserID(),
							o.getText(),
							o.getCreated(),
							o.getDescription(),
							o.getUserFavorite(),
							o.getFollowers(),
							o.getFriend(),
							o.getUserId(),
							o.getLang(),
							o.getLocation(),
							o.getName(),
							o.getProfileBackgroundColor(),
							o.getProfileBackgroundImageURL(),
							o.getProfileImageURL(),
							o.getProfileSidebarBorderColor(),
							o.getProfileSidebarFillColor(),
							o.getProfileTextColor(),
							o.getScreenName(),
							o.getStatusesCount(),
							o.getTimeZone(),
							o.getUrl(),
							o.getUtc(),
							o.getContributorsEnable(),
							o.getGeoEnable(),
							o.getProfileBackgroundTiled(),
							o.getIsProtected(),
							o.getVerified(),
							o.getSource(),
							o.getFavorite(),
							o.getRetweet(),
							o.getTruncated() );
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			DbUtils.commitAndCloseQuietly( con );
		}catch(Exception e) {
			DbUtils.commitAndClose( con );
			e.printStackTrace();
		}
	}
	
	/**
	 * 新規データの挿入
	 * @param o
	 * @throws SQLException 
	 */
	public void insert( TweetDBObject o ) throws SQLException {
		List<TweetDBObject> objects = new ArrayList<TweetDBObject>();
		objects.add(o);
		insert( objects );
	}
}
