package twitter.gui.action;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.table.TableModel;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import org.xml.sax.SAXParseException;

import twitter.action.TweetDirectMessageGetter;
import twitter.action.TweetFavoriteGetter;
import twitter.action.TweetGetter;
import twitter.action.TweetListGetter;
import twitter.action.TweetMentionGetter;
import twitter.action.TweetSearchResultGetter;
import twitter.action.TweetSendDirectMessageGetter;
import twitter.action.TweetTimelineGetter;
import twitter.action.TweetUserTimelineGetter;
import twitter.action.list.ListGetterSelection;
import twitter.action.list.UserListGetter;
import twitter.action.list.UserListMembershipsGetter;
import twitter.action.list.UserListSpecificUserListsGetter;
import twitter.action.list.UserListSubscriptionGetter;
import twitter.cache.TwitterImageCache;
import twitter.gui.component.DnDTabbedPane;

import twitter.gui.component.TweetTabbedTable;
import twitter.gui.component.TweetTableModel;
import twitter.gui.form.AboutDialog;
import twitter.gui.form.AccountDialog;
import twitter.gui.form.ConfigurationDialog;
import twitter.gui.form.DirectMessageDialog;
import twitter.gui.form.FollowingFollowerDialog;
import twitter.gui.form.HashtagSearchDialog;
import twitter.gui.form.KeywordSearchDialog;
import twitter.gui.form.OutputCSVLogDialog;
import twitter.gui.form.UserListDialog;
import twitter.gui.form.UserSearchDialog;
import twitter.log.TwitterLogManager;
import twitter.manage.TweetConfiguration;
import twitter.manage.TweetManager;
import twitter.manage.URLBitlyConverter;
import twitter.task.ExistTimerIDException;
import twitter.task.TimerID;
import twitter.task.TweetTaskException;
import twitter.task.TweetTaskManager;
import twitter.task.TweetUpdateTask;
import twitter.util.HTMLEncode;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * GUIのアクション部分
 *
 * @author nishio
 *
 */
public class TweetMainAction {

	// 基本設定を保存するファイル名
	public static final String BASIC_SETTING_FILENAME = TweetConfiguration.BASIC_SETTING_FILENAME;
	// httpのパターン
	private static final Pattern convURLLinkPtn = Pattern.compile(
			"(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+",
			Pattern.CASE_INSENSITIVE);
	// default char encoding
	private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
	// 設定ファイルを保存するディレクトリ名
	public static final String PROPERTIES_DIRECTORY = "properties";
	// search twitterのクエリ
	private static final String SEARCH_QUERY = "search?q=";
	// search twitterのURL
	private static final String SEARCH_TWITTER_URL = "http://"
			+ TweetConfiguration.SEARCH_TWITTER_HOSTNAME + "/";
	// Direct Messageタブに表示する文字
	public static final String TAB_DIRECT_MESSAGE_STRING = "Message";
	// Mentionタブに表示する文字
	public static final String TAB_MENTION_STRING = "Mention";
	// タイムラインタブに表示する文字
	public static final String TAB_TIMELINE_STRING = "Timeline";
	// Send Direct Messageタブに表示する文字
	public static final String TAB_SEND_DIRECT_MESSAGE_STRING = "Send";
	// テーブルのデータ量が以下の値を超えたら古いデータから削除
	private static int tableElementMaxSize = 200;
	// twitterの公式URL
	private static final String TWITTER_URL = "http://twitter.com/";
	// 基本設定用ダイアログ
	private ConfigurationDialog configurationDialog = null;
	// 現在選択しているStatus情報
	private Status currentStatus = null;
	// reply予定のStatus
	private Status replyStatus = null;
	// 詳細情報パネル
	private JPanel detailInfoPanel = null;
	// ダイレクトメッセージ送信用ダイアログ
	private DirectMessageDialog directMessageDialog = null;
	// Twitter全体からキーワード検索ダイアログ
	private KeywordSearchDialog keywordSearchDialog = null;
	// hashtag search dialog
	private HashtagSearchDialog hashtagSearchDialog = null;
	// 新しく取得した部分のテーブルカラー
	private Color newTableColor = new Color(224, 255, 255);
	// TLのフォント名
	private String tlFontName = "Takao Pゴシック";
	// TLのフォントサイズ
	private int tlFontSize = 13;
	// 詳細情報のフォント名
	private String detailFontName = "Takao Pゴシック";
	// 詳細情報のフォントサイズ
	private int detailFontSize = 13;
	// テーブル１要素の高さ
	private int tableElementHeight = 50;
	// メインフレームの幅
	private int mainFrameWidth = 729;
	// メインフレームの高さ
	private int mainFrameHeight = 629;
	// MainFrame
	private JFrame mainFrame = null;
	// 設定
	private Properties property = null;
	// 現在テーブルで選択しているユーザ画像のURL
	private URL selectedUserImageURL = null;
	// 現在テーブルで選択しているユーザの名前
	private String selectedUsername = null;
	// ステータス表示ラベル
	private JLabel statusBarLabel = null;
	// 自分がつぶやきをかく領域
	private JTextPane tweetBoxPane = null;
	// 自分がつぶやきを書く領域のスクロールペーン
	private JPanel tweetBoxRegionPane = null;
	// tweet情報などを表示するタブ
	private JTabbedPane tweetMainTab = null;
	// Tweet管理
	private TweetManager tweetManager = null;
	// tweetを表示するTextPane
	private JEditorPane tweetMessageBox = null;
	// つぶやくことができる文字数を表示するラベル
	private JLabel tweetMessageCountLabel = null;
	private int uncheckedDirectMessageCount = 0;
	private int uncheckedMentionTweetCount = 0;
	// 自分宛のメッセージを通知バーに表示するか
	private boolean isNotifyMentionMessage = true;
	private boolean isNotifyDirectMessage = true;
	//前回開いていたタブの情報を復活する際に利用する（最初の一回だけ利用）
	private boolean isTempOpenedTimelineTab = true;
	private boolean isTempOpenedMentionTab = true;
	private boolean isTempOpenedDMTab = true;
	private boolean isTempOpenedSendDMTab = true;
        //ログを保存するかどうか
        private boolean saveLog = false;

	// Tweetの詳細情報を表示する部分
	private JLabel userImageLabel = null;
	private JLabel userNameLabel = null;
	private JLabel updateTimeLabel = null;
	private JLabel followerLabel = null;
	private JLabel followingLabel = null;
	private JLabel locationLabel = null;
	private JEditorPane clientNameLabel = null;
	private JLabel updateLabel = null;
	private JEditorPane userIntroBox = null;
	private JEditorPane userWebBox = null;
	// トレイアイコン
	private TrayIcon trayIcon = null;

	// checkbox関係
	private javax.swing.JToggleButton timelineToggleButton;
	private javax.swing.JToggleButton mentionToggleButton;
	private javax.swing.JToggleButton dmToggleButton;
	private javax.swing.JToggleButton sendDMToggleButton;

	private javax.swing.JCheckBoxMenuItem timelineCheckBoxMenuItem;
	private javax.swing.JCheckBoxMenuItem mentionCheckBoxMenuItem;
	private javax.swing.JCheckBoxMenuItem dmCheckBoxMenuItem;
	private javax.swing.JCheckBoxMenuItem sendCheckBoxMenuItem;

	// 新しく取得したtweetでまだ参照していない数
	private int uncheckedTimelineTweetCount = 0;
	private AboutDialog aboutDialog = null;
	// アカウント情報表示ダイアログ
	private AccountDialog accountDialog;
	// ツイートを表示するテーブル管理
	private List<TweetTabbedTable> tweetTabbedTableList = new ArrayList<TweetTabbedTable>();
	// ツイートテーブルの情報を一定間隔で更新するクラスを作成
	private TweetTaskManager tweetTaskManager = new TweetTaskManager();

	// リストダイアログ
	private UserListDialog userListDialog = null;
        //ユーザサーチダイアログ
        private UserSearchDialog userSearchDialog = null;
        //CSVログ出力ダイアログ
        private OutputCSVLogDialog outputCSVLogDialog = null;
        //following follower表示ダイアログ
        private FollowingFollowerDialog followingFollowerDialog = null;

	// 情報更新間隔[sec]
	private int getTimelinePeriod = 60;
	private int getMentionPeriod = 60 * 3;
	private int getDirectMessagePeriod = 60 * 15;
	private int getSendDirectMessagePeriod = 60 * 30;

