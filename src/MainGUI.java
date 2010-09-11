import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import twitter.gui.action.TweetMainAction;
import twitter.gui.component.ImagePanel;
import twitter.gui.component.TweetCommentRenderer;
import twitter.gui.component.TweetHyperlinkHandler;
import twitter.gui.component.TweetTableModel;
import twitter.gui.component.UserImageRenderer;
import twitter.manage.TweetManager;
import twitter4j.Status;

public class MainGUI {

	/**
	 * This method initializes jScrollPane3
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane3() {
		if (jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setViewportView(getJTable2());
		}
		return jScrollPane3;
	}

	/**
	 * This method initializes jTable2
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable2() {
		if (jTable2 == null) {
			jTable2 = new JTable();
			messageTableModel = new TweetTableModel();
			jTable2 = new JTable(messageTableModel);
			jTable2.addFocusListener(new java.awt.event.FocusAdapter() {
				@Override
				public void focusGained(java.awt.event.FocusEvent e) {
					mainAction.actionResetUncheckedDirectMessageCount();
				}
			});
			jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mousePressed(java.awt.event.MouseEvent e) {
					showPopup(e);
				}

				@Override
				public void mouseReleased(java.awt.event.MouseEvent e) {
					showPopup(e);
				}

				private void showPopup(MouseEvent e) {
					if (e.isPopupTrigger()) {
						// 右クリックのメニューを表示
						getRightClickPopup().show(e.getComponent(), e.getX(),
								e.getY());
					}
				}
			});
			createTweetTable(jTable2);
		}
		return jTable2;
	}

	/**
	 * This method initializes jScrollPane4
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane4() {
		if (jScrollPane4 == null) {
			jScrollPane4 = new JScrollPane();
			jScrollPane4.setViewportView(getJTable3());
		}
		return jScrollPane4;
	}

	/**
	 * This method initializes jTable3
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable3() {
		if (jTable3 == null) {
			jTable3 = new JTable();
			sendMessageTableModel = new TweetTableModel();
			jTable3 = new JTable(sendMessageTableModel);
			createTweetTable(jTable3);
		}
		return jTable3;
	}

	/**
	 * This method initializes jScrollPane5
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane5() {
		if (jScrollPane5 == null) {
			jScrollPane5 = new JScrollPane();
			jScrollPane5.setViewportView(getJTextPane());
		}
		return jScrollPane5;
	}

	/**
	 * This method initializes jScrollPane6
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane6() {
		if (jScrollPane6 == null) {
			jScrollPane6 = new JScrollPane();
			jScrollPane6.setBounds(new Rectangle(58, 8, 739, 46));
			jScrollPane6.setViewportView(getTweetMessageBox());
		}
		return jScrollPane6;
	}

	/**
	 * This method initializes jTable4
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable4() {
		if (jTable4 == null) {
			jTable4 = new JTable();
			filterTableModel = new TweetTableModel();
			createTweetTable(jTable4);
		}
		return jTable4;
	}

	/**
	 * This method initializes basicSettingMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getBasicSettingMenuItem() {
		if (basicSettingMenuItem == null) {
			basicSettingMenuItem = new JMenuItem();
			basicSettingMenuItem.setText("基本設定");
			basicSettingMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// 基本設定ダイアログを開く
							mainAction.actionBasicSettingDialog();
						}
					});
		}
		return basicSettingMenuItem;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					// UIをシステム標準のものとする
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				MainGUI application = new MainGUI();
				application.getJFrame().setVisible(true);
			}
		});
	}

	// TweetManager
	private final TweetManager tweetManager = new TweetManager();

	// GUIAction部分
	private TweetMainAction mainAction = null;
	private JFrame jFrame = null; // @jve:decl-index=0:visual-constraint="10,96"
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu settingMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JDialog aboutDialog = null;
	private JPanel aboutContentPane = null;
	private JLabel aboutVersionLabel = null;
	private JTextPane jTextPane = null;
	private JButton jButton = null;
	private JTabbedPane jTabbedPane = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private TweetTableModel tweetTableModel = null;
	private TweetTableModel mentionTableModel = null;
	private TweetTableModel messageTableModel = null;
	private TweetTableModel sendMessageTableModel = null;
	private TweetTableModel filterTableModel = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JPanel jPanel = null;
	private JButton jButton3 = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel1 = null;
	private JEditorPane tweetMessageBox = null;
	private JLabel userImageLabel = null;
	private JLabel userNameLabel = null;
	private JLabel updateTimeLabel = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel locationLabel = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JLabel followingLabel = null;
	private JLabel FollowerLabel = null;
	private JLabel updateLabel = null;
	private JEditorPane userWebBox = null;
	private JScrollPane jScrollPane1 = null;
	private JEditorPane userIntroBox = null;
	private JEditorPane clientName = null;
	private JLabel jLabel3 = null;
	private JLabel statusBar = null;
	private JPanel jPanel3 = null;
	private JScrollPane jScrollPane2 = null;
	private JTable jTable1 = null;
	private JPopupMenu rightClickPopup = null;

	private JScrollPane jScrollPane3 = null;

	private JTable jTable2 = null;

	private JScrollPane jScrollPane4 = null;

	private JTable jTable3 = null;

	private JScrollPane jScrollPane5 = null;

	private JScrollPane jScrollPane6 = null;

	private JTable jTable4 = null;

	private JMenuItem basicSettingMenuItem = null;

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

						int sc = table.getSelectedRowCount();
						String infoMessage = "";
						if (sc == 1) {
							Status st = mainAction.getTweetTableInformation(
									table, table.getModel());
							infoMessage = st.getText();
							// tweetMessageBox内のURLをhtmlリンクへ変換
							infoMessage = mainAction
									.actionReplaceTweetMessageBoxURLLink(infoMessage);
							// @ユーザ情報をhtmlリンクへ変換
							infoMessage = mainAction
									.actionReplaceTweetMessageBoxUserInfo(infoMessage);
							// #ハッシュタグ情報をhtmlリンクへ変換
							infoMessage = mainAction
									.actionReplaceTweetMessageBoxHashTab(infoMessage);
							// 詳細情報にテーブルで選択した人のツイート情報を表示
							tweetMessageBox.setText(infoMessage);
							// user icon
							userImageLabel.setIcon(new ImageIcon(st.getUser()
									.getProfileImageURL()));
							// user name and id
							userNameLabel.setText(st.getUser().getName()
									+ " / " + st.getUser().getScreenName());
							// update Time
							updateTimeLabel.setText(st.getCreatedAt()
									.toLocaleString());
							// ユーザのバックグラウンドイメージを取得
							/*
							 * ImagePanel imagePanel = null; if( jContentPane
							 * instanceof ImagePanel ) { imagePanel =
							 * (ImagePanel)jContentPane; try { URL url = new URL
							 * ( st.getUser().getProfileBackgroundImageUrl() );
							 * imagePanel.setImage( new ImageIcon( url
							 * ).getImage() ); } catch (MalformedURLException
							 * e1) { e1.printStackTrace(); } }
							 */
							// ユーザ自己紹介文
							userIntroBox.setText(st.getUser().getDescription());
							// フォローされている数
							FollowerLabel.setText(st.getUser()
									.getFollowersCount()
									+ "");
							// フォローしている数
							followingLabel.setText(st.getUser()
									.getFriendsCount()
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
							clientName.setText(" via " + st.getSource());
							// Update
							updateLabel.setText(st.getUser().getStatusesCount()
									+ "");
						}
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
	 * This method initializes aboutContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new BorderLayout());
			aboutContentPane.add(getAboutVersionLabel(), BorderLayout.CENTER);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(getJFrame(), true);
			aboutDialog.setTitle("About");
			aboutDialog.setContentPane(getAboutContentPane());
		}
		return aboutDialog;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();
					aboutDialog.pack();
					Point loc = getJFrame().getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutVersionLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private JLabel getAboutVersionLabel() {
		if (aboutVersionLabel == null) {
			aboutVersionLabel = new JLabel();
			aboutVersionLabel.setText("Version 1.0");
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}

	/**
	 * This method initializes clientName
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getClientName() {
		if (clientName == null) {
			clientName = new JEditorPane();
			clientName.setBounds(new Rectangle(210, 135, 294, 16));
			clientName.setContentType("text/html");
			clientName.setEditable(false);
			clientName.addHyperlinkListener(new TweetHyperlinkHandler());
			try {
				// htmlフォント変更
				HTMLDocument doc = (HTMLDocument) clientName.getDocument();
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
		}
		return clientName;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getSettingMenu() {
		if (settingMenu == null) {
			settingMenu = new JMenu();
			settingMenu.setText("Setting");
			settingMenu.add(getBasicSettingMenuItem());
		}
		return settingMenu;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 終了動作
					mainAction.actionExitButton(e);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("つぶやく");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// メッセージをつぶやく
					mainAction.actionTweet();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("今すぐ更新");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					mainAction.actionUpdateButton(e);
					// いますぐ更新ボタンを押したので，更新タイムを一度リセットする
					mainAction.resetTweetAutoUpdate();
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Refresh");
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// tweet取得時間情報を更新
					mainAction.actionRefreshTime();
				}
			});
		}
		return jButton2;
	}

	/**
	 * This method initializes jButton3
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton3() {
		if (jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setText("詳細情報");
			jButton3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// 詳細情報ボタンを押した時の動作
					mainAction.actionDetailInfoButton(e);
				}
			});
		}
		return jButton3;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			statusBar = new JLabel();
			statusBar.setText("Status");
			jContentPane = new ImagePanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTabbedPane(), null);
			jContentPane.add(getJPanel2(), null);
			jContentPane.add(getJPanel3(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(832, 786);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("Nishio Tweet Manager");
			init(); // 初期化
		}
		return jFrame;
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getSettingMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints5.gridheight = 2;
			gridBagConstraints5.gridwidth = 3;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.ipadx = 675;
			gridBagConstraints5.ipady = 41;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 1.0;
			gridBagConstraints5.insets = new Insets(0, 0, 0, 0);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new Insets(6, 1, 2, 8);
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.ipadx = 97;
			gridBagConstraints4.ipady = 12;
			gridBagConstraints4.gridx = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(0, 0, 3, 3);
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.ipadx = 134;
			gridBagConstraints3.ipady = 9;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridx = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(0, 0, 3, 5);
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.ipadx = 131;
			gridBagConstraints2.ipady = 9;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 3, 5);
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.ipadx = 121;
			gridBagConstraints1.ipady = 9;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(3, 1, 6, 7);
			gridBagConstraints.gridy = 2;
			gridBagConstraints.ipadx = 36;
			gridBagConstraints.ipady = 5;
			gridBagConstraints.gridx = 3;
			jLabel3 = new JLabel();
			jLabel3.setText("140");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setPreferredSize(new Dimension(811, 445));
			jPanel.setBounds(new Rectangle(3, 160, 814, 119));
			jPanel.add(getJButton(), gridBagConstraints);
			jPanel.add(getJButton1(), gridBagConstraints1);
			jPanel.add(getJButton2(), gridBagConstraints2);
			jPanel.add(getJButton3(), gridBagConstraints3);
			jPanel.add(jLabel3, gridBagConstraints4);
			jPanel.add(getJScrollPane5(), gridBagConstraints5);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			updateLabel = new JLabel();
			updateLabel.setBounds(new Rectangle(414, 100, 91, 15));
			updateLabel.setText("0");
			FollowerLabel = new JLabel();
			FollowerLabel.setBounds(new Rectangle(414, 80, 91, 15));
			FollowerLabel.setText("0");
			followingLabel = new JLabel();
			followingLabel.setBounds(new Rectangle(414, 60, 91, 15));
			followingLabel.setText("0");
			jLabel6 = new JLabel();
			jLabel6.setBounds(new Rectangle(329, 100, 78, 14));
			jLabel6.setText("Update");
			jLabel5 = new JLabel();
			jLabel5.setBounds(new Rectangle(329, 80, 78, 16));
			jLabel5.setText("Follower");
			jLabel4 = new JLabel();
			jLabel4.setBounds(new Rectangle(329, 60, 78, 17));
			jLabel4.setText("Following");
			locationLabel = new JLabel();
			locationLabel.setBounds(new Rectangle(72, 100, 254, 15));
			locationLabel.setText("No Information");
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(8, 100, 58, 12));
			jLabel2.setText("現在地");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(8, 80, 58, 12));
			jLabel1.setText("更新日");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(8, 60, 58, 12));
			jLabel.setText("ユーザ名");
			updateTimeLabel = new JLabel();
			updateTimeLabel.setBounds(new Rectangle(72, 80, 254, 13));
			updateTimeLabel.setText("Date");
			userNameLabel = new JLabel();
			userNameLabel.setBounds(new Rectangle(72, 60, 254, 15));
			userNameLabel.setText("UserName");
			userImageLabel = new JLabel();
			userImageLabel.setBounds(new Rectangle(6, 8, 48, 45));
			userImageLabel.setText("");
			userImageLabel.setBackground(new Color(51, 51, 51));
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			jPanel1.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			jPanel1.setBounds(new Rectangle(9, 0, 809, 151));
			jPanel1.add(userImageLabel, null);
			jPanel1.add(userNameLabel, null);
			jPanel1.add(updateTimeLabel, null);
			jPanel1.add(jLabel, null);
			jPanel1.add(jLabel1, null);
			jPanel1.add(jLabel2, null);
			jPanel1.add(locationLabel, null);
			jPanel1.add(jLabel4, null);
			jPanel1.add(jLabel5, null);
			jPanel1.add(jLabel6, null);
			jPanel1.add(followingLabel, null);
			jPanel1.add(FollowerLabel, null);
			jPanel1.add(updateLabel, null);
			jPanel1.add(getUserWebBox(), null);
			jPanel1.add(getJScrollPane1(), null);
			jPanel1.add(getClientName(), null);
			jPanel1.add(getJScrollPane6(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(null);
			jPanel2.setBounds(new Rectangle(2, 403, 826, 309));
			jPanel2.add(getJPanel(), null);
			jPanel2.add(getJPanel1(), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jPanel3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(new BoxLayout(getJPanel3(), BoxLayout.X_AXIS));
			jPanel3.setBounds(new Rectangle(1, 715, 824, 21));
			jPanel3.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			jPanel3.add(statusBar, null);
		}
		return jPanel3;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setBounds(new Rectangle(511, 72, 285, 78));
			jScrollPane1
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane1.setViewportView(getUserIntroBox());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jScrollPane2
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getJTable1());
		}
		return jScrollPane2;
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setName("Tweet");
			jTabbedPane.setBounds(new Rectangle(2, 2, 826, 394));
			jTabbedPane.setEnabled(true);
			jTabbedPane.addTab("Timeline", null, getJScrollPane(), null);
			jTabbedPane.addTab("Mention", null, getJScrollPane2(), null);
			jTabbedPane.addTab("Message", null, getJScrollPane3(), null);
			jTabbedPane.addTab("SendMessage", null, getJScrollPane4(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable() {
		if (jTable == null) {
			tweetTableModel = new TweetTableModel();
			jTable = new JTable(tweetTableModel);
			jTable.setFont(new Font("Takao Pゴシック", Font.PLAIN, 10));
			jTable.addFocusListener(new java.awt.event.FocusAdapter() {
				@Override
				public void focusGained(java.awt.event.FocusEvent e) {
					// まだ見ていないtweet数を0にする
					mainAction.actionResetUncheckedTimelineTweetCount();
				}
			});
			jTable.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mousePressed(java.awt.event.MouseEvent e) {
					showPopup(e);
				}

				@Override
				public void mouseReleased(java.awt.event.MouseEvent e) {
					showPopup(e);
				}

				private void showPopup(MouseEvent e) {
					if (e.isPopupTrigger()) {
						// 右クリックのメニューを表示
						getRightClickPopup().show(e.getComponent(), e.getX(),
								e.getY());
					}
				}
			});

			createTweetTable(jTable);
		}
		return jTable;
	}

	/**
	 * This method initializes jTable1
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable1() {
		if (jTable1 == null) {
			mentionTableModel = new TweetTableModel();
			jTable1 = new JTable(mentionTableModel);
			jTable1.addFocusListener(new java.awt.event.FocusAdapter() {
				@Override
				public void focusGained(java.awt.event.FocusEvent e) {
					mainAction.actionResetUncheckedMentionTweetCount();
				}
			});
			jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mousePressed(java.awt.event.MouseEvent e) {
					showPopup(e);
				}

				@Override
				public void mouseReleased(java.awt.event.MouseEvent e) {
					showPopup(e);
				}

				private void showPopup(MouseEvent e) {
					if (e.isPopupTrigger()) {
						// 右クリックのメニューを表示
						getRightClickPopup().show(e.getComponent(), e.getX(),
								e.getY());
					}
				}
			});
			createTweetTable(jTable1);
		}
		return jTable1;
	}

	/**
	 * This method initializes jTextPane
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.setBackground(Color.white);
			jTextPane.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyReleased(java.awt.event.KeyEvent e) {
					// 残りつぶやける文字数情報を更新
					mainAction.actionUpdateTweetMessageCount();
				}
			});
			jTextPane.addFocusListener(new java.awt.event.FocusAdapter() {
				@Override
				public void focusGained(java.awt.event.FocusEvent e) {
					updateLen();
				}

				@Override
				public void focusLost(java.awt.event.FocusEvent e) {
					updateLen();
				}

				private void updateLen() {
					// 残りつぶやける文字数情報を更新
					mainAction.actionUpdateTweetMessageCount();
				}
			});

		}
		return jTextPane;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save");
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
		}
		return saveMenuItem;
	}

	/**
	 * This method initializes tweetMessageBox
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getTweetMessageBox() {
		if (tweetMessageBox == null) {
			tweetMessageBox = new JEditorPane();
			tweetMessageBox.setEditable(false);
			tweetMessageBox.setContentType("text/html");
			tweetMessageBox.setBackground(new Color(255, 255, 255));
			tweetMessageBox.addHyperlinkListener(new TweetHyperlinkHandler());
		}
		return tweetMessageBox;
	}

	/**
	 * This method initializes userIntroBox
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getUserIntroBox() {
		if (userIntroBox == null) {
			userIntroBox = new JEditorPane();
		}
		return userIntroBox;
	}

	/**
	 * This method initializes userWebBox
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getUserWebBox() {
		if (userWebBox == null) {
			userWebBox = new JEditorPane();
			userWebBox.setBounds(new Rectangle(8, 134, 201, 16));
			userWebBox.setContentType("text/html");
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
		}
		return userWebBox;
	}

	/**
	 * Twitter初期化
	 */
	private void init() {
        try {
            // twitterログイン
            tweetManager.loginTwitter();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
		// メインアクション初期化
		/*mainAction = new TweetMainAction(jFrame, tweetManager, statusBar,
				tweetTableModel, mentionTableModel, messageTableModel,
				sendMessageTableModel, jTable, jTable1, jTable2, jTable3,
				jTextPane, jLabel3, jPanel1, jTabbedPane, tweetMessageBox);
		// 自動更新開始
		mainAction.startTweetAutoUpdate();*/
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
			directMessageMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// ダイレクトメッセージ送信ダイアログを表示
							mainAction.actionShowDirectMessageDialog();
						}
					});
			rightClickPopup.add(directMessageMenuItem);
			JMenuItem retweetMenuItem = new JMenuItem("発言を公式リツイート");
			retweetMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// 選択したセルのステータスをRetweet
							mainAction.actionRetweet();
						}
					});
			rightClickPopup.add(retweetMenuItem);
			JMenuItem quoteMenuItem = new JMenuItem("発言をコメント付きリツイート");
			quoteMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// 選択したセルのステータスをコメント付Retweet
							mainAction.actionCopySelectedStatusToTweetBoxPane();
						}
					});
			rightClickPopup.add(quoteMenuItem);

			JMenuItem statusBrowserMenuItem = new JMenuItem("発言をブラウザで開く");
			statusBrowserMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// 選択したセルのステータスをブラウザで開く
							mainAction.actionOpenStatusURL();
						}
					});
			rightClickPopup.add(statusBrowserMenuItem);

			JMenuItem openBrowserUserInformationMenuItem = new JMenuItem(
					"この人のTimelineをブラウザで開く");
			openBrowserUserInformationMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// 選択したセルのユーザ情報をブラウザで開く
							mainAction.actionOpenUserURL();
						}
					});
			rightClickPopup.add(openBrowserUserInformationMenuItem);
		}
		return rightClickPopup;
	}

}