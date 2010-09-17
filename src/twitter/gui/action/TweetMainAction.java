package twitter.gui.action;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.TableModel;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import twitter.action.TweetDirectMessageGetter;
import twitter.action.TweetGetter;
import twitter.action.TweetMentionGetter;
import twitter.action.TweetSearchResultGetter;
import twitter.action.TweetSendDirectMessageGetter;
import twitter.action.TweetTimelineGetter;
import twitter.gui.component.DnDTabbedPane;

import twitter.gui.component.TweetTabbedTable;
import twitter.gui.component.TweetTableModel;
import twitter.gui.form.AboutDialog;
import twitter.gui.form.AccountDialog;
import twitter.gui.form.ConfigurationDialog;
import twitter.gui.form.DirectMessageDialog;
import twitter.gui.form.KeywordSearchDialog;
import twitter.manage.TweetConfiguration;
import twitter.manage.TweetManager;
import twitter.task.ExistTimerIDException;
import twitter.task.TimerID;
import twitter.task.TweetTaskException;
import twitter.task.TweetTaskManager;
import twitter.task.TweetUpdateTask;
import twitter4j.Status;
import twitter4j.TwitterException;

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
    private static final String SEARCH_TWITTER_URL = "http://search.twitter.com/";
    // Direct Messageタブに表示する文字
    public static final String TAB_DIRECT_MESSAGE_STRING = "Message";
    // Mentionタブに表示する文字
    public static final String TAB_MENTION_STRING = "Mention";
    // タイムラインタブに表示する文字
    public static final String TAB_TIMELINE_STRING = "Timeline";
    //Send Direct Messageタブに表示する文字
    public static final String TAB_SEND_DIRECT_MESSAGE_STRING = "Send";
    // テーブルのデータ量が以下の値を超えたら古いデータから削除
    private static final int TABLE_ELEMENT_MAX_SIZE = 200;
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
    //Twitter全体からキーワード検索ダイアログ
    private KeywordSearchDialog keywordSearchDialog = null;
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
    //メインフレームの幅
    private int mainFrameWidth = 729;
    //メインフレームの高さ
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
    //自分がつぶやきを書く領域のスクロールペーン
    private JScrollPane tweetBoxScrollPane = null;
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

    //Tweetの詳細情報を表示する部分
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

    // 新しく取得したtweetでまだ参照していない数
    private int uncheckedTimelineTweetCount = 0;
    private AboutDialog aboutDialog = null;
    //アカウント情報表示ダイアログ
    private AccountDialog accountDialog;
    //ツイートを表示するテーブル管理
    private List<TweetTabbedTable> tweetTabbedTableList = new ArrayList<TweetTabbedTable>();
    //ツイートテーブルの情報を一定間隔で更新するクラスを作成
    private TweetTaskManager tweetTaskManager = new TweetTaskManager();

    //情報更新間隔[sec]
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
    public TweetMainAction(JFrame mainFrame,
            TweetManager tweetManager,
            JLabel statusBarLabel,
            JTextPane tweetBoxPane,
            JScrollPane tweetBoxScrollPane,
            JLabel tweetMessageCountLabel,
            JPanel detailInfoPanel,
            JTabbedPane tweetMainTab,
            JEditorPane tweetMessageBox,
            JLabel userImageLabel,
            JLabel userNameLabel,
            JLabel updateTimeLabel,
            JLabel followerLabel,
            JLabel followingLabel,
            JLabel locationLabel,
            JEditorPane clientNameLabel,
            JLabel updateLabel,
            JEditorPane userIntroBox,
            JEditorPane userWebBox) {
        this.mainFrame = mainFrame;
        this.tweetManager = tweetManager;
        this.statusBarLabel = statusBarLabel;
        this.tweetBoxPane = tweetBoxPane;
        this.tweetMessageCountLabel = tweetMessageCountLabel;
        this.detailInfoPanel = detailInfoPanel;
        this.tweetMainTab = tweetMainTab;
        this.tweetMessageBox = tweetMessageBox;
        this.tweetBoxScrollPane = tweetBoxScrollPane;

        //詳細情報部分
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

        //罰ボタンを押した時のイベントを追加
        if( this.tweetMainTab instanceof DnDTabbedPane ) {
            ((DnDTabbedPane)this.tweetMainTab).setMainAction(this);
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

        //フレームの大きさを反映
        mainFrame.setSize(this.mainFrameWidth, this.mainFrameHeight);
        mainFrame.setPreferredSize(new Dimension(this.mainFrameWidth, this.mainFrameHeight));
    }

    /**
     * Timeline, Mention , DM, SendDMの情報更新間隔を取得し,その情報をテーブルに反映
     */
    public void updatePeriodInformationToComponent() {
        //すべてのテーブルにフォント情報を反映
        for (TweetTabbedTable t : this.tweetTabbedTableList) {
            String timerID = t.getTimerID();
            if( timerID.equals( TimerID.createTimelineID() ) ) {
                //TLの周期情報更新
                this.tweetTaskManager.updateTaskPeriod(timerID, this.getGetTimelinePeriod(), false);
            }else if( timerID.equals( TimerID.createMentionID() ) ) {
                //Mentionの周期情報更新
                this.tweetTaskManager.updateTaskPeriod(timerID, this.getGetMentionPeriod(), false );
            }else if( timerID.equals( TimerID.createDirectMessageID() ) ) {
                //DMの周期情報更新
                this.tweetTaskManager.updateTaskPeriod(timerID, this.getGetDirectMessagePeriod(), false);
            }else if( timerID.equals( TimerID.createSendDirectMessageID() ) ) {
                //SendDMの周期情報更新
                this.tweetTaskManager.updateTaskPeriod(timerID, this.getGetSendDirectMessagePeriod(), false);
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

            //すべてのテーブルにフォント情報を反映
            for( TweetTabbedTable t : this.tweetTabbedTableList) {
                t.getTable().setFont(tlFont);
            }

            // tweetメッセージボックスのフォントはhtmlレベルで変更する必要がある
            this.tweetMessageBox.setFont(detailFont);
            // htmlフォント変更
            HTMLDocument doc = (HTMLDocument) this.tweetMessageBox.getDocument();
            StyleSheet[] style = doc.getStyleSheet().getStyleSheets();
            for (int i = style.length - 1; i >= 0; i--) {
                Style body = style[i].getStyle("body");
                if (body != null) {
                    StyleConstants.setFontFamily(body, detailFont.getFontName());
                    StyleConstants.setFontSize(body, detailFont.getSize());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新しいタブを追加
     * @param timerID TimerIDクラスで生成したタイマーID
     * @param period 情報更新間隔[sec]
     * @param tweetGetter 実行するアクション
     * @param tabTitle 追加するタブのタイトル
     */
    public void actionAddTab(String timerID, int period, TweetGetter tweetGetter, String tabTitle) {
        //周期的に情報を更新する
        if( period > 0 ) {
            try {
                //テーブルを作成
                final TweetTabbedTable table = new TweetTabbedTable(tweetGetter, tabTitle,
                        this.tweetMainTab, 
                        this.tableElementHeight, this.tweetManager,
                        this, newTableColor, TABLE_ELEMENT_MAX_SIZE, timerID);

                this.tweetTaskManager.addTask(timerID, new TweetUpdateTask() {

                    @Override
                    public void runTask() throws TweetTaskException {
                        //ツイート情報を一定間隔で更新
                        table.updateTweetTable();
                    }
                });
                //更新開始
                this.tweetTaskManager.startTask(timerID, period * 1000L);

                //タブにテーブルを追加
                table.addTableToTab();
                //タブリストに追加
                this.tweetTabbedTableList.add(table);
                //searchTable.updateTweetTable();
            } catch (TweetTaskException ex) {
                Logger.getLogger(TweetMainAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //フォント情報を更新
        this.updateFontInformationToComponent();
        //テーブルの高さをすべて更新
        this.updateTableHeight( this.getTableElementHeight() );
    }

    /**
     * mentionタブを追加する
     * @param period 情報更新間隔[sec]
     */
    public void actionAddMentionTab(int period) {
        TimerID timerID = TimerID.getInstance();
        String id = TimerID.createMentionID();
        try {
            //既にIDが存在していたらここで例外発生
            timerID.addID(id);
            //検索結果を表示するタブを生成
            actionAddTab(id, period, new TweetMentionGetter(tweetManager),
                    TweetMainAction.TAB_MENTION_STRING);
        } catch (ExistTimerIDException ex) {
            JOptionPane.showMessageDialog(null, "そのタブは既に存在しています",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * timelineタブを追加する
     * @param period[sec]
     */
    public void actionAddTimelineTab(int period) {
        TimerID timerID = TimerID.getInstance();
        String id = TimerID.createTimelineID();
        try {
            //既にIDが存在していたらここで例外発生
            timerID.addID(id);
            //検索結果を表示するタブを生成
            actionAddTab(id, period, new TweetTimelineGetter(tweetManager),
                    TweetMainAction.TAB_TIMELINE_STRING);
        } catch (ExistTimerIDException ex) {
            JOptionPane.showMessageDialog(null, "そのタブは既に存在しています",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ダイレクトメッセージタブを追加する
     * @param period 更新間隔[sec]
     */
    public void actionAddDirectMessageTab(int period) {
        TimerID timerID = TimerID.getInstance();
        String id = TimerID.createDirectMessageID();
        try {
            //既にIDが存在していたらここで例外発生
            timerID.addID(id);
            //検索結果を表示するタブを生成
            actionAddTab(id, period, new TweetDirectMessageGetter(tweetManager),
                    TweetMainAction.TAB_DIRECT_MESSAGE_STRING);
        } catch (ExistTimerIDException ex) {
            JOptionPane.showMessageDialog(null, "そのタブは既に存在しています",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * SendDMタブを追加する
     * @param period
     */
    public void actionAddSendDirectMessageTab(int period) {
        TimerID timerID = TimerID.getInstance();
        String id = TimerID.createSendDirectMessageID();
        try {
            //既にIDが存在していたらここで例外発生
            timerID.addID(id);
            //検索結果を表示するタブを生成
            actionAddTab(id, period, new TweetSendDirectMessageGetter(tweetManager),
                    TweetMainAction.TAB_SEND_DIRECT_MESSAGE_STRING);
        } catch (ExistTimerIDException ex) {
            JOptionPane.showMessageDialog(null, "そのタブは既に存在しています",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * ツイート検索結果を表示するタブを新しく追加
     * @param searchWord
     * @param period 更新周期[sec] 0以下の場合は更新しない
     */
    public void actionAddNewSearchResultTab(String searchWord, int period) {
        TimerID timerID = TimerID.getInstance();
        String id = TimerID.createSearchTimerID(searchWord);
        try {
            //既にIDが存在していたらここで例外発生
            timerID.addID(id);
            //検索結果を表示するタブを生成
            actionAddTab(id, period, new TweetSearchResultGetter(this.tweetManager, searchWord), searchWord);
        } catch (ExistTimerIDException ex) {
            JOptionPane.showMessageDialog(null, "そのタブは既に存在しています",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        //選択した部分
        this.setReplyStatus( currentStatus );
        // コメントしたユーザ名
        String username = this.getCurrentStatus().getUser().getScreenName();
        this.tweetBoxPane.setText("@" + username + " ");

        //情報表示
        this.information(username + "さんに返信");
    }

    /**
     * 引用Tweet
     */
    public void actionSetQuoteStatusToTweetBoxPane() {
        //選択した部分
        this.setReplyStatus( currentStatus );
        // コメントしたユーザ名
        String username = this.getCurrentStatus().getUser().getScreenName();
        // コメント
        String message = this.getCurrentStatus().getText();
        this.tweetBoxPane.setText("QT @" + username + ": " + message);

        //情報表示
        this.information(username + "さんのメッセージを引用ツイート");
    }

    /**
     * 選択したtweetを非公式RT
     */
    public void actionCopySelectedStatusToTweetBoxPane() {
        // コメントしたユーザ名
        String username = this.getCurrentStatus().getUser().getScreenName();
        // コメント
        String message = this.getCurrentStatus().getText();
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
     * 書き込みメッセージボックスの表示ONOFFボタンを押した時の動作
     * @param e
     */
    public void actionShowTweetboxButton(ActionEvent e) {
        if( this.tweetBoxScrollPane.isVisible() ) {
            this.tweetBoxScrollPane.setVisible(false);
        }else {
            this.tweetBoxScrollPane.setVisible(true);
        }
    }

    /**
     * 選択しているタブを削除
     */
    public void actionRemoveFocusedTabbedTable() {
        int selected = this.tweetMainTab.getSelectedIndex();
        actionRemoveTabbedTable(selected);
    }

    /**
     * 指定した場所にあるタブを削除
     * @param removeTabIndex
     */
    public void actionRemoveTabbedTable(int removeTabIndex) {
        int selected = removeTabIndex;
        Component c = this.tweetMainTab.getComponentAt( removeTabIndex );
        //タブの何番目に消したいテーブルがあるのかと，tweetTabbedTableListの何番目に消したいテーブルがあるのかは違う
        //これを探してくる必要がある

        //選択したタブのテーブルを取得
        int deleteTabIndex = -1;
        for(int i=0 ; i < tweetTabbedTableList.size(); i++ ) {
            TweetTabbedTable table = tweetTabbedTableList.get(i);
            if( selected == table.getTabSetNum() ) {
                //消したいタブが見つかった
                deleteTabIndex = i;
                break;
            }
        }

        if( deleteTabIndex >= 0 ) {
            //タブを削除
            this.tweetMainTab.remove(selected);
            //タブのタイマーID
            String timerID = this.tweetTabbedTableList.get(deleteTabIndex).getTimerID();
            //削除
            this.tweetTabbedTableList.remove(deleteTabIndex);
            //自動更新しているタブを削除
            this.tweetTaskManager.shutdownTask( timerID );
            //ID削除
            TimerID idManager = TimerID.getInstance();
            idManager.removeID(timerID);
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
     * 選択したユーザ情報をブラウザで開く
     */
    public void actionOpenUserURL() {
        try {
            String userName = this.getCurrentStatus().getUser().getScreenName();
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
        //タブに存在する時間情報を更新
        for(TweetTabbedTable t : this.tweetTabbedTableList ) {
            TweetTableModel model = t.getModel();
            if( model != null ) {
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
        Pattern userPtn = Pattern.compile("#[0-9A-Z_]+",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = userPtn.matcher(message);
        /*
         * if( matcher.matches() ) { matcher. }
         */
        // #で始まる情報一覧を抜き出す
        Set<String> findList = new TreeSet<String>();
        while (matcher.find()) {
            findList.add(matcher.group(0));
        }
        // 指定した情報をすべてリンクへ変更
        for (String f : findList) {
            try {
                message = message.replaceAll(f, "<a href=\""
                        + SEARCH_TWITTER_URL + SEARCH_QUERY
                        + URLEncoder.encode(f, DEFAULT_CHARACTER_ENCODING)
                        + "\">" + f + "</a>");
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
     * 現在選択しているステータスを公式Retweet
     */
    public void actionRetweet() {
        Status status = null;
        if( this.getCurrentStatus().isRetweet() ) {
            status = this.getCurrentStatus().getRetweetedStatus();
        }else {
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
        for( TweetTabbedTable t : this.tweetTabbedTableList) {
            t.getTable().setRowHeight(tableElementHeight);
        }
    }

    /**
     * ダイレクトメッセージダイアログ表示
     */
    public void actionShowDirectMessageDialog() {
        // ダイレクトメッセージ送信用ダイアログを開く
        Point loc = getDirectMessageDialog().getLocation();
        //loc.translate(20, 20);
        DirectMessageDialog dialog = getDirectMessageDialog();
        //dialog.setLocation(loc);
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
        //loc.translate(20, 20);
        AboutDialog dialog = getAboutDialog();
        dialog.setLocationRelativeTo(null);
        //dialog.setLocation(loc);
        dialog.setVisible(true);
    }

    /**
     * アカウントダイアログを表示
     */
    public void actionShowAccountDialog() {
        Point loc = getDirectMessageDialog().getLocation();
        //loc.translate(20, 20);
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
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * tweetBoxPaneに書かれた文字をつぶやく
     */
    public void actionTweet() {
        if( this.replyStatus != null ) {
            tweetManager.replyTweet(tweetBoxPane.getText(), this.replyStatus.getId());
        }else {
            tweetManager.tweet(tweetBoxPane.getText());
        }
        //ツイートした旨を表示
        this.information("メッセージをつぶやきました. 発言:" + tweetBoxPane.getText());
        
        tweetBoxPane.setText(""); // テキストをクリア
        
    }

    /**
     * Tweet情報を更新
     *
     * @param e
     */
    public void actionUpdateButton(java.awt.event.ActionEvent e) {
        try {
            //タブ上に存在するテーブルの情報を更新
            for(TweetTabbedTable t : this.tweetTabbedTableList ) {
                String timerID = t.getTimerID();
                this.tweetTaskManager.resetTask(timerID, true);
            }

            // API残り回数を取得
            int remainingHits = tweetManager.getRateLimitStatus().getRemainingHits();
            // 取得したコメント数をステータスバーに表示
            information("新しいツイートを取得しました. (APIリクエスト残数は" + remainingHits
                    + "回です)");
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
        if (len < 0) {
            len = 0;
        }
        tweetMessageCountLabel.setText(len + "");

        //残りつぶやき数140の場合，reply状態も解除する
        if( len == 140 ) {
            this.setReplyStatus(null);
        }
    }

    /**
     * 基本設定用ダイアログを取得
     *
     * @return
     */
    public ConfigurationDialog getConfigurationDialog() {
        if (configurationDialog == null) {
            configurationDialog = new ConfigurationDialog(mainFrame, this);
        }
        return configurationDialog;
    }

    /**
     * twitter全体からキーワード検索ダイアログを表示
     * @return
     */
    public KeywordSearchDialog getKeywordSearchDialog() {
        if( keywordSearchDialog == null ) {
            keywordSearchDialog = new KeywordSearchDialog(mainFrame, true, this);
        }
        return keywordSearchDialog;
    }

    /**
     * アカウント情報設定ダイアログを取得
     * @return
     */
    public AccountDialog getAccountDialog() {
        if( accountDialog == null ) {
            accountDialog = new AccountDialog(mainFrame, true, tweetManager, this);
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
     * @param table
     */
    public void setDetailInformationFromTable(JTable table) {
        int sc = table.getSelectedRowCount();
        String infoMessage = "";

        //選択している行が1行だけの場合，情報を表示する
        if (sc == 1 && table != null) {
            Status st = getTweetTableInformation(table, table.getModel());
            infoMessage = st.getText();
            // tweetMessageBox内のURLをhtmlリンクへ変換
            infoMessage = actionReplaceTweetMessageBoxURLLink(infoMessage);
            // @ユーザ情報をhtmlリンクへ変換
            infoMessage = actionReplaceTweetMessageBoxUserInfo(infoMessage);
            // #ハッシュタグ情報をhtmlリンクへ変換
            infoMessage = actionReplaceTweetMessageBoxHashTab(infoMessage);
            // 詳細情報にテーブルで選択した人のツイート情報を表示
            tweetMessageBox.setText(infoMessage);
            // user icon
            userImageLabel.setIcon(new ImageIcon(st.getUser().getProfileImageURL()));
            // user name and id
            userNameLabel.setText(st.getUser().getName()
                    + " / " + st.getUser().getScreenName());
            // update Time
            updateTimeLabel.setText(DateFormat.getInstance().format( st.getCreatedAt() ));
            // ユーザ自己紹介文
            userIntroBox.setText(st.getUser().getDescription());
            // フォローされている数
            followerLabel.setText(st.getUser().getFollowersCount()
                    + "");
            // フォローしている数
            followingLabel.setText(st.getUser().getFriendsCount()
                    + "");
            // 現在地
            locationLabel.setText(st.getUser().getLocation());
            // Web
            if (st.getUser().getURL() != null) {
                userWebBox.setText("<a href=\""
                        + st.getUser().getURL() + "\">"
                        + st.getUser().getScreenName()
                        + "のWebを開く" + "</a>");
            } else {
                userWebBox.setText("");
            }
            // client
            clientNameLabel.setText(" via " + st.getSource());
            // Update
            updateLabel.setText(st.getUser().getStatusesCount()
                    + "");
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

        //メインフレームの大きさ
        String mfw = this.property.getProperty("mainFrameWidth");
        String mfh = this.property.getProperty("mainFrameHeight");

        try {
            this.newTableColor = new Color(Integer.parseInt(ntrgb));
            this.tlFontSize = Integer.parseInt(tfs);
            this.detailFontSize = Integer.parseInt(dfs);
            this.tableElementHeight = Integer.parseInt(teh);
            this.mainFrameWidth = Integer.parseInt(mfw);
            this.mainFrameHeight = Integer.parseInt(mfh);

            //更新間隔
            this.getTimelinePeriod = Integer.parseInt(gtp);
            this.getMentionPeriod = Integer.parseInt(gmp);
            this.getDirectMessagePeriod = Integer.parseInt(gdmp);
            this.getSendDirectMessagePeriod = Integer.parseInt(gsdmp);
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

        //情報更新間隔
        this.property.setProperty("getTimelinePeriod", this.getTimelinePeriod + "");
        this.property.setProperty("getMentionPeriod", this.getMentionPeriod + "");
        this.property.setProperty("getDirectMessagePeriod", this.getDirectMessagePeriod + "");
        this.property.setProperty("getSendDirectMessagePeriod", this.getSendDirectMessagePeriod + "");

        this.property.setProperty("newTableColorRGB", newTableColor.getRGB()
                + "");
        this.property.setProperty("tlFontName", this.tlFontName);
        this.property.setProperty("tlFontSize", this.tlFontSize + "");
        this.property.setProperty("detailFontName", this.detailFontName);
        this.property.setProperty("detailFontSize", this.detailFontSize + "");
        this.property.setProperty("tableElementHeight", this.tableElementHeight
                + "");

        //main frame size
        if( this.mainFrame.getExtendedState() == JFrame.NORMAL ) {
            this.mainFrameWidth = this.mainFrame.getWidth();
            this.mainFrameHeight = this.mainFrame.getHeight();
        }
        this.property.setProperty("mainFrameWidth", this.mainFrameWidth + "");
        this.property.setProperty("mainFrameHeight", this.mainFrameHeight + "");
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
     * @param getTimelinePeriod the getTimelinePeriod to set
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
     * @param getMentionPeriod the getMentionPeriod to set
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
     * @param getDirectMessagePeriod the getDirectMessagePeriod to set
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
     * @param getSendDirectMessagePeriod the getSendDirectMessagePeriod to set
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
     * @param currentStatus the currentStatus to set
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
}
