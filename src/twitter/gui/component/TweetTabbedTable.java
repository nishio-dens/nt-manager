/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.gui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import twitter.action.TweetGetter;
import twitter.gui.action.TweetMainAction;
import twitter.manage.TweetManager;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * ツイートを表示するテーブルを扱うクラス
 * @author nishio
 */
public class TweetTabbedTable {
    //ツイートを表示するテーブル
    private JTable table;
    //ツイートを表示するテーブルのモデル
    private TweetTableModel model;
    //未読のツイート数
    private int uncheckedTweet;
    //ツイートを取得する時に行うアクション
    private TweetGetter tweetGetter;
    //タブに表示される名前
    private String title;
    //タブ
    private JTabbedPane tabbedPane;
    //スクロールペーン
    private JScrollPane scrollPane;
    //ツイートを管理するクラス
    private TweetManager tweetManager;
    //メインアクション
    private TweetMainAction mainAction;
    //テーブルの高さ
    private int tableElementHeight;
    //新しく取得した部分のテーブルの色
    private Color newTableColor = null;
    //テーブルに追加できる要素の最大数
    private int tableElementMaxSize = 200;
    //自動更新に使うタイマーのID
    private String timerID;


    /**
     *
     * @param tweetGetter tweet取得時に行うアクション
     * @param title　タブに表示するタイトル
     * @param tabbedPane　テーブルを追加するタブ
     * @param tableElementHeight テーブルの高さ
     * @param tweetManager　ツイート管理クラス
     * @param mainAction メインアクション
     * @param newTableColor 新しく取得した部分の色
     * @param tableElementMaxSize テーブルに格納できる要素の最大数
     * @param timerID 自動更新につかうタイマーのID
     */
    public TweetTabbedTable(TweetGetter tweetGetter,
            String title, JTabbedPane tabbedPane, int tableElementHeight,
            TweetManager tweetManager, TweetMainAction mainAction, Color newTableColor,
            int tableElementMaxSize, String timerID) {
        this.tweetGetter = tweetGetter;
        this.title = title;
        this.tabbedPane = tabbedPane;
        this.tweetManager = tweetManager;
        this.mainAction = mainAction;
        this.tableElementHeight = tableElementHeight;
        this.newTableColor = newTableColor;
        this.tableElementMaxSize = tableElementMaxSize;
        this.timerID = timerID;

        table = new JTable();
        model = new TweetTableModel();
        uncheckedTweet = 0;
        scrollPane = new JScrollPane();
    }

