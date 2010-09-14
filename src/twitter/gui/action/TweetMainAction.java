package twitter.gui.action;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
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
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import twitter.action.TweetGetter;
import twitter.action.TweetSearchResultGetter;

import twitter.gui.component.TweetCommentRenderer;
import twitter.gui.component.TweetTabbedTable;
import twitter.gui.component.TweetTableModel;
import twitter.gui.form.AboutDialog;
import twitter.gui.form.AccountDialog;
import twitter.gui.form.ConfigurationDialog;
import twitter.gui.form.DirectMessageDialog;
import twitter.gui.form.KeywordSearchDialog;
import twitter.manage.TweetConfiguration;
import twitter.manage.TweetManager;
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

    /**
     * 一定時間毎にtweet情報をアップデートするタスク
     *
     * @author nishio
     *
     */
    private class TweetAutoUpdateTask implements Runnable {

        TweetAutoUpdateTask() {
        }

        public void run() {
            // 一定時間ごとにTweet情報をアップデート
            try {
                if (currentGetTimelinePeriodNum == 0) {
                    // Tweetテーブルの情報を更新
                    actionTweetTableUpdate();
                }
                currentGetTimelinePeriodNum = (currentGetTimelinePeriodNum + 1)
                        % getTimelinePeriodNum;

                if (currentGetMentionPeriodNum == 0) {
                    // Mentionテーブルの情報を更新
                    actionMentionTableUpdate();
                }
                currentGetMentionPeriodNum = (currentGetMentionPeriodNum + 1)
                        % getMentionPeriodNum;

                if (currentGetDirectMessagePeriodNum == 0) {
                    // DirectMessageテーブルの情報を更新
                    actionDirectMessageTableUpdate();
                }
                currentGetDirectMessagePeriodNum = (currentGetDirectMessagePeriodNum + 1)
                        % getDirectMessagePeriodNum;

                if (currentGetSendDirectMessagePeriodNum == 0) {
                    // SendDirectMessageテーブルの情報を更新
                    actionSendDirectMessageTableUpdate();
                }
                currentGetSendDirectMessagePeriodNum = (currentGetSendDirectMessagePeriodNum + 1)
                        % getSendDirectMessagePeriodNum;

                //設定ファイルを保存
                saveProperties();

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 一定時間毎にtweet情報をアップデートする
     *
     * @author nishio
     *
     */
    private class TweetAutoUpdateTimer {

        private ScheduledFuture<?> future;
        private final ScheduledExecutorService scheduler;
        private final Runnable task;
        private long time = 0;

        public TweetAutoUpdateTimer() {
            task = new TweetAutoUpdateTask();
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        /**
         * 更新リセット
         */
        public void reset() {
            stop();
            if (future != null) {
                future = scheduler.scheduleAtFixedRate(task, time, time,
                        TimeUnit.SECONDS);
            }
        }

        /**
         * シャットダウン
         */
        public void shutdown() {
            scheduler.shutdown();
        }

        /**
         * 一定時間毎にTweetUpdateTaskを実行
         *
         * @param time
         *            second単位
         */
        public void start(long time) {
            future = scheduler.scheduleAtFixedRate(task, 2, time,
                    TimeUnit.SECONDS);
            this.time = time;
        }

        /**
         * タスク終了
         */
        public void stop() {
            if (future != null) {
                future.cancel(true);
            }
        }
    }
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
    // テーブルのデータ量が以下の値を超えたら古いデータから削除
    private static final int TABLE_ELEMENT_MAX_SIZE = 200;
    // twitterの公式URL
    private static final String TWITTER_URL = "http://twitter.com/";
    // Tweet情報自動更新タイマー
    private TweetAutoUpdateTimer autoUpdateTimer = null;
    // 基本設定用ダイアログ
    private ConfigurationDialog configurationDialog = null;
    private int currentGetDirectMessagePeriodNum = 0;
    private int currentGetMentionPeriodNum = 0;
    private int currentGetSendDirectMessagePeriodNum = 0;
    private int currentGetTimelinePeriodNum = 0;
    // 現在選択しているStatus情報
    private Status currentStatus = null;
    // 詳細情報パネル
    private JPanel detailInfoPanel = null;
    // ダイレクトメッセージ送信用ダイアログ
    private DirectMessageDialog directMessageDialog = null;
    //Twitter全体からキーワード検索ダイアログ
    private KeywordSearchDialog keywordSearchDialog = null;
    // directMessageを表示するテーブル
    private JTable directMessageTable = null;
    // directMessageのtweetを表示するテーブルモデル
    private TweetTableModel directMessageTableModel = null;
    // 情報アップデート間隔(分)
    private int updatePeriod = 1;
    // DirectMessageの取得間隔
    private int getDirectMessagePeriodNum = 10;
    // Mentionの取得間隔
    private int getMentionPeriodNum = 5;
    // SendDirectMessageの取得間隔
    private int getSendDirectMessagePeriodNum = 30;
    // Timelineの取得間隔
    private int getTimelinePeriodNum = 1;
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
    // メインのtweetを表示するテーブル
    private JTable mainTweetTable = null;
    // mentionを表示するテーブル
    private JTable mentionTable = null;
    // mentionのtweetを表示するテーブルモデル
    private TweetTableModel mentionTableModel = null;
    // 設定
    private Properties property = null;
    // 現在テーブルで選択しているユーザ画像のURL
    private URL selectedUserImageURL = null;
    // 現在テーブルで選択しているユーザの名前
    private String selectedUsername = null;
    // sendDirectMessageを表示するテーブル
    private JTable sendDirectMessageTable = null;
    // sendDirectMessageのtweetを表示するテーブル
    private TweetTableModel sendDirectMessageTableModel = null;
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
    // メインのtweetを表示するテーブルモデル
    private TweetTableModel tweetTableModel = null;
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
    //ここは一時的に追加している部分 タブにすでに存在しているテーブルの数
    private int ALREADY_TWEET_TAB_NUM = 4;

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
            TweetTableModel tweetTableModel,
            TweetTableModel mentionTableModel,
            TweetTableModel directMessageTableModel,
            TweetTableModel sendDirectMessageTableModel,
            JTable mainTweetTable,
            JTable mentionTable,
            JTable directMessageTable,
            JTable sendDirectMessageTable,
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
        this.tweetTableModel = tweetTableModel;
        this.mentionTableModel = mentionTableModel;
        this.directMessageTableModel = directMessageTableModel;
        this.sendDirectMessageTableModel = sendDirectMessageTableModel;
        this.mainTweetTable = mainTweetTable;
        this.mentionTable = mentionTable;
        this.directMessageTable = directMessageTable;
        this.sendDirectMessageTable = sendDirectMessageTable;
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
            this.mainTweetTable.setFont(tlFont);
            this.mentionTable.setFont(tlFont);
            this.directMessageTable.setFont(tlFont);
            this.sendDirectMessageTable.setFont(tlFont);

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
     * ツイート検索結果を表示するタブを新しく追加
     * @param searchWord
     * @param period 更新周期[sec] 0以下の場合は更新しない
     */
    public void actionAddNewSearchResultTab(String searchWord, int period) {
        int numOfTab = this.tweetTabbedTableList.size();
        //すでに追加されているタブの数
        //TODO:ここはあとで変更する必要がある．なぜなら既に追加されているタブの数は変わる可能性があるから
        int alreadyExistTabNum = ALREADY_TWEET_TAB_NUM;

        //周期的に情報を更新する
        if( period > 0 ) {
            //TODO: timerIDを再度検討する必要があるかもしれない
            try {
                //指定したワードを検索してくるアクション
                TweetGetter tweetGetter = new TweetSearchResultGetter(this.tweetManager, searchWord);
                //検索したワードを表示するテーブルを作成,及びタブにそのテーブルを追加
                final TweetTabbedTable searchTable = new TweetTabbedTable(tweetGetter, searchWord,
                        this.tweetMainTab, numOfTab + alreadyExistTabNum,
                        this.tableElementHeight, this.tweetManager,
                        this, newTableColor, tableElementHeight);

                String timerID = "SEARCH:" + searchWord;
                this.tweetTaskManager.addTask(timerID, new TweetUpdateTask() {

                    @Override
                    public void runTask() throws TweetTaskException {
                        //ツイート情報を一定間隔で更新
                        searchTable.updateTweetTable();
                    }
                });
                //更新開始
                this.tweetTaskManager.startTask(timerID, period * 1000L);

                //タブにテーブルを追加
                searchTable.addTableToTab();
                //タブリストに追加
                this.tweetTabbedTableList.add(searchTable);
                //searchTable.updateTweetTable();
            } catch (TweetTaskException ex) {
                Logger.getLogger(TweetMainAction.class.getName()).log(Level.SEVERE, null, ex);
            }
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
     * 選択したtweetをRT
     */
    public void actionCopySelectedStatusToTweetBoxPane() {
        // コメントしたユーザ名
        String username = this.currentStatus.getUser().getScreenName();
        // コメント
        String message = this.currentStatus.getText();
        this.tweetBoxPane.setText("RT: @" + username + ": " + message);
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
     * DirectMessageテーブルの更新
     */
    public void actionDirectMessageTableUpdate() {
        try {
            // API残り回数を取得
            int remainingHits = tweetManager.getRateLimitStatus().getRemainingHits();
            if (remainingHits <= 0) {
                information("API制限です．リクエストの残数が0となりました．");
                return;
            }

            // Direct Message情報

            // DirectMessageを追加
            List<Status> directMessages = tweetManager.getNewDirectMessages();
            // まだ見ていないdirectMessage数を追加
            uncheckedDirectMessageCount += directMessages.size();
            // まだ見ていないmentionの数をタブに表示
            if (uncheckedDirectMessageCount > 0) {
                tweetMainTab.setTitleAt(2, TAB_DIRECT_MESSAGE_STRING + "("
                        + uncheckedDirectMessageCount + ")");
            }
            for (Status t : directMessages) {
                directMessageTableModel.insertTweet(t);
                directMessageTable.setRowHeight(0, tableElementHeight);
            }
            // 古いデータを削除
            directMessageTableModel.removeOldTweet(TABLE_ELEMENT_MAX_SIZE);

            directMessageTableModel.refreshTime();
        } catch (TwitterException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 選択しているタブを削除
     */
    public void actionRemoveFocusedTabbedTable() {
        int selected = this.tweetMainTab.getSelectedIndex();
        //TODO:ここはいつか変更
        int deleteTabIndex = selected - this.ALREADY_TWEET_TAB_NUM;
        if( deleteTabIndex >= 0 ) {
            //タブを削除
            this.tweetMainTab.remove(deleteTabIndex + this.ALREADY_TWEET_TAB_NUM);
            int tabSetNum = this.tweetTabbedTableList.get(deleteTabIndex).getTabSetNum();
            //タブのタイマーID
            String timerID = this.tweetTabbedTableList.get(deleteTabIndex).getTitle();

            //削除
            this.tweetTabbedTableList.remove(deleteTabIndex);

            //一時的に
            tabSetNum -= this.ALREADY_TWEET_TAB_NUM;
            //削除した分，既存のタブ番号を1つずつずらさなければならない
            int tabNum = this.tweetTabbedTableList.size();
            for(int i = tabSetNum; i < tabNum; i++) {
                TweetTabbedTable table = this.tweetTabbedTableList.get( i );
                table.setTabSetNum( table.getTabSetNum() - 1);
            }

            //自動更新しているタブを削除
            timerID = "SEARCH:" + timerID;
            this.tweetTaskManager.shutdownTask( timerID );
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
     * Mentionテーブル情報を更新
     */
    public void actionMentionTableUpdate() {
        try {
            // API残り回数を取得
            int remainingHits = tweetManager.getRateLimitStatus().getRemainingHits();
            if (remainingHits <= 0) {
                information("API制限です．リクエストの残数が0となりました．");
                return;
            }

            // Mention情報

            // Mentionを追加
            List<Status> mention = tweetManager.getNewMentionData();
            // まだ見ていないmention数を追加
            uncheckedMentionTweetCount += mention.size();
            // まだ見ていないmentionの数をタブに表示
            if (uncheckedMentionTweetCount > 0) {
                tweetMainTab.setTitleAt(1, TAB_MENTION_STRING + "("
                        + uncheckedMentionTweetCount + ")");
            }
            for (Status t : mention) {
                mentionTableModel.insertTweet(t);
                mentionTable.setRowHeight(0, tableElementHeight);
            }
            // 新規した部分の背景色を変更
            TableCellRenderer renderer2 = mentionTable.getCellRenderer(0, 2);
            if (renderer2 instanceof TweetCommentRenderer) {
                if (this.uncheckedMentionTweetCount - 1 >= 0) {
                    ((TweetCommentRenderer) renderer2).updateNewCellRow(
                            this.uncheckedMentionTweetCount, newTableColor);
                } else {
                    ((TweetCommentRenderer) renderer2).updateNewCellRow(-1,
                            newTableColor);
                }
            }
            // 古いデータを削除
            mentionTableModel.removeOldTweet(TABLE_ELEMENT_MAX_SIZE);

            mentionTableModel.refreshTime();
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 選択した発言をブラウザで開く
     */
    public void actionOpenStatusURL() {
        try {
            // ユーザ名
            String userName = this.currentStatus.getUser().getScreenName();
            // 発言のstatusID
            long statusID = this.currentStatus.getId();
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
            String userName = this.currentStatus.getUser().getScreenName();
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
        tweetTableModel.refreshTime();
        mentionTableModel.refreshTime();
        directMessageTableModel.refreshTime();
        sendDirectMessageTableModel.refreshTime();

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
        // 選択しているtweetのstatus id
        long statusID = this.currentStatus.getId();
        // コメントしたユーザ名
        String username = this.currentStatus.getUser().getScreenName();
        // コメント
        String message = this.currentStatus.getText();
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
        mainTweetTable.setRowHeight(tableElementHeight);
        mentionTable.setRowHeight(tableElementHeight);
        directMessageTable.setRowHeight(tableElementHeight);
        sendDirectMessageTable.setRowHeight(tableElementHeight);
    }

    /**
     * SendDirectMessageテーブルを更新
     */
    public void actionSendDirectMessageTableUpdate() {
        try {
            // API残り回数を取得
            int remainingHits = tweetManager.getRateLimitStatus().getRemainingHits();
            if (remainingHits <= 0) {
                information("API制限です．リクエストの残数が0となりました．");
                return;
            }
            // Direct Message情報

            List<Status> sendDirectMessages = tweetManager.getNewSendDirectMessages();

            //TODO:ここはnullぽが頻発している．修正の必要あり
            try {
                for (Status t : sendDirectMessages) {
                    sendDirectMessageTableModel.insertTweet(t);
                    sendDirectMessageTable.setRowHeight(0, tableElementHeight);
                }
            }catch(NullPointerException e2) {
                e2.printStackTrace();
            }
            // 古いデータを削除
            sendDirectMessageTableModel.removeOldTweet(TABLE_ELEMENT_MAX_SIZE);

            sendDirectMessageTableModel.refreshTime();
        } catch (TwitterException e1) {
            e1.printStackTrace();
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
        tweetManager.tweet(tweetBoxPane.getText());
        tweetBoxPane.setText(""); // テキストをクリア
    }

    /**
     * Tweetテーブルの情報を更新
     */
    public void actionTweetTableUpdate() {
        try {
            // API残り回数を取得
            int remainingHits = tweetManager.getRateLimitStatus().getRemainingHits();
            if (remainingHits <= 0) {
                information("API制限です．リクエストの残数が0となりました．");
                return;
            }

            // Timeline情報
            List<Status> tweet = tweetManager.getNewTimelineData();
            // まだ見ていないtweet数を追加
            uncheckedTimelineTweetCount += tweet.size();
            // まだチェックしていないtweetの数をタブにも表示
            if (uncheckedTimelineTweetCount > 0) {
                tweetMainTab.setTitleAt(0, TAB_TIMELINE_STRING + "("
                        + uncheckedTimelineTweetCount + ")");
            }
            // Timelineをテーブルに追加
            for (Status t : tweet) {
                tweetTableModel.insertTweet(t);
                mainTweetTable.setRowHeight(0, tableElementHeight);
            }
            // 新規した部分の背景色を変更
            TableCellRenderer renderer = mainTweetTable.getCellRenderer(0, 2);
            if (renderer instanceof TweetCommentRenderer) {
                if (this.uncheckedTimelineTweetCount - 1 >= 0) {
                    ((TweetCommentRenderer) renderer).updateNewCellRow(
                            this.uncheckedTimelineTweetCount, newTableColor);
                } else {
                    ((TweetCommentRenderer) renderer).updateNewCellRow(-1,
                            newTableColor);
                }
            }
            // 古いデータを削除
            tweetTableModel.removeOldTweet(TABLE_ELEMENT_MAX_SIZE);

            // 取得したコメント数をステータスバーに表示
            information(uncheckedTimelineTweetCount
                    + " 件の新しいツイートを取得しました. (APIリクエスト残数は" + remainingHits
                    + "回です)");

            tweetTableModel.refreshTime();
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * メインTweet情報を更新
     *
     * @param e
     */
    public void actionUpdateButton(java.awt.event.ActionEvent e) {
        try {
            // Tweetテーブルの情報を更新
            actionTweetTableUpdate();
            // Mentionテーブルの情報を更新
            actionMentionTableUpdate();
            // DirectMessageテーブルの情報を更新
            actionDirectMessageTableUpdate();
            // SendDirectMessageテーブルの情報を更新
            actionSendDirectMessageTableUpdate();

            //新しく追加したタブ上に存在するテーブルの情報を更新
            for(TweetTabbedTable t : this.tweetTabbedTableList ) {
                t.updateTweetTable();
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
        if (len < 0) {
            len = 0;
        }
        tweetMessageCountLabel.setText(len + "");
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

        if (sc == 1) {
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
     * updatePeriodを取得します。
     *
     * @return updatePeriod
     */
    public int getUpdatePeriod() {
        return updatePeriod;
    }

    /**
     * updatePeriodを設定します。
     *
     * @param updatePeriod
     *            updatePeriod
     */
    public void setUpdatePeriod(int updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    /**
     * getDirectMessagePeriodNumを取得します。
     *
     * @return getDirectMessagePeriodNum
     */
    public int getGetDirectMessagePeriodNum() {
        return getDirectMessagePeriodNum;
    }

    /**
     * getMentionPeriodNumを取得します。
     *
     * @return getMentionPeriodNum
     */
    public int getGetMentionPeriodNum() {
        return getMentionPeriodNum;
    }

    /**
     * getSendDirectMessagePeriodNumを取得します。
     *
     * @return getSendDirectMessagePeriodNum
     */
    public int getGetSendDirectMessagePeriodNum() {
        return getSendDirectMessagePeriodNum;
    }

    /**
     * getTimelinePeriodNumを取得します。
     *
     * @return getTimelinePeriodNum
     */
    public int getGetTimelinePeriodNum() {
        return getTimelinePeriodNum;
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
            this.currentStatus = status;
        }
        return status;
    }

    /**
     * ステータスバーに情報を表示する
     *
     * @param message
     */
    private void information(String message) {
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
        String gtpn = this.property.getProperty("getTimelinePeriodNum");
        String gmpn = this.property.getProperty("getMentionPeriodNum");
        String gdmpn = this.property.getProperty("getDirectMessagePeriodNum");
        String gsdmpn = this.property.getProperty("getSendDirectMessagePeriodNum");
        String up = this.property.getProperty("updatePeriod");
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
            this.getTimelinePeriodNum = Integer.parseInt(gtpn);
            this.getMentionPeriodNum = Integer.parseInt(gmpn);
            this.getDirectMessagePeriodNum = Integer.parseInt(gdmpn);
            this.getSendDirectMessagePeriodNum = Integer.parseInt(gsdmpn);
            this.updatePeriod = Integer.parseInt(up);
            this.newTableColor = new Color(Integer.parseInt(ntrgb));
            this.tlFontSize = Integer.parseInt(tfs);
            this.detailFontSize = Integer.parseInt(dfs);
            this.tableElementHeight = Integer.parseInt(teh);
            this.mainFrameWidth = Integer.parseInt(mfw);
            this.mainFrameHeight = Integer.parseInt(mfh);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tweet情報の自動更新のタイムをリセット
     */
    public void resetTweetAutoUpdate() {
        if (this.autoUpdateTimer != null) {
            this.autoUpdateTimer.reset();
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
        this.property.setProperty("getTimelinePeriodNum",
                this.getTimelinePeriodNum + "");
        this.property.setProperty("getMentionPeriodNum",
                this.getMentionPeriodNum + "");
        this.property.setProperty("getDirectMessagePeriodNum",
                this.getDirectMessagePeriodNum + "");
        this.property.setProperty("getSendDirectMessagePeriodNum",
                this.getSendDirectMessagePeriodNum + "");
        this.property.setProperty("updatePeriod", this.updatePeriod + "");
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
     * getDirectMessagePeriodNumを設定します。
     *
     * @param getDirectMessagePeriodNum
     *            getDirectMessagePeriodNum
     */
    public void setGetDirectMessagePeriodNum(int getDirectMessagePeriodNum) {
        this.getDirectMessagePeriodNum = getDirectMessagePeriodNum;
    }

    /**
     * getMentionPeriodNumを設定します。
     *
     * @param getMentionPeriodNum
     *            getMentionPeriodNum
     */
    public void setGetMentionPeriodNum(int getMentionPeriodNum) {
        this.getMentionPeriodNum = getMentionPeriodNum;
    }

    /**
     * getSendDirectMessagePeriodNumを設定します。
     *
     * @param getSendDirectMessagePeriodNum
     *            getSendDirectMessagePeriodNum
     */
    public void setGetSendDirectMessagePeriodNum(
            int getSendDirectMessagePeriodNum) {
        this.getSendDirectMessagePeriodNum = getSendDirectMessagePeriodNum;
    }

    /**
     * getTimelinePeriodNumを設定します。
     *
     * @param getTimelinePeriodNum
     *            getTimelinePeriodNum
     */
    public void setGetTimelinePeriodNum(int getTimelinePeriodNum) {
        this.getTimelinePeriodNum = getTimelinePeriodNum;
    }

    /**
     * Tweet情報の自動更新スタート
     *
     * @param second
     */
    public void startTweetAutoUpdate() {
        if (this.autoUpdateTimer == null) {
            this.autoUpdateTimer = new TweetAutoUpdateTimer();
        }
        // 一度タイマーストップ
        this.autoUpdateTimer.stop();
        // 自動更新開始
        this.autoUpdateTimer.start(updatePeriod * 60);
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
}
