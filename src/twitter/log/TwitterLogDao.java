package twitter.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
	private static final String createTableSql =
		"CREATE TABLE IF NOT EXISTS TWEET(" +
		"		id INTEGER PRIMARY KEY,"    +
		"		following TEXT," +
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
	private static final String insertDataSql = 
		"INSERT INTO TWEET VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	
	//データ取得SQL
	private static final String selectDataSql = 
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
			statement.execute( createTableSql );
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 新規データの挿入
	 * @param o
	 * @throws SQLException 
	 */
	public void insert( TweetDBObject o ) throws SQLException {
		Connection con = DriverManager.getConnection(DATABASE_CONNECTION);		
		QueryRunner qr = new QueryRunner();
		//データ挿入
		try {
			qr.update(con, insertDataSql, 
					o.getId(),
					o.getFollowing(),
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
		}catch(SQLException e) {
			//挿入ではなく更新にする
		}
		con.close();
	}
	
	public static void main(String[] args) {
		TwitterLogDao dao = new TwitterLogDao();
		dao.connectDB();
		
		dao.createTable();
		
		dao.closeDB();
	}
}
