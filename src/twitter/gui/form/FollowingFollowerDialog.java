/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FollowingFollowerDialog.java
 *
 * Created on 2011/05/15, 21:16:02
 */

package twitter.gui.form;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import twitter.cache.TwitterImageCache;
import twitter.gui.component.TweetHashtagHyperlinkHandler;
import twitter.gui.component.TweetHyperlinkHandler;
import twitter.gui.component.UserTableModel;
import twitter.manage.TweetManager;
import twitter4j.User;

/**
 *
 * @author nishio
 */
public class FollowingFollowerDialog extends javax.swing.JDialog {

    //リストモデル
    private UserTableModel listTableModel = new UserTableModel();
    //tweet管理
    private TweetManager tweetManager = null;
    //取得済み件数
    private int numOfPage = 0;
    //アイコンの大きさ
    private static final int iconSize = 50;
    //hasttag
    private TweetHashtagHyperlinkHandler hashTagHyperlinkListener = new TweetHashtagHyperlinkHandler();
    //前回はfollowing取得か follower取得か
    private int prevGetFollowingFollower = 0;
    //前回取得したユーザ名
    private String prevUsername = "";
    //更新スレッド
    private Thread updateThread = null;
    //現在スレッドが更新中か
    private boolean currentThreadRunState = false;

    /** Creates new form FollowingFollowerDialog */
    public FollowingFollowerDialog(java.awt.Frame parent, boolean modal,
            TweetManager manager) {
        super(parent, modal);
        initComponents();

        this.tweetManager = manager;

        //初期化
        this.numOfPage = 0;
    }

    /**
     * following/followerデータ取得用のスレッド作成
     * @return
     */
    public Thread createFollowingFollowerGetThread() {
        return new Thread() {

            @Override
            public void run() {

                String userName = jTextField1.getText();
                int ffIndex = jComboBox1.getSelectedIndex();

                if (userName != null
                        && userName.length() > 0
                        && prevUsername.equals(userName)
                        && prevGetFollowingFollower == ffIndex) {
                    //前回取得した情報の続きを取得する
                } else {
                    //新しい情報取得
                    numOfPage = 0;
                    jLabel2.setText("0");
                    listTableModel.clearStatus();
                }

                //データプログレス表示
                InsertProgressListener progressListener = new InsertProgressListener(jProgressBar1);

                try {
                    if (userName != null && userName.length() > 0) {
                        boolean update = false;
                        switch (ffIndex) {
                            case 0:
                                //following
                                update = addFollowingToTable(userName, numOfPage, progressListener);
                                break;
                            default:
                                //follewer
                                update = addFollowerToTable(userName, numOfPage, progressListener);
                                break;
                        }
                        if (update == true) {
                            numOfPage++;
                            jLabel2.setText((numOfPage * 100) + "");
                        }
                        //前回取得情報
                        prevUsername = userName;
                        prevGetFollowingFollower = ffIndex;
                    }
                    //データ取得完了を表示
                    setInformation("データ取得が完了しました");
                }catch(Exception e) {
                    setInformation("データ取得に失敗しました。指定したユーザは存在しない可能性があります。");
                }
            }
        };
    }

     /**
     * テーブルにfollowing一覧を挿入
     * @param username
      * @param page
      * @param listener
      * @return 更新されたデータがあればtrue
     */
    public boolean addFollowingToTable(String username, int page, InsertProgressListener listener) {
        this.setTableEnvironment();
        boolean update = false;
        List<User> list = this.tweetManager.getFollowingUser(username, page);
        if( list != null ) {
            //データの最大値をProgressbarにセット
            if( listener != null ) {
                listener.setMaxNum( list.size() - 1 );
            }
            //どのくらいデータの挿入が完了したか
            int currentInsertNum = 0;
            for(User u : list ) {
                if( listener != null ) {
                    listener.setCurrentNum(currentInsertNum);
                }
                //Modelにデータ挿入
                currentInsertNum++;
                this.listTableModel.insertUserList(u);
                update = true;
            }
        }
        return update;
    }

    /**
     * テーブルにfollowing一覧を挿入
     * @param username
     * @param page
     * @param listener
     * @return 更新されたデータがあれば true
     */
    public boolean addFollowerToTable(String username, int page, InsertProgressListener listener) {
        this.setTableEnvironment();
        boolean update = false;
        List<User> list = this.tweetManager.getFollowerUser(username, page);
        if( list != null ) {
            //データの最大値をProgressbarにセット
            if( listener != null ) {
                listener.setMaxNum( list.size() - 1 );
            }
            int currentInsertNum = 0;
            for(User u : list ) {
                if( listener != null ) {
                    listener.setCurrentNum(currentInsertNum);
                }
                currentInsertNum++;
                this.listTableModel.insertUserList(u);
                update = true;
            }
        }
        return update;
    }

