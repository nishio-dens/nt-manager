/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.gui.component;

import java.awt.Color;
import java.awt.Point;
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
    //何番目のタブにテーブルをセットするか
    private int tabSetNum;
    //スクロールペーン
    private JScrollPane scrollPane;
    //ツイートを管理するクラス
    private TweetManager tweetManager;
    //メインアクション
    private TweetMainAction mainAction;
    //右クリックを押した時のポップアップ
    private JPopupMenu rightClickPopup = null;
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
     * @param tabSetNum 何番目のタブにセットするか
     * @param tableElementHeight テーブルの高さ
     * @param tweetManager　ツイート管理クラス
     * @param mainAction メインアクション
     * @param newTableColor 新しく取得した部分の色
     * @param tableElementMaxSize テーブルに格納できる要素の最大数
     * @param timerID 自動更新につかうタイマーのID
     */
    public TweetTabbedTable(TweetGetter tweetGetter,
            String title, JTabbedPane tabbedPane, int tabSetNum, int tableElementHeight,
            TweetManager tweetManager, TweetMainAction mainAction, Color newTableColor,
            int tableElementMaxSize, String timerID) {
        this.tweetGetter = tweetGetter;
        this.title = title;
        this.tabbedPane = tabbedPane;
        this.tabSetNum = tabSetNum;
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
        table.setModel(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
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
        createTweetTable(table);

        //スクロールペーン追加
        scrollPane.setViewportView(table);
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
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {

                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        //テーブルで選択した要素を詳細情報として表示
                        mainAction.setDetailInformationFromTable(table);
                    }
                });
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
                // }
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
                this.table.setRowHeight(0, getTableElementHeight());
            }
            // 新規した部分の背景色を変更
            TableCellRenderer renderer = table.getCellRenderer(0, 2);
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

            getModel().refreshTime();
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
        if (rightClickPopup == null) {
            rightClickPopup = new JPopupMenu();
            JMenuItem directMessageMenuItem = new JMenuItem("ダイレクトメッセージを送信");
            directMessageMenuItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // ダイレクトメッセージ送信ダイアログを表示
                    mainAction.actionShowDirectMessageDialog();
                }
            });
            rightClickPopup.add(directMessageMenuItem);
            JMenuItem retweetMenuItem = new JMenuItem("発言を公式リツイート");
            retweetMenuItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // 選択したセルのステータスをRetweet
                    mainAction.actionRetweet();
                }
            });
            rightClickPopup.add(retweetMenuItem);
            JMenuItem quoteMenuItem = new JMenuItem("発言をコメント付きリツイート");
            quoteMenuItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // 選択したセルのステータスをコメント付Retweet
                    mainAction.actionCopySelectedStatusToTweetBoxPane();
                }
            });
            rightClickPopup.add(quoteMenuItem);

            JMenuItem statusBrowserMenuItem = new JMenuItem("発言をブラウザで開く");
            statusBrowserMenuItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // 選択したセルのステータスをブラウザで開く
                    mainAction.actionOpenStatusURL();
                }
            });
            rightClickPopup.add(statusBrowserMenuItem);

            JMenuItem openBrowserUserInformationMenuItem = new JMenuItem(
                    "この人のTimelineをブラウザで開く");
            openBrowserUserInformationMenuItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // 選択したセルのユーザ情報をブラウザで開く
                    mainAction.actionOpenUserURL();
                }
            });
            rightClickPopup.add(openBrowserUserInformationMenuItem);
        }
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
     * @return the tabSetNum
     */
    public int getTabSetNum() {
        return tabSetNum;
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
     * @param tabSetNum the tabSetNum to set
     */
    public void setTabSetNum(int tabSetNum) {
        this.tabSetNum = tabSetNum;
    }

    /**
     * @return the timerID
     */
    public String getTimerID() {
        return timerID;
    }

}