	/**
	 *
	 * @param mainFrame
	 * @param tweetManager
	 * @param statusBarLabel
	 * @param tweetTableModel
	 * @param mentionTableModel
	 * @param directMessageTableModel
	 * @param sendDirectMessageTableModel
	 * @param mainTweetTable
	 * @param mentionTable
	 * @param directMessageTable
	 * @param sendDirectMessageTable
	 * @param tweetBoxPane
	 * @param tweetBoxScrollPane
	 * @param tweetMessageCountLabel
	 * @param detailInfoPanel
	 * @param tweetMainTab
	 * @param tweetMessageBox
	 * @param userImageLabel
	 * @param userNameLabel
	 * @param updateTimeLabel
	 * @param followerLabel
	 * @param followingLabel
	 * @param locationLabel
	 * @param clientNameLabel
	 * @param updateLabel
	 * @param userIntroBox
	 * @param userWebBox
	 */
	public TweetMainAction(JFrame mainFrame, TweetManager tweetManager,
			JLabel statusBarLabel, JTextPane tweetBoxPane,
			JPanel tweetBoxScrollPane, JLabel tweetMessageCountLabel,
			JPanel detailInfoPanel, JTabbedPane tweetMainTab,
			JEditorPane tweetMessageBox, JLabel userImageLabel,
			JLabel userNameLabel, JLabel updateTimeLabel, JLabel followerLabel,
			JLabel followingLabel, JLabel locationLabel,
			JEditorPane clientNameLabel, JLabel updateLabel,
			JEditorPane userIntroBox, JEditorPane userWebBox,
			JToggleButton timelineToggleButton,
			JToggleButton mentionToggleButton, JToggleButton dmToggleButton,
			JToggleButton sendToggleButton,
			JCheckBoxMenuItem timelineCheckBoxMenuItem,
			JCheckBoxMenuItem mentionCheckBoxMenuItem,
			JCheckBoxMenuItem dmCheckBoxMenuItem,
			JCheckBoxMenuItem sendCheckBoxMenuItem, TrayIcon trayIcon) {
		this.mainFrame = mainFrame;
		this.tweetManager = tweetManager;
		this.statusBarLabel = statusBarLabel;
		this.tweetBoxPane = tweetBoxPane;
		this.tweetMessageCountLabel = tweetMessageCountLabel;
		this.detailInfoPanel = detailInfoPanel;
		this.tweetMainTab = tweetMainTab;
		this.tweetMessageBox = tweetMessageBox;
		this.tweetBoxRegionPane = tweetBoxScrollPane;

		// 詳細情報部分
		this.userImageLabel = userImageLabel;
		this.userNameLabel = userNameLabel;
		this.updateTimeLabel = updateTimeLabel;
		this.userIntroBox = userIntroBox;
		this.followerLabel = followerLabel;
		this.followingLabel = followingLabel;
		this.locationLabel = locationLabel;
		this.userWebBox = userWebBox;
		this.clientNameLabel = clientNameLabel;
		this.updateLabel = updateLabel;

		this.timelineCheckBoxMenuItem = timelineCheckBoxMenuItem;
		this.timelineToggleButton = timelineToggleButton;
		this.mentionCheckBoxMenuItem = mentionCheckBoxMenuItem;
		this.mentionToggleButton = mentionToggleButton;
		this.dmCheckBoxMenuItem = dmCheckBoxMenuItem;
		this.dmToggleButton = dmToggleButton;
		this.sendCheckBoxMenuItem = sendCheckBoxMenuItem;
		this.sendDMToggleButton = sendToggleButton;

		// トレイアイコン
		this.trayIcon = trayIcon;

		// 罰ボタンを押した時のイベントを追加
		if (this.tweetMainTab instanceof DnDTabbedPane) {
			((DnDTabbedPane) this.tweetMainTab).setMainAction(this);
		}

		// 設定ファイルの読み込み
		try {
			loadProperties();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// フォント情報を反映
		updateFontInformationToComponent();

		// フレームの大きさを反映
		mainFrame.setSize(this.mainFrameWidth, this.mainFrameHeight);
		mainFrame.setPreferredSize(new Dimension(this.mainFrameWidth,
				this.mainFrameHeight));
	}

	/**
	 * チェックボックスの状態を更新
	 */
	public void updateCheckboxInformation() {
		boolean timeline = this.isExistTimelineTab();
		boolean mention = this.isExistMentionTab();
		boolean dm = this.isExistDirectMessageTab();
		boolean send = this.isExistSendDirectMessageTab();

		this.timelineCheckBoxMenuItem.setSelected(timeline);
		this.timelineToggleButton.setSelected(timeline);
		this.mentionCheckBoxMenuItem.setSelected(mention);
		this.mentionToggleButton.setSelected(mention);
		this.dmCheckBoxMenuItem.setSelected(dm);
		this.dmToggleButton.setSelected(dm);
		this.sendCheckBoxMenuItem.setSelected(send);
		this.sendDMToggleButton.setSelected(send);
	}

	/**
	 * Timeline, Mention , DM, SendDMの情報更新間隔を取得し,その情報をテーブルに反映
	 */
	public void updatePeriodInformationToComponent() {
		// すべてのテーブルにフォント情報を反映
		for (TweetTabbedTable t : this.tweetTabbedTableList) {
			String timerID = t.getTimerID();
			if (timerID.equals(TimerID.createTimelineID())) {
				// TLの周期情報更新
				this.tweetTaskManager.updateTaskPeriod(timerID, this
						.getGetTimelinePeriod(), false);
			} else if (timerID.equals(TimerID.createMentionID())) {
				// Mentionの周期情報更新
				this.tweetTaskManager.updateTaskPeriod(timerID, this
						.getGetMentionPeriod(), false);
			} else if (timerID.equals(TimerID.createDirectMessageID())) {
				// DMの周期情報更新
				this.tweetTaskManager.updateTaskPeriod(timerID, this
						.getGetDirectMessagePeriod(), false);
			} else if (timerID.equals(TimerID.createSendDirectMessageID())) {
				// SendDMの周期情報更新
				this.tweetTaskManager.updateTaskPeriod(timerID, this
						.getGetSendDirectMessagePeriod(), false);
			}
		}
	}

	// フォント情報をコンポーネントに反映
	public void updateFontInformationToComponent() {
		try {
			Font tlFont = null;
			if (this.tlFontName != null) {
				tlFont = new Font(this.tlFontName, Font.PLAIN, this.tlFontSize);
			}
			Font detailFont = null;
			if (this.detailFontName != null) {
				detailFont = new Font(this.detailFontName, Font.PLAIN,
						this.detailFontSize);
			}

			// すべてのテーブルにフォント情報を反映
			for (TweetTabbedTable t : this.tweetTabbedTableList) {
				t.getTable().setFont(tlFont);
			}

			// tweetメッセージボックスのフォントはhtmlレベルで変更する必要がある
			this.tweetMessageBox.setFont(detailFont);
			// htmlフォント変更
			HTMLDocument doc = (HTMLDocument) this.tweetMessageBox
					.getDocument();
			StyleSheet[] style = doc.getStyleSheet().getStyleSheets();
			for (int i = style.length - 1; i >= 0; i--) {
				Style body = style[i].getStyle("body");
				if (body != null) {
					StyleConstants
							.setFontFamily(body, detailFont.getFontName());
					StyleConstants.setFontSize(body, detailFont.getSize());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新しいタブを追加
	 *
	 * @param timerID
	 *            TimerIDクラスで生成したタイマーID
	 * @param period
	 *            情報更新間隔[sec]
	 * @param tweetGetter
	 *            実行するアクション
	 * @param tabTitle
	 *            追加するタブのタイトル
	 */
	public void actionAddTab(String timerID, int period,
			TweetGetter tweetGetter, String tabTitle) {
		// 周期的に情報を更新する
		if (period > 0) {
			try {
				// テーブルを作成
				final TweetTabbedTable table = new TweetTabbedTable(
						tweetGetter, tabTitle, this.tweetMainTab,
						this.tableElementHeight, this.tweetManager, this,
						newTableColor, this.tableElementMaxSize, timerID);

				this.tweetTaskManager.addTask(timerID, new TweetUpdateTask() {

					@Override
					public void runTask() throws TweetTaskException {
						// ツイート情報を一定間隔で更新
						table.updateTweetTable();
					}
				});
				// 更新開始
				this.tweetTaskManager.startTask(timerID, period * 1000L);

				// タブにテーブルを追加
				table.addTableToTab();
				// タブリストに追加
				this.tweetTabbedTableList.add(table);
				//設定を保存
				this.saveProperties();
				// searchTable.updateTweetTable();
				// フォーカスを新しいタブに移す
				this.actionRequestForusToLastTab();
			} catch (TweetTaskException ex) {
				Logger.getLogger(TweetMainAction.class.getName()).log(
						Level.SEVERE, null, ex);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// フォント情報を更新
		this.updateFontInformationToComponent();
		// テーブルの高さをすべて更新
		this.updateTableHeight(this.getTableElementHeight());
	}

	/**
	 * mentionタブを追加する
	 *
	 * @param period
	 *            情報更新間隔[sec]
	 */
	public void actionAddMentionTab(int period) {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createMentionID();
		try {
			// 既にIDが存在していたらここで例外発生
			timerID.addID(id);
			// 検索結果を表示するタブを生成
			if (this.isNotifyMentionMessage) {
				// メッセージが到着したら通知を行う
				actionAddTab(id, period, new TweetMentionGetter(tweetManager,
						this.trayIcon), TweetMainAction.TAB_MENTION_STRING);
			} else {
				actionAddTab(id, period, new TweetMentionGetter(tweetManager),
						TweetMainAction.TAB_MENTION_STRING);
			}
		} catch (ExistTimerIDException ex) {
			JOptionPane.showMessageDialog(null, "そのタブは既に存在しています", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * お気に入りタブを追加
	 *
	 * @param screenName
	 *            nullで自分自身を取得，指定するとscreenNameのFav取得
	 */
	public void actionAddFavoriteTab(String screenName) {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createFavoriteID(screenName);
		try {
			// 既にIDが存在していたらここで例外発生
			timerID.addID(id);
			String favTitle;
			if (screenName == null) {
				favTitle = "お気に入り";
			} else {
				favTitle = screenName + "のお気に入り";
			}
			// 検索結果を表示するタブを生成
			actionAddTab(id, Integer.MAX_VALUE, new TweetFavoriteGetter(
					tweetManager, screenName), favTitle);
		} catch (ExistTimerIDException ex) {
			JOptionPane.showMessageDialog(null, "そのタブは既に存在しています", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * timelineタブを追加する
	 *
	 * @param period
	 *            [sec]
	 */
	public void actionAddTimelineTab(int period) {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createTimelineID();
		try {
			// 既にIDが存在していたらここで例外発生
			timerID.addID(id);
			// 検索結果を表示するタブを生成
			actionAddTab(id, period, new TweetTimelineGetter(tweetManager, this),
					TweetMainAction.TAB_TIMELINE_STRING);
		} catch (ExistTimerIDException ex) {
			JOptionPane.showMessageDialog(null, "そのタブは既に存在しています", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * ダイレクトメッセージタブを追加する
	 *
	 * @param period
	 *            更新間隔[sec]
	 */
	public void actionAddDirectMessageTab(int period) {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createDirectMessageID();
		try {
			// 既にIDが存在していたらここで例外発生
			timerID.addID(id);
			// 検索結果を表示するタブを生成
			if (this.isNotifyDirectMessage == true) {
				actionAddTab(id, period, new TweetDirectMessageGetter(
						tweetManager, this.trayIcon),
						TweetMainAction.TAB_DIRECT_MESSAGE_STRING);
			} else {
				actionAddTab(id, period, new TweetDirectMessageGetter(
						tweetManager),
						TweetMainAction.TAB_DIRECT_MESSAGE_STRING);
			}
		} catch (ExistTimerIDException ex) {
			JOptionPane.showMessageDialog(null, "そのタブは既に存在しています", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * SendDMタブを追加する
	 *
	 * @param period
	 */
	public void actionAddSendDirectMessageTab(int period) {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createSendDirectMessageID();
		try {
			// 既にIDが存在していたらここで例外発生
			timerID.addID(id);
			// 検索結果を表示するタブを生成
			actionAddTab(id, period, new TweetSendDirectMessageGetter(
					tweetManager),
					TweetMainAction.TAB_SEND_DIRECT_MESSAGE_STRING);
		} catch (ExistTimerIDException ex) {
			JOptionPane.showMessageDialog(null, "そのタブは既に存在しています", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 指定したリストをタブに追加
	 *
	 * @param username
	 * @param listID
	 * @param listFullname
	 * @param period
	 */
	public void actionAddListTab(String username, int listID,
			String listFullname, int period) {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createUserListID(username, listID);
		try {
			// 既にIDが存在していたらここで例外発生
			timerID.addID(id);
			// 検索結果を表示するタブを生成
			actionAddTab(id, period, new TweetListGetter(tweetManager,
					username, listID), listFullname);
		} catch (ExistTimerIDException ex) {
			JOptionPane.showMessageDialog(null, "そのタブは既に存在しています", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * ツイート検索結果を表示するタブを新しく追加
	 *
	 * @param searchWord
	 * @param period
	 *            更新周期[sec] 0以下の場合は更新しない
	 */
	public void actionAddNewSearchResultTab(String searchWord, int period) {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createSearchTimerID(searchWord);
		try {
			// 既にIDが存在していたらここで例外発生
			timerID.addID(id);
			// 検索結果を表示するタブを生成
			actionAddTab(id, period, new TweetSearchResultGetter(
					this.tweetManager, searchWord), searchWord);
		} catch (ExistTimerIDException ex) {
			JOptionPane.showMessageDialog(null, "そのタブは既に存在しています", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 指定したユーザの発言を表示
	 *
	 * @param username
	 *            タブのタイトルにつけるユーザ名
	 * @param period
	 *            更新周期[sec]
	 */
	public void actionAddUserTimelineTab(String username,
			int period) {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createUserTimelineID(username);
		try {
			// 既にIDが存在していたらここで例外発生
			timerID.addID(id);
			// 検索結果を表示するタブを生成
			actionAddTab(id, period, new TweetUserTimelineGetter(tweetManager,
					username), username + "の発言");
		} catch (ExistTimerIDException ex) {
			JOptionPane.showMessageDialog(null, "そのタブは既に存在しています", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * タイムラインタブが存在しているか
	 *
	 * @return
	 */
	public boolean isExistTimelineTab() {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createTimelineID();
		return timerID.contains(id);
	}

	/**
	 * Mentionタブが存在するか
	 *
	 * @return
	 */
	public boolean isExistMentionTab() {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createMentionID();
		return timerID.contains(id);
	}

	/**
	 * DMタブが存在するか
	 *
	 * @return
	 */
	public boolean isExistDirectMessageTab() {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createDirectMessageID();
		return timerID.contains(id);
	}

	/**
	 * 送信済みDMタブが存在するか
	 *
	 * @return
	 */
	public boolean isExistSendDirectMessageTab() {
		TimerID timerID = TimerID.getInstance();
		String id = TimerID.createSendDirectMessageID();
		return timerID.contains(id);
	}

	/**
	 * 基本設定ダイアログを開く
	 */
	public void actionBasicSettingDialog() {
		// ダイレクトメッセージ送信用ダイアログを開く
		Point loc = getConfigurationDialog().getLocation();
		loc.translate(20, 20);
		ConfigurationDialog dialog = getConfigurationDialog();
		dialog.setLocation(loc);
		dialog.setVisible(true);
	}

	/**
	 * reply設定
	 */
	public void actionSetReplyStatusToTweetBoxPane() {
		// 選択した部分
		this.setReplyStatus(currentStatus);

		Status s = this.getCurrentStatus();
		if (s.isRetweet()) {
			s = s.getRetweetedStatus();
		}
		// コメントしたユーザ名
		String username = s.getUser().getScreenName();
		this.tweetBoxPane.setText("@" + username + " ");

		// 情報表示
		this.information(username + "さんに返信");
	}

	/**
	 * reply All設定
	 */
	public void actionSetReplyAllStatusToTweetBoxPane() {
		// 選択し多分
		this.setReplyStatus(null);

		Status s = this.getCurrentStatus();
		if (s.isRetweet()) {
			s = s.getRetweetedStatus();
		}
		// コメントしたユーザ名
		String username = s.getUser().getScreenName();
		this.tweetBoxPane.setText("@" + username + " ");

		// 情報表示
		this.information(username + "さんに返信");
	}

	/**
	 * 引用Tweet
	 */
	public void actionSetQuoteStatusToTweetBoxPane() {
		// 選択した部分
		this.setReplyStatus(currentStatus);
		Status s = this.getCurrentStatus();
		if (s.isRetweet()) {
			s = s.getRetweetedStatus();
		}
		// コメントしたユーザ名
		String username = s.getUser().getScreenName();
		// コメント
		String message = s.getText();
		this.tweetBoxPane.setText("QT @" + username + ": " + message);

		// 情報表示
		this.information(username + "さんのメッセージを引用ツイート");
	}

	/**
	 * 選択したtweetを非公式RT
	 */
	public void actionCopySelectedStatusToTweetBoxPane() {
		Status s = this.getCurrentStatus();
		if (s.isRetweet()) {
			s = s.getRetweetedStatus();
		}
		// コメントしたユーザ名
		String username = s.getUser().getScreenName();
		// コメント
		String message = s.getText();
		this.tweetBoxPane.setText("RT @" + username + ": " + message);
	}

	/**
	 * 詳細情報表示ボタンを押した時の動作
	 *
	 * @param e
	 */
	public void actionDetailInfoButton(ActionEvent e) {
		if (detailInfoPanel.isVisible()) {
			detailInfoPanel.setVisible(false);
		} else {
			detailInfoPanel.setVisible(true);
		}
	}

	/**
	 * 詳細情報ボタンが表示されているか
	 *
	 * @return
	 */
	public boolean isDetailInfoPanelVisible() {
		return detailInfoPanel.isVisible();
	}

	/**
	 * 書き込みメッセージボックスの表示ONOFFボタンを押した時の動作
	 *
	 * @param e
	 */
	public void actionShowTweetboxButton(ActionEvent e) {
		if (this.tweetBoxRegionPane.isVisible()) {
			this.tweetBoxRegionPane.setVisible(false);
		} else {
			this.tweetBoxRegionPane.setVisible(true);
		}
	}

	/**
	 * 書き込みメッセージボックス領域が表示されているか
	 */
	public boolean isShowTweetBoxVisible() {
		return this.tweetBoxRegionPane.isVisible();
	}

	/**
	 * 選択しているタブを削除
	 */
	public void actionRemoveFocusedTabbedTable() {
		int selected = this.tweetMainTab.getSelectedIndex();
		actionRemoveTabbedTable(selected);
	}

	/**
	 * 指定したIDのタブを削除
	 *
	 * @param timerID
	 */
	public void actionRemoveTabbedTable(String timerID) {
		int deleteTabIndex = -1;

		for (int i = 0; i < tweetTabbedTableList.size(); i++) {
			TweetTabbedTable table = tweetTabbedTableList.get(i);
			if (table.getTimerID().equals(timerID)) {
				// 消したいタブが見つかった
				deleteTabIndex = i;
				break;
			}
		}

		if (deleteTabIndex >= 0) {
			int selected = this.tweetTabbedTableList.get(deleteTabIndex)
					.getTabSetNum();
			// タブを削除
			this.tweetMainTab.remove(selected);
			// 削除
			this.tweetTabbedTableList.remove(deleteTabIndex);
			// 自動更新しているタブを削除
			this.tweetTaskManager.shutdownTask(timerID);
			// ID削除
			TimerID idManager = TimerID.getInstance();
			idManager.removeID(timerID);

			// checkboxの状態更新
			this.updateCheckboxInformation();
			//設定保存
			try {
				saveProperties();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 指定した場所にあるタブを削除
	 *
	 * @param removeTabIndex
	 */
	public void actionRemoveTabbedTable(int removeTabIndex) {
		int selected = removeTabIndex;
		// タブの何番目に消したいテーブルがあるのかと，tweetTabbedTableListの何番目に消したいテーブルがあるのかは違う
		// これを探してくる必要がある

		// 選択したタブのテーブルを取得
		int deleteTabIndex = -1;
		for (int i = 0; i < tweetTabbedTableList.size(); i++) {
			TweetTabbedTable table = tweetTabbedTableList.get(i);
			if (selected == table.getTabSetNum()) {
				// 消したいタブが見つかった
				deleteTabIndex = i;
				break;
			}
		}

		if (deleteTabIndex >= 0) {
			// タブを削除
			this.tweetMainTab.remove(selected);
			// タブのタイマーID
			String timerID = this.tweetTabbedTableList.get(deleteTabIndex)
					.getTimerID();
			// 削除
			this.tweetTabbedTableList.remove(deleteTabIndex);
			// 自動更新しているタブを削除
			this.tweetTaskManager.shutdownTask(timerID);
			// ID削除
			TimerID idManager = TimerID.getInstance();
			idManager.removeID(timerID);

			// checkboxの状態更新
			this.updateCheckboxInformation();
			//設定保存
			try {
				saveProperties();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 終了ボタンを押した時の動作
	 *
	 * @param e
	 */
	public void actionExitButton(ActionEvent e) {
		System.exit(0);
	}

	/**
	 * 選択した発言をブラウザで開く
	 */
	public void actionOpenStatusURL() {
		try {
			// ユーザ名
			String userName = this.getCurrentStatus().getUser().getScreenName();
			// 発言のstatusID
			long statusID = this.getCurrentStatus().getId();
			Desktop.getDesktop().browse(
					new URI(TWITTER_URL + userName + "/statuses/" + statusID));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "エラーによりブラウザを起動できませんでした．",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

        /**
         * 選択したユーザのfollowingを表示
         */
        public void actionOpenFollowing() {
            // ユーザ名
            try {
                String userName = this.getCurrentStatus().getUser().getScreenName();
                this.actionShowFollowingFollowerDialog(userName, true);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 選択したユーザのfollowingを表示
         */
        public void actionOpenFollower() {
            try {
                // ユーザ名
                String userName = this.getCurrentStatus().getUser().getScreenName();
                this.actionShowFollowingFollowerDialog(userName, false);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

	/**
	 * 選択したユーザのFavを開く
	 */
	public void actionOpenUserFav() {
		try {
			Status s = this.getCurrentStatus();
			if (s.isRetweet()) {
				s = s.getRetweetedStatus();
			}
			String userName = s.getUser().getScreenName();
			this.actionAddFavoriteTab(userName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 自分自身のFavを開く
	 */
	public void actionOpenFav() {
		String username = this.tweetManager.getScreenName();
		this.actionAddFavoriteTab(username);
	}

	/**
	 * 選択したユーザ情報をブラウザで開く
	 */
	public void actionOpenUserURL() {
		try {
			Status s = this.getCurrentStatus();
			if (s.isRetweet()) {
				s = s.getRetweetedStatus();
			}
			String userName = s.getUser().getScreenName();
			Desktop.getDesktop().browse(new URI(TWITTER_URL + userName));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "エラーによりブラウザを起動できませんでした．",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Tweet取得時間情報を更新
	 */
	public void actionRefreshTime() {
		// タブに存在する時間情報を更新
		for (TweetTabbedTable t : this.tweetTabbedTableList) {
			TweetTableModel model = t.getModel();
			if (model != null) {
				model.refreshTime();
			}
		}
	}

	/**
	 * TweetMessageBox内にある#ハッシュタグ の部分をa hrefリンクに変換
	 *
	 * @param message
	 */
	public String actionReplaceTweetMessageBoxHashTab(String message) {
		// #で始まる情報
		Pattern userPtn = Pattern.compile( TweetConfiguration.HASHTAG_PATTERN,
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = userPtn.matcher(message);

		// #で始まる情報一覧を抜き出す
		Set<String> findList = new TreeSet<String>();
		while (matcher.find()) {
			findList.add(matcher.group(0));
		}
		// 指定した情報をすべてリンクへ変更
		for (String f : findList) {
			try {
				message = message.replaceAll(f + "$|" + f + "\\s", "<a href=\""
						+ SEARCH_TWITTER_URL + SEARCH_QUERY
						+ URLEncoder.encode(f, DEFAULT_CHARACTER_ENCODING)
						+ "\">" + "$0</a>");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return message;
	}

	/**
	 * TweetMessageBox内にあるリンクをa hrefリンクに変換
	 *
	 * @param message
	 */
	public String actionReplaceTweetMessageBoxURLLink(String message) {
		Matcher matcher = convURLLinkPtn.matcher(message);
		return matcher.replaceAll("<a href=\"$0\">$0</a>");
	}

	/**
	 * メッセージ内にあるURLをBitlyに変換する
	 * @param message
	 * @return
	 */
	public String actionConvertURLToBitly(String message) {
		String result = new String( message );
		//URLをbitlyに変換
		Matcher matcher = convURLLinkPtn.matcher(message);
		while( matcher.find() ) {
			String source = matcher.group();
			String conv = URLBitlyConverter.convertUrlToBitly( source );
			result = result.replace( source , conv );
		}
		return result;
	}

	/**
	 * つぶやきボックス内のURLをbitlyに変換
	 */
	public void actionConvertTweetBoxURLToBitly() {
		String message = this.tweetBoxPane.getText();
		String conv = actionConvertURLToBitly(message);
		this.tweetBoxPane.setText( conv );
	}

	/**
	 * @ユーザ名の部分をa hrefリンクに変換
	 * @param message
	 */
	public String actionReplaceTweetMessageBoxUserInfo(String message) {
		// @で始まる情報
		Pattern userPtn = Pattern.compile("@[0-9A-Z_]+",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = userPtn.matcher(message);
		// @で始まるユーザ名一覧を抜き出す
		Set<String> findList = new TreeSet<String>();
		while (matcher.find()) {
			findList.add(matcher.group(0));
		}
		// 指定したユーザ名をすべてリンクへ変更
		for (String f : findList) {
			message = message.replaceAll(f, "<a href=\"" + TWITTER_URL
					+ f.substring(1) + "\">" + f + "</a>");
		}
		return message;
	}

	/**
	 * まだ見ていないdirectMessage数を0にする
	 */
	public void actionResetUncheckedDirectMessageCount() {
		uncheckedDirectMessageCount = 0;
		tweetMainTab.setTitleAt(2, TAB_DIRECT_MESSAGE_STRING);
	}

	/**
	 * まだ見ていないmention数を0にする
	 */
	public void actionResetUncheckedMentionTweetCount() {
		uncheckedMentionTweetCount = 0;
		tweetMainTab.setTitleAt(1, TAB_MENTION_STRING);
	}

	/**
	 * まだ見ていないtweet数を0にする
	 */
	public void actionResetUncheckedTimelineTweetCount() {
		uncheckedTimelineTweetCount = 0;
		tweetMainTab.setTitleAt(0, TAB_TIMELINE_STRING);
	}

	/**
	 * 選択したユーザの発言を開く
	 */
	public void actionSelectedUserTimeline() {
		Status status = null;
		if (this.getCurrentStatus().isRetweet()) {
			status = this.getCurrentStatus().getRetweetedStatus();
		} else {
			status = this.getCurrentStatus();
		}
		String username = status.getUser().getScreenName();
		// ユーザ
		actionAddUserTimelineTab(username, this.getGetTimelinePeriod());
	}

	/**
	 * 選択したユーザが作成したリスト一覧を表示
	 *
	 * @param selection
	 */
	public void actionShowSelectedUserList(ListGetterSelection selection) {
		Status status = null;
		if (this.getCurrentStatus().isRetweet()) {
			status = this.getCurrentStatus().getRetweetedStatus();
		} else {
			status = this.getCurrentStatus();
		}
		String username = status.getUser().getScreenName();
		actionShowUserListDialog(status.getUser().getScreenName(), selection);
	}

	/**
	 * 選択しているツイートをお気に入りに追加
	 */
	public void actionCreateFavorite() {
		Status status = null;
		if (this.getCurrentStatus().isRetweet()) {
			status = this.getCurrentStatus().getRetweetedStatus();
		} else {
			status = this.getCurrentStatus();
		}
		// 選択しているtweetのstatus id
		long statusID = status.getId();
		// コメントしたユーザ名
		String username = status.getUser().getScreenName();
		// コメント
		String message = status.getText();
		// 発言が長すぎる場合，後半をカット
		if (message.length() > 30) {
			message = message.substring(0, 30) + " ...(以下略)";
		}
		// Retweetしていいかどうかの確認
		int ret = JOptionPane.showConfirmDialog(mainFrame, username + " さんの発言:"
				+ message + "\nをお気に入りに追加しますか?", "Favの確認",
				JOptionPane.YES_NO_OPTION);
		if (ret == JOptionPane.YES_OPTION) {
			try {
				// Retweetを行う
				this.tweetManager.createFavorite(statusID);
			} catch (TwitterException e) {
				JOptionPane.showMessageDialog(null, "エラーによりお気に入りに追加できませんでした．",
						"Fav Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * 選択しているツイートをお気に入りから外す
	 */
	public void actionDestroyFavorite() {
		Status status = null;
		if (this.getCurrentStatus().isRetweet()) {
			status = this.getCurrentStatus().getRetweetedStatus();
		} else {
			status = this.getCurrentStatus();
		}
		// 選択しているtweetのstatus id
		long statusID = status.getId();
		// コメントしたユーザ名
		String username = status.getUser().getScreenName();
		// コメント
		String message = status.getText();
		// 発言が長すぎる場合，後半をカット
		if (message.length() > 30) {
			message = message.substring(0, 30) + " ...(以下略)";
		}
		// Retweetしていいかどうかの確認
		int ret = JOptionPane.showConfirmDialog(mainFrame, username + " さんの発言:"
				+ message + "\nをお気に入りから削除しますか?", "Favの確認",
				JOptionPane.YES_NO_OPTION);
		if (ret == JOptionPane.YES_OPTION) {
			try {
				// Retweetを行う
				this.tweetManager.destroyFavorite(statusID);
			} catch (TwitterException e) {
				JOptionPane.showMessageDialog(null, "エラーによりお気に入りから削除できませんでした．",
						"Fav Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * 現在選択しているステータスを公式Retweet
	 */
	public void actionRetweet() {
		Status status = null;
		if (this.getCurrentStatus().isRetweet()) {
			status = this.getCurrentStatus().getRetweetedStatus();
		} else {
			status = this.getCurrentStatus();
		}

		// 選択しているtweetのstatus id
		long statusID = status.getId();
		// コメントしたユーザ名
		String username = status.getUser().getScreenName();
		// コメント
		String message = status.getText();
		// 発言が長すぎる場合，後半をカット
		if (message.length() > 30) {
			message = message.substring(0, 30) + " ...(以下略)";
		}
		// Retweetしていいかどうかの確認
		int ret = JOptionPane.showConfirmDialog(mainFrame, username + " さんの発言:"
				+ message + "\nをRetweetしますか?", "Retweetの確認",
				JOptionPane.YES_NO_OPTION);
		if (ret == JOptionPane.YES_OPTION) {
			try {
				// Retweetを行う
				this.tweetManager.retweet(statusID);
			} catch (TwitterException e) {
				JOptionPane.showMessageDialog(null, "エラーによりRetweetできませんでした．",
						"Retweet Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * テーブルの高さを更新
	 *
	 * @param height
	 */
	public void updateTableHeight(int height) {
		this.tableElementHeight = height;
		for (TweetTabbedTable t : this.tweetTabbedTableList) {
			t.getTable().setRowHeight(tableElementHeight);
		}
	}

	/**
	 * ダイレクトメッセージダイアログ表示
	 */
	public void actionShowDirectMessageDialog() {
		// ダイレクトメッセージ送信用ダイアログを開く
		Point loc = getDirectMessageDialog().getLocation();
		// loc.translate(20, 20);
		DirectMessageDialog dialog = getDirectMessageDialog();
		// dialog.setLocation(loc);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.setUserInformation(this.selectedUsername,
				this.selectedUserImageURL, this.tweetManager);
	}

	/**
	 * Aboutダイアログを表示
	 */
	public void actionShowAboutDialog() {
		Point loc = getDirectMessageDialog().getLocation();
		// loc.translate(20, 20);
		AboutDialog dialog = getAboutDialog();
		dialog.setLocationRelativeTo(null);
		// dialog.setLocation(loc);
		dialog.setVisible(true);
	}

	/**
	 * アカウントダイアログを表示
	 */
	public void actionShowAccountDialog() {
		// TODO: location取得のコードおかしい
		Point loc = getDirectMessageDialog().getLocation();
		// loc.translate(20, 20);
		AccountDialog dialog = getAccountDialog();
		dialog.setLocationRelativeTo(null);
		// dialog.setLocation(loc);
		dialog.setVisible(true);
	}

	/**
	 * Twitter全体からキーワード検索ダイアログを表示
	 */
	public void actionShowKeywordSearchDialog() {
		Point loc = getDirectMessageDialog().getLocation();
		KeywordSearchDialog dialog = getKeywordSearchDialog();
		dialog.setSearchWord("");
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	/**
	 * Twitter全体からキーワード検索ダイアログを表示
	 *
	 * @param searchWord
	 *            searchwordボックスに予め設定するワード
	 */
	public void actionShowKeywordSearchDialog(String searchWord) {
		Point loc = getDirectMessageDialog().getLocation();
		KeywordSearchDialog dialog = getKeywordSearchDialog();
		dialog.setSearchWord(searchWord);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

        /**
         * 指定したユーザ検索ダイアログを表示
         */
        public void actionShowUserSearchDialog() {
            UserSearchDialog dialog = getUserSearchDialog();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }

        /**
         * CSVログ出力ダイアログを表示
         */
        public void actionShowOutputCSVLogDialog() {
            OutputCSVLogDialog dialog = getOutputCSVLogDialog();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }

        /**
         * following follower表示ダイアログを表示
         */
        public void actionShowFollowingFollowerDialog() {
            FollowingFollowerDialog dialog = getFollowingFollowerDialog();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }

        /**
         * following follower表示ダイアログを表示
         * @param username 取得したいユーザ
         * @param ff trueでfollowing/ falseでfollower取得
         */
        public void actionShowFollowingFollowerDialog(String username, boolean ff) {
            FollowingFollowerDialog dialog = getFollowingFollowerDialog();
            //following follower取得開始
            dialog.actionUserSearch(username, ff);

            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }

	/**
	 * ハッシュタグ検索ダイアログを表示
	 */
	public void actionShowHashtagSearchDialog() {
		Point loc = getDirectMessageDialog().getLocation();
		HashtagSearchDialog dialog = getHashtagSearchDialog();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	/**
	 * リストダイアログを表示
	 *
	 * @param listUserName
	 *            リストを保持しているユーザの名前
	 * @param selection
	 *            CREATED: 指定したユーザが作成したリスト SUBSCRIPTION: 指定したユーザがフォローしているリスト
	 *            MEMBERSHIPS: 指定したユーザが追加されているリスト
	 */
	public void actionShowUserListDialog(String listUserName,
			ListGetterSelection selection) {
		UserListDialog dialog = getUserListDialog(listUserName, selection);
		Point loc = dialog.getLocation();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	/**
	 * tweetBoxPaneに書かれた文字をつぶやく
	 */
	public void actionTweet() {
		boolean isTweet = false;
		try {
			if (this.replyStatus != null) {
				tweetManager.replyTweet(tweetBoxPane.getText(),
						this.replyStatus.getId());
			} else {
				tweetManager.tweet(tweetBoxPane.getText());
			}
			isTweet = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isTweet) {
			// ツイートした旨を表示
			this.information("メッセージをつぶやきました. 発言:" + tweetBoxPane.getText());
			tweetBoxPane.setText(""); // テキストをクリア
		} else {
			this.information("つぶやきに失敗しました");
			JOptionPane.showMessageDialog(null,
					"つぶやきに失敗しました。文字数がオーバーしているか、ツイッターに接続ができませんでした。",
					"Tweet Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Tweet情報を更新
	 *
	 * @param e
	 */
	public void actionUpdateButton(java.awt.event.ActionEvent e) {
		try {
			// タブ上に存在するテーブルの情報を更新
			for (TweetTabbedTable t : this.tweetTabbedTableList) {
				String timerID = t.getTimerID();
				this.tweetTaskManager.resetTask(timerID, true);
			}

			// API残り回数を取得
			int remainingHits = tweetManager.getRateLimitStatus()
					.getRemainingHits();
			// 取得したコメント数をステータスバーに表示
			information("新しいツイートを取得しました. (APIリクエスト残数は" + remainingHits + "回です)");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 新しく追加したタブにフォーカスを移す
	 */
	public void actionRequestForusToLastTab() {
		int lasttab = this.tweetMainTab.getTabCount() - 1;
		if (lasttab >= 0) {
			this.tweetMainTab.setSelectedIndex(lasttab);
		}
	}

	/**
	 * 指定した番号のタブにフォーカスを移す
	 *
	 * @param index
	 */
	public void actionRequestFocusToTab(int index) {
		if (index >= 0) {
			this.tweetMainTab.setSelectedIndex(index);
		}
	}

	/**
	 * 選択しているタブにあるテーブル情報だけを更新
	 */
	public void actionFocusedTableUpdate() {
		int selected = this.tweetMainTab.getSelectedIndex();
		try {
			if (selected >= 0) {
				// タブ上に存在するテーブルの情報を更新
				TweetTabbedTable t = this.tweetTabbedTableList.get(selected);
				String timerID = t.getTimerID();
				this.tweetTaskManager.resetTask(timerID, true);
				// API残り回数を取得
				int remainingHits = tweetManager.getRateLimitStatus()
						.getRemainingHits();
				// 取得したコメント数をステータスバーに表示
				information(t.getTitle() + "タブのツイートを" + t.getUncheckedTweet()
						+ "件取得しました. (APIリクエスト残数は" + remainingHits + "回です)");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * つぶやける残り文字数の更新
	 *
	 * @param e
	 */
	public void actionUpdateTweetMessageCount() {
		int len = 140 - (tweetBoxPane.getText().length());
		boolean over = false;
		if (len < 0) {
			// len = 0;
			over = true;
		}

		if (over) {
			tweetMessageCountLabel.setForeground(Color.RED);
			// オーバーしたことを伝える
			tweetMessageCountLabel.setText("Over(" + len + ")");
		} else {
			tweetMessageCountLabel.setForeground(Color.BLACK);
			// 残り文字数
			tweetMessageCountLabel.setText(len + "");
		}

		// 残りつぶやき数140の場合，reply状態も解除する
		if (len == 140) {
			this.setReplyStatus(null);
		}
	}

	/**
	 * デバッグ用
	 */
	public void debug() {
            //this.actionShowFollowingFollowerDialog("nishio_dens", false);
	}

        /**
         * ログデータを保存
         * @param outputFilePath
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
         * @throws SAXParseException
         * @throws IOException
         */
        public void outputLogToCSV(String outputFilePath,
                boolean showUsername, boolean showScreenName,
			boolean showText,
			boolean showUpdateTime, boolean showClient,
			boolean showUserDescription,
			boolean showFollowing, boolean showFollower,
			boolean showUpdateCount, boolean showUserURL,
			boolean showProfileImageURL) throws SAXParseException, IOException {
            TwitterLogManager logManager = new TwitterLogManager();
            List<Status> statuses = logManager.get();

            logManager.outputCSVLog( outputFilePath, statuses, showUsername, showScreenName,
                    showText, showUpdateTime, showClient,
                    showUserDescription, showFollowing, showFollower,
                    showUpdateCount, showUserURL,showProfileImageURL);
        }

	/**
	 * 基本設定用ダイアログを取得
	 *
	 * @return
	 */
	public ConfigurationDialog getConfigurationDialog() {
		if (configurationDialog == null) {
			configurationDialog = new ConfigurationDialog(mainFrame, true, this);
		}
		return configurationDialog;
	}

	/**
	 * リストダイアログを取得
	 *
	 * @param listUserName
	 *            リストを保持しているユーザの名前
	 * @param selection
	 * @return
	 */
	public UserListDialog getUserListDialog(String listUserName,
			ListGetterSelection selection) {
		UserListGetter getter = null;
		switch (selection) {
		case CREATED:
			getter = new UserListSpecificUserListsGetter(tweetManager);
			break;
		case MEMBERSHIPS:
			getter = new UserListMembershipsGetter(tweetManager);
			break;
		case SUBSCRIPTION: /* fall through */
		default:
			getter = new UserListSubscriptionGetter(tweetManager);
			break;
		}
		userListDialog = new UserListDialog(mainFrame, true, this, getter,
				listUserName);
		return userListDialog;
	}

	/**
	 * 自身のスクリーン名を取得
	 *
	 * @return
	 */
	public String getScreenName() {
		return tweetManager.getScreenName();
	}

	/**
	 * twitter全体からキーワード検索ダイアログを表示
	 *
	 * @return
	 */
	public KeywordSearchDialog getKeywordSearchDialog() {
		if (keywordSearchDialog == null) {
			keywordSearchDialog = new KeywordSearchDialog(mainFrame, true, this);
		}
		return keywordSearchDialog;
	}

        /**
         * ユーザ検索ダイアログを表示
         * @return
         */
        public UserSearchDialog getUserSearchDialog() {
            if( this.userSearchDialog == null ) {
                this.userSearchDialog = new UserSearchDialog(mainFrame, true, this);
            }
            return this.userSearchDialog;
        }

        /**
         * CSVログ出力ダイアログを表示
         * @return
         */
        public OutputCSVLogDialog getOutputCSVLogDialog() {
            if( this.outputCSVLogDialog == null ) {
                this.outputCSVLogDialog = new OutputCSVLogDialog(mainFrame, true, this);
            }
            return this.outputCSVLogDialog;
        }

        /**
         * Following follower表示ダイアログ
         * @return
         */
        public FollowingFollowerDialog getFollowingFollowerDialog() {
            if( this.followingFollowerDialog == null ) {
                this.followingFollowerDialog =
                        new FollowingFollowerDialog(mainFrame, true, this.tweetManager);
            }
            return this.followingFollowerDialog;
        }

	/**
	 * hashtag検索ダイアログ
	 *
	 * @return
	 */
	public HashtagSearchDialog getHashtagSearchDialog() {
		if (hashtagSearchDialog == null) {
			hashtagSearchDialog = new HashtagSearchDialog(mainFrame, true,
					this, tweetManager);
		}
		return hashtagSearchDialog;
	}

	/**
	 * アカウント情報設定ダイアログを取得
	 *
	 * @return
	 */
	public AccountDialog getAccountDialog() {
		if (accountDialog == null) {
			accountDialog = new AccountDialog(mainFrame, true, tweetManager,
					this);
		}
		return accountDialog;
	}

	/**
	 * ダイレクトメッセージ送信用ダイアログを取得
	 *
	 * @return
	 */
	public DirectMessageDialog getDirectMessageDialog() {
		if (directMessageDialog == null) {
			directMessageDialog = new DirectMessageDialog(mainFrame);
			directMessageDialog.setTitle("ダイレクトメッセージを送信");
		}
		return directMessageDialog;
	}

	/**
	 * テーブルで選択したツイートを詳細情報としてセット
	 *
	 * @param table
	 */
	public void setDetailInformationFromTable(JTable table) {
		int sc = table.getSelectedRowCount();
		String infoMessage = "";

		// 選択している行が1行だけの場合，情報を表示する
		if (sc == 1 && table != null) {
			Status st = getTweetTableInformation(table, table.getModel());
			// RTの場合，もとの発言を表示
			if (st.isRetweet()) {
				st = st.getRetweetedStatus();
			}
			infoMessage = st.getText();
			// メッセージのHTMLエンコードを行う
			infoMessage = HTMLEncode.encode(infoMessage);
			// tweetMessageBox内のURLをhtmlリンクへ変換
			infoMessage = actionReplaceTweetMessageBoxURLLink(infoMessage);
			// @ユーザ情報をhtmlリンクへ変換
			infoMessage = actionReplaceTweetMessageBoxUserInfo(infoMessage);
			// #ハッシュタグ情報をhtmlリンクへ変換
			infoMessage = actionReplaceTweetMessageBoxHashTab(infoMessage);
			// 詳細情報にテーブルで選択した人のツイート情報を表示
			tweetMessageBox.setText(infoMessage);
			// user icon
			// アイコンをキャッシュから取得
			ImageIcon icon = TwitterImageCache.getInstance().getProfileImage(
					st.getUser().getProfileImageURL().toString());
			userImageLabel.setIcon(icon);
			// user name and id
			userNameLabel.setText(st.getUser().getName() + " / "
					+ st.getUser().getScreenName());
			// update Time
			updateTimeLabel.setText(DateFormat.getInstance().format(
					st.getCreatedAt()));
			// ユーザ自己紹介文
			userIntroBox.setText(st.getUser().getDescription());
			// フォローされている数
			followerLabel.setText(st.getUser().getFollowersCount() + "");
			// フォローしている数
			followingLabel.setText(st.getUser().getFriendsCount() + "");
			// 現在地
			locationLabel.setText(st.getUser().getLocation());
			// Web
			if (st.getUser().getURL() != null) {
				userWebBox.setText("<a href=\"" + st.getUser().getURL() + "\">"
						+ st.getUser().getScreenName() + "のWebを開く" + "</a>");
			} else {
				userWebBox.setText("");
			}
			// client
			clientNameLabel.setText(" via " + st.getSource());
			// Update
			updateLabel.setText(st.getUser().getStatusesCount() + "");
		}
	}

	/**
	 *
	 * @return
	 */
	public AboutDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(mainFrame, true);
		}
		return aboutDialog;
	}

	/**
	 * テーブルで選択した場所のTweet情報を取得
	 *
	 * @return
	 */
	public Status getTweetTableInformation(JTable table, TableModel model) {
		int index = table.convertRowIndexToModel(table.getSelectedRow());
		Status status = null;
		if (model instanceof TweetTableModel) {
			status = ((TweetTableModel) model).getTweetStatus(index);
			// 現在選択したセルのユーザ名を保存しておく
			this.selectedUsername = status.getUser().getScreenName();
			// 現在選択したセルのユーザURLを保存しておく
			this.selectedUserImageURL = status.getUser().getProfileImageURL();
			// 選択したStatusを保存しておく
			this.setCurrentStatus(status);
		}
		return status;
	}

	/**
	 * ステータスバーに情報を表示する
	 *
	 * @param message
	 */
	public void information(String message) {
		statusBarLabel.setText(message);
	}

	/**
	 * 前回タイムラインタブを開いていたか
	 * @return
	 */
	public boolean isTempOpenedTimelineTab() {
		return isTempOpenedTimelineTab;
	}

	/**
	 * 前回メンションタブを開いていたか
	 * @return
	 */
	public boolean isTempOpenedMentionTab() {
		return isTempOpenedMentionTab;
	}

	/**
	 * 前回DMタブを開いていたか
	 * @return
	 */
	public boolean isTempOpenedDMTab() {
		return isTempOpenedDMTab;
	}

	/**
	 * 前回SendDMタブを開いていたか
	 * @return
	 */
	public boolean isTempOpenedSendDMTab() {
		return isTempOpenedSendDMTab;
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
				+ BASIC_SETTING_FILENAME));
		// 設定読み込み
		String gtp = this.property.getProperty("getTimelinePeriod");
		String gmp = this.property.getProperty("getMentionPeriod");
		String gdmp = this.property.getProperty("getDirectMessagePeriod");
		String gsdmp = this.property.getProperty("getSendDirectMessagePeriod");

		String ntrgb = this.property.getProperty("newTableColorRGB");

		this.tlFontName = this.property.getProperty("tlFontName");
		this.detailFontName = this.property.getProperty("detailFontName");

		String tfs = this.property.getProperty("tlFontSize");
		String dfs = this.property.getProperty("detailFontSize");
		String teh = this.property.getProperty("tableElementHeight");

		// メインフレームの大きさ
		String mfw = this.property.getProperty("mainFrameWidth");
		String mfh = this.property.getProperty("mainFrameHeight");

		// メッセージ通知を行うか
		String nm = this.property.getProperty("notifyMention");
		String ndm = this.property.getProperty("notifyDirectMessage");

		//前回開いていたタブの情報
		String ptl = this.property.getProperty("openTimelineTab");
		String pm = this.property.getProperty("openMentionTab");
		String podm = this.property.getProperty("openDirectMessageTab");
		String posdmt = this.property.getProperty("openSendDirectMessageTab");
                
                //ログ
                String log = this.property.getProperty("saveLog");
                
                //表示可能ツイート数
                String nost = this.property.getProperty("numOfShowTweet");

		try {
			this.newTableColor = new Color(Integer.parseInt(ntrgb));
			this.tlFontSize = Integer.parseInt(tfs);
			this.detailFontSize = Integer.parseInt(dfs);
			this.tableElementHeight = Integer.parseInt(teh);
			this.mainFrameWidth = Integer.parseInt(mfw);
			this.mainFrameHeight = Integer.parseInt(mfh);

			// 更新間隔
			this.getTimelinePeriod = Integer.parseInt(gtp);
			this.getMentionPeriod = Integer.parseInt(gmp);
			this.getDirectMessagePeriod = Integer.parseInt(gdmp);
			this.getSendDirectMessagePeriod = Integer.parseInt(gsdmp);

			// 通知関係
			this.isNotifyMentionMessage = Boolean.parseBoolean(nm);
			this.isNotifyDirectMessage = Boolean.parseBoolean(ndm);

			//前回開いていたタブ情報
			this.isTempOpenedTimelineTab = Boolean.parseBoolean(ptl);
			this.isTempOpenedMentionTab = Boolean.parseBoolean(pm);
			this.isTempOpenedDMTab = Boolean.parseBoolean(podm);
			this.isTempOpenedSendDMTab = Boolean.parseBoolean(posdmt);
                        
                        //ログ
                        this.saveLog = Boolean.parseBoolean(log);
                        
                        //表示可能ツイート数
                        this.tableElementMaxSize = Integer.parseInt(nost);
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

		// 情報更新間隔
		this.property.setProperty("getTimelinePeriod", this.getTimelinePeriod
				+ "");
		this.property.setProperty("getMentionPeriod", this.getMentionPeriod
				+ "");
		this.property.setProperty("getDirectMessagePeriod",
				this.getDirectMessagePeriod + "");
		this.property.setProperty("getSendDirectMessagePeriod",
				this.getSendDirectMessagePeriod + "");

		this.property.setProperty("newTableColorRGB", newTableColor.getRGB()
				+ "");
		this.property.setProperty("tlFontName", this.tlFontName);
		this.property.setProperty("tlFontSize", this.tlFontSize + "");
		this.property.setProperty("detailFontName", this.detailFontName);
		this.property.setProperty("detailFontSize", this.detailFontSize + "");
		this.property.setProperty("tableElementHeight", this.tableElementHeight
				+ "");

		// main frame size
		if (this.mainFrame.getExtendedState() == JFrame.NORMAL) {
			this.mainFrameWidth = this.mainFrame.getWidth();
			this.mainFrameHeight = this.mainFrame.getHeight();
		}
		this.property.setProperty("mainFrameWidth", this.mainFrameWidth + "");
		this.property.setProperty("mainFrameHeight", this.mainFrameHeight + "");

		// メッセージ通知を行うか
		this.property.setProperty("notifyMention", this.isNotifyMentionMessage
				+ "");
		this.property.setProperty("notifyDirectMessage",
				this.isNotifyDirectMessage + "");

		//タブの保存
		this.property.setProperty("openTimelineTab", this.isExistTimelineTab() + "");
		this.property.setProperty("openMentionTab", this.isExistMentionTab() + "");
		this.property.setProperty("openDirectMessageTab", this.isExistDirectMessageTab() + "");
		this.property.setProperty("openSendDirectMessageTab", this.isExistSendDirectMessageTab() + "");
                
                //ログを保存するか
                this.property.setProperty("saveLog", this.isSaveLog() + "");
                
                //テーブルに表示可能なツイートの数
                this.property.setProperty("numOfShowTweet", this.getTableElementMaxSize() + "");

		// プロパティのリストを保存
		property.store(new FileOutputStream("./" + PROPERTIES_DIRECTORY + "/"
				+ BASIC_SETTING_FILENAME), null);
	}

	/**
	 * newTableColorを取得します。
	 *
	 * @return newTableColor
	 */
	public Color getNewTableColor() {
		return newTableColor;
	}

	/**
	 * newTableColorを設定します。
	 *
	 * @param newTableColor
	 *            newTableColor
	 */
	public void setNewTableColor(Color newTableColor) {
		this.newTableColor = newTableColor;
	}

	/**
	 * tlFontNameを取得します。
	 *
	 * @return tlFontName
	 */
	public String getTlFontName() {
		return tlFontName;
	}

	/**
	 * tlFontNameを設定します。
	 *
	 * @param tlFontName
	 *            tlFontName
	 */
	public void setTlFontName(String tlFontName) {
		this.tlFontName = tlFontName;
	}

	/**
	 * tlFontSizeを取得します。
	 *
	 * @return tlFontSize
	 */
	public int getTlFontSize() {
		return tlFontSize;
	}

	/**
	 * tlFontSizeを設定します。
	 *
	 * @param tlFontSize
	 *            tlFontSize
	 */
	public void setTlFontSize(int tlFontSize) {
		this.tlFontSize = tlFontSize;
	}

	/**
	 * detailFontNameを取得します。
	 *
	 * @return detailFontName
	 */
	public String getDetailFontName() {
		return detailFontName;
	}

	/**
	 * detailFontNameを設定します。
	 *
	 * @param detailFontName
	 *            detailFontName
	 */
	public void setDetailFontName(String detailFontName) {
		this.detailFontName = detailFontName;
	}

	/**
	 * detailFontSizeを取得します。
	 *
	 * @return detailFontSize
	 */
	public int getDetailFontSize() {
		return detailFontSize;
	}

	/**
	 * detailFontSizeを設定します。
	 *
	 * @param detailFontSize
	 *            detailFontSize
	 */
	public void setDetailFontSize(int detailFontSize) {
		this.detailFontSize = detailFontSize;
	}

	/**
	 * tableElementHeightを取得します。
	 *
	 * @return tableElementHeight
	 */
	public int getTableElementHeight() {
		return tableElementHeight;
	}

	/**
	 * tableElementHeightを設定します。
	 *
	 * @param tableElementHeight
	 *            tableElementHeight
	 */
	public void setTableElementHeight(int tableElementHeight) {
		this.tableElementHeight = tableElementHeight;
	}

	/**
	 * @return the getTimelinePeriod
	 */
	public int getGetTimelinePeriod() {
		return getTimelinePeriod;
	}

	/**
	 *
	 * @param notify
	 */
	public void setNotifyMention(boolean notify) {
		this.isNotifyMentionMessage = notify;
	}

	/**
	 *
	 * @return
	 */
	public boolean getNotifyMention() {
		return this.isNotifyMentionMessage;
	}

	/**
	 *
	 * @param notify
	 * @return
	 */
	public void setNotifyDirectMessage(boolean notify) {
		this.isNotifyDirectMessage = notify;
	}

	/**
	 *
	 * @return
	 */
	public boolean getNotifyDirectMessage() {
		return this.isNotifyDirectMessage;
	}

	/**
	 * @param getTimelinePeriod
	 *            the getTimelinePeriod to set
	 */
	public void setGetTimelinePeriod(int getTimelinePeriod) {
		this.getTimelinePeriod = getTimelinePeriod;
	}

	/**
	 * @return the getMentionPeriod
	 */
	public int getGetMentionPeriod() {
		return getMentionPeriod;
	}

	/**
	 * @param getMentionPeriod
	 *            the getMentionPeriod to set
	 */
	public void setGetMentionPeriod(int getMentionPeriod) {
		this.getMentionPeriod = getMentionPeriod;
	}

	/**
	 * @return the getDirectMessagePeriod
	 */
	public int getGetDirectMessagePeriod() {
		return getDirectMessagePeriod;
	}

	/**
	 * @param getDirectMessagePeriod
	 *            the getDirectMessagePeriod to set
	 */
	public void setGetDirectMessagePeriod(int getDirectMessagePeriod) {
		this.getDirectMessagePeriod = getDirectMessagePeriod;
	}

	/**
	 * @return the getSendDirectMessagePeriod
	 */
	public int getGetSendDirectMessagePeriod() {
		return getSendDirectMessagePeriod;
	}

	/**
	 * @param getSendDirectMessagePeriod
	 *            the getSendDirectMessagePeriod to set
	 */
	public void setGetSendDirectMessagePeriod(int getSendDirectMessagePeriod) {
		this.getSendDirectMessagePeriod = getSendDirectMessagePeriod;
	}

	/**
	 * @return the currentStatus
	 */
	public Status getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * @param currentStatus
	 *            the currentStatus to set
	 */
	public void setCurrentStatus(Status currentStatus) {
		this.currentStatus = currentStatus;
	}

	/**
	 *
	 * @return
	 */
	public Status getReplyStatus() {
		return replyStatus;
	}

	/**
	 *
	 * @param status
	 * @return
	 */
	public void setReplyStatus(Status status) {
		this.replyStatus = status;
	}

        /**
         * ログを保存するかどうか
         * @return 
         */
        public boolean isSaveLog() {
            return this.saveLog;
        }
        
        /**
         * ログを保存するかどうか設定
         * @param check 
         */
        public void setSaveLog(boolean check) {
            this.saveLog = check;
        }
        
        /**
         * テーブルに一度に表示できる数を設定
         * @param size 
         */
        public void setTableElementMaxSize(int size) {
            if( size > 0 ) {
                this.tableElementMaxSize = size;
            }
        }
        
        /**
         * テーブルに一度に表示できる数を取得
         * @return 
         */
        public int getTableElementMaxSize() {
            return this.tableElementMaxSize;
        }
}