    /**
     * テーブルの大きさ等の情報を設定
     */
    public void setTableEnvironment() {
        //テーブルの横幅設定
        TableColumnModel columnModel = this.jTable1.getColumnModel();
        TableColumn column = columnModel.getColumn(0);
        column.setMinWidth( this.iconSize );
        column.setMaxWidth( this.iconSize );

        //マウスリスナー設定
        jTable1.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                // いったんSelectしていた情報を削除
                jTable1.clearSelection();
                // if (e.getButton() == MouseEvent.BUTTON3) {
                Point p = e.getPoint();
                int col = jTable1.columnAtPoint(p);
                int row = jTable1.rowAtPoint(p);
                jTable1.changeSelection(row, col, false, false);

                //テーブルで選択した要素を詳細情報として表示
                setDetailInformation(jTable1);
                // }
            }
        });

        //キー受付
        jTable1.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent ke) {
                setDetailInformation(jTable1);
            }

        });
    }

    /**
     * テーブル情報の詳細をセット
     * @param table
     */
    public void setDetailInformation(JTable table) {
        User user = getTweetTableInformation(table, listTableModel);
        //情報が存在しないとき
        if( user == null ) {
            return;
        }
        //user name/screenname
        String username = user.getName();
        String screenName = user.getScreenName();
        //update time
        String updateTime = "";
        if( user.getStatus() != null ) {
            updateTime = user.getStatus().getCreatedAt().toString();
        }
        //location
        String location = "";
        if( user.getLocation() != null ) {
            location = user.getLocation().toString();
        }
        //following
        String following = user.getFriendsCount() + "";
        //follower
        String follower = user.getFollowersCount() + "";
        //update
        String update = user.getStatusesCount() + "";
        //user url
        String url = "";
        if( user.getURL() != null ) {
            url = user.getURL().toString();
        }
        //client
        String client = "";
        String text = "";
        if( user.getStatus() != null ) {
            client = user.getStatus().getSource();
            //最後の更新テキスト
            text = user.getStatus().getText();
        }
        //profile
        String profile = user.getDescription();

        if( username != null || screenName != null ) {
            this.userNameLabel.setText(username + "/" + screenName);
        }
        if( updateTime != null ) {
            this.updateTimeLabel.setText( updateTime );
        }
        if( location != null ) {
            this.locationLabel.setText( location );
        }
        if( following != null ) {
            this.followingLabel.setText( following );
        }
        if( follower != null ) {
            this.followerLabel.setText( follower );
        }
        if( update != null ) {
            this.updateLabel.setText( update );
        }
        if( url != null ) {
            this.userWebBox.setText(url);
        }
        if( client != null ) {
            this.clientNameLabel.setText( client );
        }
        if( text != null ) {
            this.tweetMessageBox.setText(text);
        }
        if( profile != null ) {
            this.userIntroBox.setText(profile);
        }

        //user icon設定
        ImageIcon icon = TwitterImageCache.getInstance().getProfileImage(
                user.getProfileImageURL().toString() );
        if( icon != null ) {
            userImageLabel.setIcon(icon);
        }
    }

    /**
     * テーブルで選択した場所のUser情報を取得
     *
     * @return
     */
    public User getTweetTableInformation(JTable table, TableModel model) {
        int index = table.convertRowIndexToModel(table.getSelectedRow());
        User info = null;
        if (model instanceof UserTableModel) {
            if( index >= 0 ) {
                info = ((UserTableModel) model).getUserList(index);
            }
        }
        return info;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        userIntroBox = new javax.swing.JEditorPane();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        userNameLabel = new javax.swing.JLabel();
        updateTimeLabel = new javax.swing.JLabel();
        locationLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        followingLabel = new javax.swing.JLabel();
        followerLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        updateLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        userWebBox = new javax.swing.JEditorPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        clientNameLabel = new javax.swing.JEditorPane();
        userImageLabel = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tweetMessageBox = new javax.swing.JEditorPane();
        jLabel11 = new javax.swing.JLabel();
        informationLabel = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Following/Follower一覧");

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable1.setModel(listTableModel);
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("取得したいユーザ名");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Following", "Follower" }));

        jLabel2.setLabelFor(jButton1);
        jLabel2.setText("0");

        jLabel6.setText("件までの情報を取得");

        jButton2.setText("次の100件の情報を取得");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        userIntroBox.setContentType("text/html");
        userIntroBox.setEditable(false);
        userIntroBox.setMinimumSize(new java.awt.Dimension(120, 20));
        jScrollPane6.setViewportView(userIntroBox);

        jLabel10.setText("紹介文");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 158, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setPreferredSize(new java.awt.Dimension(250, 216));

        userNameLabel.setText("UserName");

        updateTimeLabel.setText("Date");

        locationLabel.setText("Information");

        jLabel9.setText("Following");

        followingLabel.setText("0");

        followerLabel.setText("0");

        jLabel12.setText("Follower");

        jLabel13.setText("更新回数");

        updateLabel.setText("0");

        jLabel3.setText("ユーザ名");

        jLabel5.setText("現在地");

        jLabel4.setText("更新日");

        jLabel7.setText("Web");

        jLabel8.setText("Client");

        jScrollPane7.setBorder(null);
        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        userWebBox.setBorder(null);
        userWebBox.setContentType("text/html");
        userWebBox.setEditable(false);
        userWebBox.setEditable(false);
        userWebBox.addHyperlinkListener(new TweetHyperlinkHandler());
        try {
            // htmlフォント変更
            HTMLDocument doc = (HTMLDocument) userWebBox.getDocument();
            StyleSheet[] style = doc.getStyleSheet().getStyleSheets();
            for (int i = style.length - 1; i >= 0; i--) {
                Style body = style[i].getStyle("body");
                if (body != null) {
                    // TODO: change default font size
                    StyleConstants.setFontSize(body, 13);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jScrollPane7.setViewportView(userWebBox);

        jScrollPane8.setBorder(null);
        jScrollPane8.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane8.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        clientNameLabel.setBorder(null);
        clientNameLabel.setContentType("text/html");
        clientNameLabel.setEditable(false);
        clientNameLabel.setEditable(false);
        clientNameLabel.addHyperlinkListener(new TweetHyperlinkHandler());
        try {
            // htmlフォント変更
            HTMLDocument doc = (HTMLDocument) clientNameLabel.getDocument();
            StyleSheet[] style = doc.getStyleSheet().getStyleSheets();
            for (int i = style.length - 1; i >= 0; i--) {
                Style body = style[i].getStyle("body");
                if (body != null) {
                    // TODO: default font size
                    StyleConstants.setFontSize(body, 13);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jScrollPane8.setViewportView(clientNameLabel);

        userImageLabel.setBackground(java.awt.Color.black);
        userImageLabel.setOpaque(true);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(updateTimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                                    .addComponent(updateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                                    .addComponent(followerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                                    .addComponent(followingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                                    .addComponent(userNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                                    .addComponent(locationLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(userNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(updateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(locationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(followingLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(followerLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(updateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {followerLabel, followingLabel, jLabel12, jLabel13, jLabel3, jLabel4, jLabel5, jLabel7, jLabel8, jLabel9, jScrollPane7, jScrollPane8, locationLabel, updateLabel, updateTimeLabel, userNameLabel});

        jScrollPane5.setHorizontalScrollBar(null);

        tweetMessageBox.setContentType("text/html");
        tweetMessageBox.setEditable(false);
        tweetMessageBox.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                tweetMessageBoxComponentResized(evt);
            }
        });
        tweetMessageBox.addHyperlinkListener(hashTagHyperlinkListener);
        jScrollPane5.setViewportView(tweetMessageBox);

        jLabel11.setText("最後の発言");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 278, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        informationLabel.setText("information");
        informationLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, 0, 289, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(informationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel2)
                .addGap(16, 16, 16)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2))))
                .addGap(12, 12, 12)
                .addComponent(informationLabel))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tweetMessageBoxComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tweetMessageBoxComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_tweetMessageBoxComponentResized

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //threadが起動してないときに起動
        if( updateThread == null || updateThread.isAlive() == false ) {
            updateThread = this.createFollowingFollowerGetThread();
            updateThread.start();
            setInformation("データ取得を開始します");
        }else {
            setInformation("現在データを取得中です");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * Userのサーチを開始する
     * @param username 取得したいユーザ
     * @param ff trueでfollowing/ falseでfollowerを取得
     */
    public void actionUserSearch(String username, boolean ff) {
        if( username != null ) {
            jTextField1.setText(username);
        }
        if( ff == true ) {
            //following
            jComboBox1.setSelectedIndex(0);
        }else {
            //follower
            jComboBox1.setSelectedIndex(1);
        }
        jButton2ActionPerformed( null );
    }

    /**
     * 現在の状態をラベルに表示する
     * @param msg
     */
    public void setInformation(String msg) {
       if( this.informationLabel != null ) {
           this.informationLabel.setText( msg );
       }
    }

    /**
     * データ挿入の進捗情報を表示するためのクラス
     */
    private class InsertProgressListener{
        private JProgressBar progress;
        private int maxNum;
        private int currentNum;

        public InsertProgressListener(JProgressBar progress) {
            this.progress = progress;
        }

        public int getCurrentNum() {
            return currentNum;
        }

        public void setCurrentNum(int currentNum) {
            this.currentNum = currentNum;
            progress.setMaximum( this.maxNum );
            progress.setValue( this.currentNum );
        }

        public int getMaxNum() {
            return maxNum;
        }

        public void setMaxNum(int maxNum) {
            this.maxNum = maxNum;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane clientNameLabel;
    private javax.swing.JLabel followerLabel;
    private javax.swing.JLabel followingLabel;
    private javax.swing.JLabel informationLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JEditorPane tweetMessageBox;
    private javax.swing.JLabel updateLabel;
    private javax.swing.JLabel updateTimeLabel;
    private javax.swing.JLabel userImageLabel;
    private javax.swing.JEditorPane userIntroBox;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JEditorPane userWebBox;
    // End of variables declaration//GEN-END:variables

}
