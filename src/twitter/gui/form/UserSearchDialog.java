/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * KeywordSearchDialog.java
 *
 * Created on 2010/09/14, 1:09:08
 */

package twitter.gui.form;

import javax.swing.JOptionPane;
import twitter.gui.action.TweetMainAction;

/**
 *
 * @author nishio
 */
public class UserSearchDialog extends javax.swing.JDialog {

    /** Creates new form KeywordSearchDialog */
    public UserSearchDialog(java.awt.Frame parent, boolean modal, TweetMainAction mainAction) {
        super(parent, modal);
        initComponents();

        //デフォルトボタンの設定
        this.getRootPane().setDefaultButton(jButton2);

        this.mainAction = mainAction;
    }

    /**
     * 検索ワード入力欄に検索ワードをセット
     * @param searchWord
     */
    public void setSearchWord(String searchWord) {
        jTextField1.setText(searchWord);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Twitter全体からキーワード検索");

        jLabel1.setText("取得したいユーザのスクリーン名を入力してください");

        jLabel2.setText("情報更新間隔を指定してください");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("情報を定期的に更新する");

        jTextField2.setText("60");

        jLabel3.setText("秒間隔");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("更新しない");

        jButton1.setText("キャンセル");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setMnemonic('S');
        jButton2.setText("取得開始(S)");
        jButton2.setActionCommand("取得開始(S)");
        jButton2.setFocusCycleRoot(true);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton1)
                            .addComponent(jRadioButton2))
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       
        //検索キーワードを取得
        String username = jTextField1.getText();
        if( username == null || username.length() == 0 ) {
            JOptionPane.showMessageDialog(null, "検索したいユーザ名を入力してください",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return; //終了
        }

        //情報を一定間隔で更新
        Integer period = 0;
        //時間情報取得
        if( jRadioButton1.isSelected() == true ) {
            try {
                period = Integer.parseInt(jTextField2.getText());
            }catch(Exception e) {
                JOptionPane.showMessageDialog(null, "正しい周期を設定してください",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return; //終了
            }

            if( period < MIN_PERIOD ) {
                //更新間隔が短すぎる
                JOptionPane.showMessageDialog(null, "周期が短すぎます．最低でも" + MIN_PERIOD + "秒以上を設定してください．",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return; //終了
            }
        }else {
            //情報を一定間隔で更新しない場合は，更新間隔をINTMAXとする
            period = Integer.MAX_VALUE;
        }

        //ユーザ情報を表示するテーブルをタブに追加
        mainAction.actionAddUserTimelineTab(username, period);
        //画面を閉じる
        this.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed



    //twitter
    private TweetMainAction mainAction;
    //30秒以下の更新は認めない
    private long MIN_PERIOD = 30;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

}
