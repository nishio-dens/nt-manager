/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NishioTweetManager.java
 *
 * Created on 2010/09/06, 3:32:04
 */
package twitter.gui.form;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import twitter.gui.action.TweetMainAction;
import twitter.gui.component.TweetHyperlinkHandler;
import twitter.manage.TweetManager;

/**
 *
 * @author nishio
 */
public class NishioTweetManager extends javax.swing.JFrame {

    private SystemTray systemTray;
    private TrayIcon trayIcon;

    /** Creates new form NishioTweetManager */
    public NishioTweetManager() {
        initComponents();
        //component初期化
        initComponents2();
        //twitterコード初期化
        init();
    }

    /**
     * コンポーネント初期化
     */
    private void initComponents2() {
        Image image = null;
        try {
            //トレイイメージ
            /*URL resource = getClass().getResource("icon.png");
            image = new ImageIcon(resource).getImage();*/
            image = new ImageIcon("resources/icon.png").getImage();
            //フレームのアイコン設定
            this.setIconImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        }
        //システムトライ関係
        systemTray = SystemTray.getSystemTray();
        final JFrame frame = this;
        final PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(image, "Nishio Tweet Manager", popup);

        //左クリックした時に「画面を開く」アクションをする
        trayIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                //左クリック
                if (me.getButton() == MouseEvent.BUTTON1) {
                    frame.setVisible(true);
                }
            }
        });

        //右クリック時のポップアップメニュー
        MenuItem item1 = new MenuItem("画面を開く");
        item1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
            }
        });
        MenuItem item2 = new MenuItem("終了");
        item2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                systemTray.remove(trayIcon);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.dispose();
                System.exit(0);
            }
        });
        popup.add(item1);
        popup.add(item2);

        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new twitter.gui.component.DnDTabbedPane();
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextPane = new javax.swing.JTextPane();
        jLabel15 = new javax.swing.JLabel();
        tweetLengthLabel = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        statusBar = new javax.swing.JLabel();
        jToggleButton2 = new javax.swing.JToggleButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();

        setTitle("Nishio Tweet Manager");

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
        tweetMessageBox.addHyperlinkListener(new TweetHyperlinkHandler());
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
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE))
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
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
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
                .addContainerGap())
        );

        jButton1.setText("今すぐ更新");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("リフレッシュ");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jScrollPane9.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane9.setMaximumSize(new java.awt.Dimension(32767, 80));
        jScrollPane9.setMinimumSize(new java.awt.Dimension(26, 80));

        jTextPane.setMaximumSize(new java.awt.Dimension(2147483647, 20));
        jTextPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextPaneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextPaneFocusLost(evt);
            }
        });
        jTextPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextPaneKeyReleased(evt);
            }
        });
        jScrollPane9.setViewportView(jTextPane);

        jLabel15.setText("残り文字数");

        tweetLengthLabel.setText("140");

        jButton4.setText("つぶやく");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jToggleButton1.setSelected(true);
        jToggleButton1.setText("詳細情報");
        jToggleButton1.setMaximumSize(new java.awt.Dimension(73, 26));
        jToggleButton1.setMinimumSize(new java.awt.Dimension(73, 26));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        statusBar.setText("Status");
        statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jToggleButton2.setSelected(true);
        jToggleButton2.setText("書き込み欄");
        jToggleButton2.setMaximumSize(new java.awt.Dimension(73, 26));
        jToggleButton2.setMinimumSize(new java.awt.Dimension(73, 26));
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        jMenu1.setMnemonic('F');
        /*
        org.openide.awt.Mnemonics.setLocalizedText(jMenu1, "ファイル(F)");
        */
        jMenu1.setText("ファイル(F)");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setMnemonic('X');
        jMenuItem1.setText("終了(X)");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu6.setMnemonic('O');
        jMenu6.setText("操作(O)");

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setMnemonic('U');
        jMenuItem7.setText("今すぐ更新(U)");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem7);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem8.setMnemonic('T');
        jMenuItem8.setText("時間情報を更新(T)");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem8);

        jMenuBar1.add(jMenu6);

        jMenu4.setMnemonic('V');
        jMenu4.setText("表示(V)");

        jMenuItem5.setMnemonic('D');
        jMenuItem5.setText("選択しているタブを削除(D)");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        jMenuItem9.setText("Debug");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem9);

        jMenuBar1.add(jMenu4);

        jMenu5.setMnemonic('S');
        jMenu5.setText("検索(S)");

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setMnemonic('A');
        jMenuItem6.setText("キーワードでTwitter全体を検索(A)");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem6);

        jMenuBar1.add(jMenu5);

        jMenu2.setMnemonic('O');
        jMenu2.setText("設定(O)");

        jMenuItem2.setText("基本設定");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem4.setText("アカウント設定");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        jMenu3.setMnemonic('H');
        jMenu3.setText("ヘルプ(H)");

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem3.setText("このプログラムについて");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tweetLengthLabel)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(tweetLengthLabel)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                        .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                        .addComponent(jToggleButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // メッセージをつぶやく
        mainAction.actionTweet();
        updateLen();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void tweetMessageBoxComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tweetMessageBoxComponentResized
    }//GEN-LAST:event_tweetMessageBoxComponentResized

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        mainAction.actionUpdateButton(evt);
        // いますぐ更新ボタンを押したので，更新タイムを一度リセットする
        //mainAction.resetTweetAutoUpdate();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // tweet取得時間情報を更新
        mainAction.actionRefreshTime();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // 詳細情報ボタンを押した時の動作
        mainAction.actionDetailInfoButton(evt);
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jTextPaneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextPaneKeyReleased
        // 残りつぶやける文字数情報を更新
        mainAction.actionUpdateTweetMessageCount();
    }//GEN-LAST:event_jTextPaneKeyReleased

    private void jTextPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextPaneFocusGained
        updateLen();
    }//GEN-LAST:event_jTextPaneFocusGained

    private void jTextPaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextPaneFocusLost
        updateLen();
    }//GEN-LAST:event_jTextPaneFocusLost

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // 終了動作
        mainAction.actionExitButton(evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // 基本設定ダイアログを開く
        mainAction.actionBasicSettingDialog();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        mainAction.actionShowAboutDialog();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        mainAction.actionShowAccountDialog();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        mainAction.actionShowTweetboxButton(evt);
        this.invalidate();
        this.validate();
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        //選択しているタブを削除
        mainAction.actionRemoveFocusedTabbedTable();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        this.mainAction.actionShowKeywordSearchDialog();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        mainAction.actionUpdateButton(evt);
        // いますぐ更新ボタンを押したので，更新タイムを一度リセットする
       // mainAction.resetTweetAutoUpdate();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // tweet取得時間情報を更新
        mainAction.actionRefreshTime();
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    /**
     *
     */
    private void updateLen() {
        // 残りつぶやける文字数情報を更新
        mainAction.actionUpdateTweetMessageCount();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    // UIをシステム標準のものとする
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                new NishioTweetManager().setVisible(true);
            }
        });
    }

    /**
     * Twitter初期化
     */
    private void init() {
        // twitterログイン
        boolean login = false;
        try {
            tweetManager.loginTwitter();
            login = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // メインアクション初期化
        mainAction = new TweetMainAction(this, tweetManager, statusBar,
                jTextPane, jScrollPane9, tweetLengthLabel, jPanel1, jTabbedPane1, tweetMessageBox, userImageLabel,
                userNameLabel, updateTimeLabel, followerLabel, followingLabel, locationLabel,
                clientNameLabel, updateLabel, userIntroBox, userWebBox);
        //もしログインに失敗したら，アカウント設定画面を出す
        if (login == false) {
            mainAction.actionShowAccountDialog();
        } else {
            //TLなどを表示するタブを追加
            //自動更新も開始
            //TODO:TL, Mention, DMのタブを表示するかしないかの設定を読み込んで反映するように
            this.mainAction.actionAddTimelineTab( this.mainAction.getGetTimelinePeriod() );
            this.mainAction.actionAddMentionTab( this.mainAction.getGetMentionPeriod() );
            this.mainAction.actionAddDirectMessageTab( this.mainAction.getGetDirectMessagePeriod() );
            //this.mainAction.actionAddSendDirectMessageTab( this.mainAction.getGetSendDirectMessagePeriod() );
        }
    }
    
    //nishio tweet manager
    private JPopupMenu rightClickPopup = null;
    private TweetMainAction mainAction = null;
    // TweetManager
    private TweetManager tweetManager = new TweetManager();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane clientNameLabel;
    private javax.swing.JLabel followerLabel;
    private javax.swing.JLabel followingLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane jTextPane;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JLabel statusBar;
    private javax.swing.JLabel tweetLengthLabel;
    private javax.swing.JEditorPane tweetMessageBox;
    private javax.swing.JLabel updateLabel;
    private javax.swing.JLabel updateTimeLabel;
    private javax.swing.JLabel userImageLabel;
    private javax.swing.JEditorPane userIntroBox;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JEditorPane userWebBox;
    // End of variables declaration//GEN-END:variables
}
