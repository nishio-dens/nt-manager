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
     * テーブルにfollowing一覧を挿入
     * @param username
      * @return 更新されたデータがあればtrue
     */
    public boolean addFollowingToTable(String username, int page) {
        this.setTableEnvironment();
        boolean update = false;
        List<User> list = this.tweetManager.getFollowingUser(username, page);
        if( list != null ) {
            for(User u : list ) {
                this.listTableModel.insertUserList(u);
                update = true;
            }
        }
        return update;
    }

    /**
     * テーブルにfollowing一覧を挿入
     * @param username
     * @return 更新されたデータがあれば true
     */
    public boolean addFollowerToTable(String username, int page) {
        this.setTableEnvironment();
        boolean update = false;
        List<User> list = this.tweetManager.getFollowerUser(username, page);
        if( list != null ) {
            for(User u : list ) {
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
        String updateTime = user.getStatus().getCreatedAt().toString();
        //location
        String location = "";
        if( user.getStatus().getPlace() != null ) {
            location = user.getStatus().getPlace().toString();
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
        String client = user.getStatus().getSource();
        //最後の更新テキスト
        String text = user.getStatus().getText();
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
        userImageLabel = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tweetMessageBox = new javax.swing.JEditorPane();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        userNameLabel = new javax.swing.JLabel();
        updateTimeLabel = new javax.swing.JLabel();
        locationLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        followingLabel = new javax.swing.JLabel();
        followerLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        updateLabel = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        userIntroBox = new javax.swing.JEditorPane();
        jScrollPane7 = new javax.swing.JScrollPane();
        userWebBox = new javax.swing.JEditorPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        clientNameLabel = new javax.swing.JEditorPane();

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

        userImageLabel.setBackground(java.awt.Color.black);
        userImageLabel.setOpaque(true);

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

        jLabel3.setText("ユーザ名");

        jLabel4.setText("更新日");

        jLabel5.setText("現在地");

        userNameLabel.setText("UserName");

        updateTimeLabel.setText("Date");

        locationLabel.setText("Information");

        jLabel9.setText("Following");

        followingLabel.setText("0");

        followerLabel.setText("0");

        jLabel12.setText("Follower");

        jLabel13.setText("更新回数");

        updateLabel.setText("0");

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        userIntroBox.setContentType("text/html");
        userIntroBox.setEditable(false);
        userIntroBox.setMinimumSize(new java.awt.Dimension(120, 20));
        jScrollPane6.setViewportView(userIntroBox);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(userImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(locationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(userNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(updateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(followerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                            .addComponent(followingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(userNameLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(updateTimeLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(locationLabel)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(followingLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(followerLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(updateLabel))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, 0, 294, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tweetMessageBoxComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tweetMessageBoxComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_tweetMessageBoxComponentResized

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.updateThread = new Thread() {

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

                if (userName != null && userName.length() > 0) {
                    boolean update = false;
                    switch (ffIndex) {
                        case 0:
                            //following
                            update = addFollowingToTable(userName, numOfPage);
                            break;
                        default:
                            //follewer
                            update = addFollowerToTable(userName, numOfPage);
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
            }
        };
        //threadが起動してないときに起動
        if( updateThread.isAlive() == false ) {
            updateThread.start();
        }
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane clientNameLabel;
    private javax.swing.JLabel followerLabel;
    private javax.swing.JLabel followingLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
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
