/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UserListDialog.java
 *
 * Created on 2010/10/17, 16:55:08
 */

package twitter.gui.form;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import twitter.action.TweetUserTimelineGetter;
import twitter.action.list.UserListGetter;
import twitter.gui.action.TweetMainAction;
import twitter.gui.component.TweetListTableModel;
import twitter.manage.TweetManager;
import twitter4j.UserList;

/**
 *
 * @author nishio
 */
public class UserListDialog extends javax.swing.JDialog {

    //メインアクション
    private TweetMainAction mainAction = null;
    //Userlist
    private UserListGetter userListGetter = null;
    //リストを取得するユーザの名前
    private String listUserName = null;
    //リストモデル
    private TweetListTableModel listTableModel = new TweetListTableModel();
    //アイコンの大きさ
    private static final int iconSize = 50;

    /** Creates new form UserListDialog */
    public UserListDialog(java.awt.Frame parent, boolean modal, TweetMainAction mainAction,
            UserListGetter userListGetter, String listUserName) {
        super(parent, modal);
        initComponents();
        this.mainAction = mainAction;
        this.listUserName = listUserName;
        this.userListGetter = userListGetter;

        final String username = listUserName;
        //スレッドにリスト挿入作業をさせる
        new Thread() {

            @Override
            public void run() {
                addUserListToTable(username);
            }
        }.start();
    }

    /**
     * テーブルにリスト一覧を挿入
     * @param username
     */
    public void addUserListToTable(String username) {
        this.listTableModel.clearStatus();
        this.setTableEnvironment();
        if( this.userListGetter != null ) {
            List<UserList> list = this.userListGetter.getUserLists(username);
            for(UserList u : list ) {
                this.listTableModel.insertUserList(u);
            }
        }
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
        UserList userList = getTweetTableInformation(table, listTableModel);
        //情報が存在しないとき
        if( userList == null ) {
            return;
        }
        String listName = userList.getName();
        String userName = userList.getUser().getName();
        int subscriber = userList.getSubscriberCount();
        int memberCount = userList.getMemberCount();
        String description = userList.getDescription();

        if( listName != null ) {
            jLabel5.setText(listName);
        }
        if( userName != null ) {
            jLabel6.setText(userName);
        }
        if( description != null ) {
            jTextArea1.setText(description);
        }
        jLabel7.setText( memberCount + "");
        jLabel8.setText( subscriber + "");

        userImageLabel.setIcon( new ImageIcon(userList.getUser().getProfileImageURL() ) );
    }

    /**
     * テーブルで選択した場所のUserList情報を取得
     *
     * @return
     */
    public UserList getTweetTableInformation(JTable table, TableModel model) {
        int index = table.convertRowIndexToModel(table.getSelectedRow());
        UserList info = null;
        if (model instanceof TweetListTableModel) {
            if( index >= 0 ) {
                info = ((TweetListTableModel) model).getUserList(index);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        userImageLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTable1.setModel(listTableModel);
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("キャンセル");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("リスト名");

        jLabel2.setText("リスト所有者");

        jLabel3.setText("フォロー中");

        jLabel4.setText("リストをフォロー");

        userImageLabel.setBackground(java.awt.Color.black);
        userImageLabel.setOpaque(true);

        jLabel5.setText("NULL");

        jLabel6.setText("NULL");

        jLabel7.setText("0");

        jLabel8.setText("0");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel7, jLabel8});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addComponent(userImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(542, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        UserList info = getTweetTableInformation(jTable1, listTableModel);
        if( info == null ) {
            JOptionPane.showMessageDialog(null, "リストを選択してください",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String listName = info.getName();
        String userName = info.getUser().getScreenName();
        int id = info.getId();
        String fullName = info.getFullName();

        if( listName == null || userName == null || fullName == null ) {
            JOptionPane.showMessageDialog(null, "選択できないリストです",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //TODO: 更新間隔をリスト毎に設定できるようにするべきか検討
        this.mainAction.actionAddListTab(userName, id, fullName, this.mainAction.getGetTimelinePeriod());
        //終了
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel userImageLabel;
    // End of variables declaration//GEN-END:variables

}