    /**
     * Tweet情報を表示するテーブルをタブに追加
     */
    public void addTableToTab() {
        //テーブルをタブに追加
        getTable().setModel(model);
        getTable().getTableHeader().setReorderingAllowed(false);
        getTable().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableMousePressed(evt);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableMouseReleased(evt);
            }
        });

        //tweetを表示するテーブルを作成
        createTweetTable(getTable());

        //スクロールペーン追加
        scrollPane.setViewportView(getTable());
        //タブにテーブル追加
        tabbedPane.addTab(this.title, scrollPane);
    }

    /**
     * Tweetを表示するテーブルを作成
     *
     * @param model
     * @return
     */
    private void createTweetTable(final JTable table) {

        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        // Comment部分のColumnを複数行コメントが表示できるようにする
        TableColumnModel mdl = table.getColumnModel();
        TableColumn col = mdl.getColumn(1);
        TweetCommentRenderer commentRenderer = new TweetCommentRenderer();
        col.setCellRenderer(commentRenderer);
        // INfo部分のColumnを複数行表示できるように
        TweetCommentRenderer infoRenderer = new TweetCommentRenderer();
        col = mdl.getColumn(2);
        col.setCellRenderer(infoRenderer);
        col.setMaxWidth(200);
        col.setMinWidth(150);
        // TODO:とりあえず幅指定した部分
        // あとでファイルに幅情報などを保存しておき，それを読み込んで設定するような仕様に変更する
        // ユーザImageを表示する部分
        col = mdl.getColumn(0);
        col.setCellRenderer(new UserImageRenderer());
        col.setMinWidth(50);
        col.setMaxWidth(50);
        // 選択したセルの情報をDetailInfoへと表示
        // JTableを右クリックでも選択できるようにする
        // また，同じ行を２回クリックできるようにする
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                // いったんSelectしていた情報を削除
                table.clearSelection();
                // if (e.getButton() == MouseEvent.BUTTON3) {
                Point p = e.getPoint();
                int col = table.columnAtPoint(p);
                int row = table.rowAtPoint(p);
                table.changeSelection(row, col, false, false);

                //テーブルで選択した要素を詳細情報として表示
                mainAction.setDetailInformationFromTable(table);
                // }
            }
        });
        //キー受付
        table.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent ke) {
                mainAction.setDetailInformationFromTable(table);
            }

        });
        // MouseEventを追加
        table.addMouseListener(commentRenderer);
        table.addMouseMotionListener(commentRenderer);
        table.addMouseListener(infoRenderer);
        table.addMouseMotionListener(infoRenderer);
    }

    /**
     * テーブル情報更新
     */
    public void updateTweetTable() {
        try {
            // API残り回数を取得
            int remainingHits = tweetManager.getRateLimitStatus().getRemainingHits();
            if (remainingHits <= 0) {
                return;
            }
            // ツイート情報
            List<Status> tweet = tweetGetter.getNewTweetData();
            // まだ見ていないtweet数を追加
            this.setUncheckedTweet(this.getUncheckedTweet() + tweet.size());

            // まだチェックしていないtweetの数をタブにも表示
            if (this.getUncheckedTweet() > 0) {
                tabbedPane.setTitleAt(this.getTabSetNum(), this.title + "("
                        + this.getUncheckedTweet() + ")");
            }
            // ツイートをテーブルに追加
            for (Status t : tweet) {
                this.getModel().insertTweet(t);
                this.getTable().setRowHeight(0, getTableElementHeight());
            }
            // 新規した部分の背景色を変更
            TableCellRenderer renderer = getTable().getCellRenderer(0, 2);
            if (renderer instanceof TweetCommentRenderer) {
                if (this.getUncheckedTweet() - 1 >= 0) {
                    ((TweetCommentRenderer) renderer).updateNewCellRow(
                            this.getUncheckedTweet(), newTableColor);
                } else {
                    ((TweetCommentRenderer) renderer).updateNewCellRow(-1,
                            newTableColor);
                }
            }
            // 古いデータを削除
            getModel().removeOldTweet( getTableElementMaxSize() );
            //時間情報リフレッシュ
            getModel().refreshTime();
            //新しい情報
            int newNum = 0;
            if( tweet != null ) {
                newNum = tweet.size();
            }
            //情報を取得したことをステータスバーに表示
            mainAction.information( this.getTitle() + "タブのツイートを" + newNum +
                    "件取得しました. (APIリクエスト残数は" + remainingHits
                    + "回です)");

            //情報間隔毎に設定を保存
            this.mainAction.saveProperties();
        } catch (TwitterException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * マウスクリック時の動作
     * @param evt
     */
    private void jTableMousePressed(java.awt.event.MouseEvent evt) {
        //右クリックメニュー表示
        showPopup(evt);
        //未読ツイート分を0にする
        this.setUncheckedTweet(0);
        this.setTitle( this.getTitle() );
    }

    /**
     * マウスリリース時の動作
     * @param evt
     */
    private void jTableMouseReleased(java.awt.event.MouseEvent evt) {
        //右クリックメニュー表示
        showPopup(evt);
    }

    /**
     * ポップアップメニューを作成
     * @param e
     */
    private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            // 右クリックのメニューを表示
            getRightClickPopup().show(e.getComponent(), e.getX(),
                    e.getY());
        }
    }

    /**
     * 右クリックを押した時のポップアップメニューを取得
     *
     * @return
     */
    private JPopupMenu getRightClickPopup() {
        //if (rightClickPopup == null) {
        JPopupMenu rightClickPopup = new JPopupMenu();

        JMenuItem replyMenuItem = new JMenuItem("この発言に返信(Reply)");
        replyMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 選択したセルのステータスにreply
                mainAction.actionSetReplyStatusToTweetBoxPane();
            }
        });
 
        JMenuItem retweetMenuItem = new JMenuItem("発言を公式リツイート(RT)");
        retweetMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 選択したセルのステータスをRetweet
                mainAction.actionRetweet();
            }
        });
       
        JMenuItem quoteMenuItem = new JMenuItem("発言を引用ツイート(QT)");
        quoteMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 選択したセルのステータスをQT
                mainAction.actionSetQuoteStatusToTweetBoxPane();
            }
        });
        
        JMenuItem unofficialRetweetMenuItem = new JMenuItem("発言をコメント付きリツイート(非公式RT)");
        unofficialRetweetMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 選択したセルのステータスをコメント付Retweet
                mainAction.actionCopySelectedStatusToTweetBoxPane();
            }
        });

        JMenuItem directMessageMenuItem = new JMenuItem("ダイレクトメッセージを送信");
        directMessageMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // ダイレクトメッセージ送信ダイアログを表示
                mainAction.actionShowDirectMessageDialog();
            }
        });

        JMenuItem statusBrowserMenuItem = new JMenuItem("この発言をブラウザで開く");
        statusBrowserMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 選択したセルのステータスをブラウザで開く
                mainAction.actionOpenStatusURL();
            }
        });
        
        JMenuItem createFavMenuItem = new JMenuItem(
                "この発言をお気に入りに追加");
        createFavMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 選択したセルのユーザ情報をブラウザで開く
                mainAction.actionCreateFavorite();
            }
        });

        JMenuItem destroyFavMenuItem = new JMenuItem(
                "この発言をお気に入りから削除");
        destroyFavMenuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 選択したセルのユーザ情報をブラウザで開く
                mainAction.actionDestroyFavorite();
            }
        });


        //指定した発言がRTかどうか判定
        int sc = table.getSelectedRowCount();
        if (sc == 1 && table != null) {
            Status st = mainAction.getTweetTableInformation(table, table.getModel());

            JMenuItem openBrowserUserInformationMenuItem = new JMenuItem(
                    "この人のTimelineをブラウザで開く");
            openBrowserUserInformationMenuItem.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // 選択したセルのユーザ情報をブラウザで開く
                    mainAction.actionOpenUserURL();
                }
            });

            JMenuItem openFavMenuItem = new JMenuItem(
                   "この人のお気に入りを開く");
            openFavMenuItem.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // 選択したセルのユーザ情報をブラウザで開く
                    mainAction.actionOpenUserFav();
                }
            });


            //メニューアイテムを追加
            //返信
            rightClickPopup.add(replyMenuItem);
            //公式RT
            rightClickPopup.add(retweetMenuItem);
            //QT
            rightClickPopup.add(quoteMenuItem);
            //非公式RT
            rightClickPopup.add(unofficialRetweetMenuItem);
            //ダイレクトメッセージ
            rightClickPopup.add(directMessageMenuItem);
            //発言をブラウザで開く
            rightClickPopup.add(statusBrowserMenuItem);
            //この人のtimelineを開く
            rightClickPopup.add(openBrowserUserInformationMenuItem);
            //この人のfavを開く
            rightClickPopup.add(openFavMenuItem);

            try {
                if (st.isRetweet()) {
                    //Retweetのときのみ表示するメニュー
                }
                if (st.isFavorited()) {
                    //お気に入りに追加されている時のみ表示するメニュー
                    //お気に入り追加
                    rightClickPopup.add(destroyFavMenuItem);
                } else {
                    rightClickPopup.add(createFavMenuItem);
                }
            }catch(Exception e) {
                //TODO:ここの無視部分をなんとかする
                //DMのときはisFavoritedができない
            }
        }
        //}
        return rightClickPopup;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
        this.tabbedPane.setTitleAt( this.getTabSetNum() , title);
    }

    /**
     * 自分自信がタブのどの場所に位置しているのかを取得
     * @return
     */
    public int getTabSetNum() {
        int tabCount = this.tabbedPane.getTabCount();
        for(int i=0; i < tabCount; i++) {
            Component c = this.tabbedPane.getComponentAt(i);
            if( c instanceof JScrollPane ) {
                if ( c == this.scrollPane ) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * @return the tableElementHeight
     */
    public int getTableElementHeight() {
        return tableElementHeight;
    }

    /**
     * @param tableElementHeight the tableElementHeight to set
     */
    public void setTableElementHeight(int tableElementHeight) {
        this.tableElementHeight = tableElementHeight;
    }

    /**
     * @return the tableElementMaxSize
     */
    public int getTableElementMaxSize() {
        return tableElementMaxSize;
    }

    /**
     * @param tableElementMaxSize the tableElementMaxSize to set
     */
    public void setTableElementMaxSize(int tableElementMaxSize) {
        this.tableElementMaxSize = tableElementMaxSize;
    }

    /**
     * @return the model
     */
    public TweetTableModel getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(TweetTableModel model) {
        this.model = model;
    }


    /**
     * @return the uncheckedTweet
     */
    public int getUncheckedTweet() {
        return uncheckedTweet;
    }

    /**
     * @param uncheckedTweet the uncheckedTweet to set
     */
    public void setUncheckedTweet(int uncheckedTweet) {
        this.uncheckedTweet = uncheckedTweet;
    }

    /**
     * @return the timerID
     */
    public String getTimerID() {
        return timerID;
    }

    /**
     * @return the table
     */
    public JTable getTable() {
        return table;
    }

}
