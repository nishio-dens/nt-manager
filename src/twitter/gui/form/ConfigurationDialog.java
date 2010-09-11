package twitter.gui.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import twitter.gui.action.TweetMainAction;

public class ConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JTextField jTextField = null;
	private JLabel jLabel2 = null;
	private JTextField jTextField1 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JTextField jTextField2 = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JLabel jLabel7 = null;
	private JTextField jTextField3 = null;
	private JTextField jTextField4 = null;
	private JLabel jLabel8 = null;
	private JLabel jLabel9 = null;
	private JPanel jPanel = null;
	private JLabel jLabel11 = null;
	private JLabel jLabel12 = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private TweetMainAction mainAction = null;
	private JTabbedPane jTabbedPane = null;

	private final JDialog myself = this; // @jve:decl-index=0:visual-constraint="10,50"
	private JPanel jPanel1 = null;
	private JLabel jLabel10 = null;
	private JComboBox jComboBox = null;

	// 利用可能なフォント一覧
	private String[] fonts = null;
	// フォント一覧コンボボックスのモデル
	private final DefaultComboBoxModel fontModel = new DefaultComboBoxModel();
	private final DefaultComboBoxModel fontModel2 = new DefaultComboBoxModel();
	// フォントサイズコンボボックスのモデル
	private final DefaultComboBoxModel fontSizeModel = new DefaultComboBoxModel();
	private final DefaultComboBoxModel fontSizeModel2 = new DefaultComboBoxModel();
	private JLabel jLabel13 = null;
	private JComboBox jComboBox1 = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private JLabel jLabel14 = null;
	private JComboBox jComboBox2 = null;
	private JLabel jLabel15 = null;
	private JComboBox jComboBox3 = null;
	private JPanel jPanel4 = null;
	private JLabel jLabel16 = null;
	private JSlider jSlider = null;

	/**
	 * @param owner
	 */
	public ConfigurationDialog(Frame owner, TweetMainAction mainAction) {
		super(owner);
		initialize();
		this.mainAction = mainAction;

		// 利用可能なフォント一覧を取得しておく
		this.fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
		// 利用可能なフォント一覧を設定しておく
		if (fonts != null) {
			for (String f : fonts) {
				fontModel.addElement(f);
				fontModel2.addElement(f);
			}
		}
		// 利用可能なフォントサイズを設定しておく
		String[] fontSize = { "8", "9", "10", "11", "12", "13", "14", "15",
				"16", "17", "18" };
		for (String f : fontSize) {
			fontSizeModel.addElement(f);
			fontSizeModel2.addElement(f);
		}
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		try {
			// 画面が見えたときに情報更新
			jTextField2.setText(mainAction.getUpdatePeriod() + "");
			jTextField.setText(mainAction.getGetTimelinePeriodNum() + "");
			jTextField1.setText(mainAction.getGetMentionPeriodNum() + "");
			jTextField3.setText(mainAction.getGetDirectMessagePeriodNum() + "");
			jTextField4.setText(mainAction.getGetSendDirectMessagePeriodNum()
					+ "");
			jLabel12.setBackground(mainAction.getNewTableColor());

			// font関係
			if (mainAction.getTlFontName() != null) {
				jComboBox.setSelectedItem(mainAction.getTlFontName());
			}
			if (mainAction.getDetailFontName() != null) {
				jComboBox1.setSelectedItem(mainAction.getDetailFontName());
			}
			jComboBox2.setSelectedItem(mainAction.getTlFontSize() + "");
			jComboBox3.setSelectedItem(mainAction.getDetailFontSize() + "");

			// 表示
			jSlider.setValue(mainAction.getTableElementHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(610, 80);
		this.setTitle("Twitter Configuration");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			myself.setSize(new Dimension(447, 367));
			myself.setSize(new Dimension(450, 361));
			myself.setSize(new Dimension(444, 361));
			myself.setSize(new Dimension(446, 368));
			myself.setSize(new Dimension(456, 361));
			myself.setSize(new Dimension(459, 363));
			myself.setSize(new Dimension(458, 365));
			myself.setSize(new Dimension(460, 368));
			myself.setSize(new Dimension(462, 365));
			jLabel12 = new JLabel();
			jLabel12.setBackground(Color.black);
			jLabel12.setText("");
			jLabel12.setBounds(new Rectangle(128, 12, 242, 16));
			jLabel12.setOpaque(true);
			jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {

					Color c = JColorChooser.showDialog(myself, "テーブルカラーの選択",
							mainAction.getNewTableColor());
					if (c != null) {
						jLabel12.setBackground(c);
					}
				}
			});
			jLabel11 = new JLabel();
			jLabel11.setText("最新情報の背景色");
			jLabel11.setBounds(new Rectangle(10, 11, 114, 19));
			jLabel9 = new JLabel();
			jLabel9.setText("回に１回更新");
			jLabel9.setBounds(new Rectangle(297, 140, 104, 20));
			jLabel8 = new JLabel();
			jLabel8.setText("回に１回更新");
			jLabel8.setBounds(new Rectangle(297, 108, 104, 20));
			jLabel7 = new JLabel();
			jLabel7.setText("送信したDMの更新間隔");
			jLabel7.setBounds(new Rectangle(1, 140, 151, 20));
			jLabel6 = new JLabel();
			jLabel6.setText("DM更新間隔");
			jLabel6.setBounds(new Rectangle(1, 108, 151, 20));
			jLabel5 = new JLabel();
			jLabel5.setText("分間隔で更新");
			jLabel5.setBounds(new Rectangle(297, 12, 104, 20));
			jLabel4 = new JLabel();
			jLabel4.setText("情報更新間隔");
			jLabel4.setBounds(new Rectangle(1, 12, 151, 20));
			jLabel3 = new JLabel();
			jLabel3.setText("回に１回更新");
			jLabel3.setBounds(new Rectangle(297, 76, 104, 20));
			jLabel2 = new JLabel();
			jLabel2.setText("回に１回更新");
			jLabel2.setBounds(new Rectangle(297, 44, 104, 20));
			jLabel1 = new JLabel();
			jLabel1.setText("Mention更新間隔");
			jLabel1.setBounds(new Rectangle(1, 76, 151, 20));
			jLabel = new JLabel();
			jLabel.setText("タイムライン更新間隔");
			jLabel.setBounds(new Rectangle(1, 44, 151, 20));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJButton(), null);
			jContentPane.add(getJButton1(), null);
			jContentPane.add(getJTabbedPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(new Rectangle(161, 44, 129, 20));
		}
		return jTextField;
	}

	/**
	 * This method initializes jTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setBounds(new Rectangle(161, 76, 129, 20));
		}
		return jTextField1;
	}

	/**
	 * This method initializes jTextField2
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField2() {
		if (jTextField2 == null) {
			jTextField2 = new JTextField();
			jTextField2.setBounds(new Rectangle(161, 12, 129, 20));
		}
		return jTextField2;
	}

	/**
	 * This method initializes jTextField3
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField3() {
		if (jTextField3 == null) {
			jTextField3 = new JTextField();
			jTextField3.setBounds(new Rectangle(161, 108, 129, 20));
		}
		return jTextField3;
	}

	/**
	 * This method initializes jTextField4
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField4() {
		if (jTextField4 == null) {
			jTextField4 = new JTextField();
			jTextField4.setBounds(new Rectangle(161, 140, 129, 20));
		}
		return jTextField4;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(jLabel4, null);
			jPanel.add(getJTextField2(), null);
			jPanel.add(jLabel5, null);
			jPanel.add(jLabel2, null);
			jPanel.add(getJTextField(), null);
			jPanel.add(jLabel, null);
			jPanel.add(jLabel1, null);
			jPanel.add(getJTextField1(), null);
			jPanel.add(jLabel3, null);
			jPanel.add(jLabel8, null);
			jPanel.add(getJTextField3(), null);
			jPanel.add(jLabel6, null);
			jPanel.add(jLabel7, null);
			jPanel.add(getJTextField4(), null);
			jPanel.add(jLabel9, null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(208, 295, 110, 32));
			jButton.setText("設定を保存");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						// 更新間隔情報
						mainAction.setUpdatePeriod(Integer.parseInt(jTextField2
								.getText()));
						mainAction.setGetTimelinePeriodNum(Integer
								.parseInt(jTextField.getText()));
						mainAction.setGetMentionPeriodNum(Integer
								.parseInt(jTextField1.getText()));
						mainAction.setGetDirectMessagePeriodNum(Integer
								.parseInt(jTextField3.getText()));
						mainAction.setGetSendDirectMessagePeriodNum(Integer
								.parseInt(jTextField4.getText()));
						mainAction.setNewTableColor(jLabel12.getBackground());

						// フォント情報
						mainAction.setTlFontName((String) jComboBox
								.getSelectedItem());
						mainAction.setDetailFontName((String) jComboBox1
								.getSelectedItem());
						mainAction
								.setTlFontSize(Integer
										.parseInt((String) jComboBox2
												.getSelectedItem()));
						mainAction
								.setDetailFontSize(Integer
										.parseInt((String) jComboBox3
												.getSelectedItem()));
						// フォント情報反映
						mainAction.updateFontInformationToComponent();

						// 表示
						mainAction.setTableElementHeight(jSlider.getValue());
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					try {
						mainAction.saveProperties();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					// 閉じる
					myself.setVisible(false);
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
			jButton1.setBounds(new Rectangle(336, 295, 110, 32));
			jButton1.setText("キャンセル");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// 閉じる
					myself.setVisible(false);
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setBounds(new Rectangle(1, 3, 452, 286));
			jTabbedPane.addTab("更新間隔設定", null, getJPanel(), null);
			jTabbedPane.addTab("フォント", null, getJPanel1(), null);
			jTabbedPane.addTab("カラー", null, getJPanel2(), null);
			jTabbedPane.addTab("表示", null, getJPanel4(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel15 = new JLabel();
			jLabel15.setBounds(new Rectangle(11, 168, 188, 26));
			jLabel15.setText("詳細情報のフォントサイズ");
			jLabel14 = new JLabel();
			jLabel14.setBounds(new Rectangle(9, 136, 189, 25));
			jLabel14.setText("タイムラインのフォントサイズ");
			jLabel13 = new JLabel();
			jLabel13.setBounds(new Rectangle(8, 71, 190, 25));
			jLabel13.setText("詳細情報のフォント");
			jLabel10 = new JLabel();
			jLabel10.setBounds(new Rectangle(8, 9, 190, 25));
			jLabel10.setText("タイムラインのフォント");
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			jPanel1.add(jLabel10, null);
			jPanel1.add(getJComboBox(), null);
			jPanel1.add(jLabel13, null);
			jPanel1.add(getJComboBox1(), null);
			jPanel1.add(jLabel14, null);
			jPanel1.add(getJComboBox2(), null);
			jPanel1.add(jLabel15, null);
			jPanel1.add(getJComboBox3(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox(fontModel);
			jComboBox.setBounds(new Rectangle(8, 38, 425, 25));
		}
		return jComboBox;
	}

	/**
	 * This method initializes jComboBox1
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox1() {
		if (jComboBox1 == null) {
			jComboBox1 = new JComboBox(fontModel2);
			jComboBox1.setBounds(new Rectangle(8, 104, 427, 25));
		}
		return jComboBox1;
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
			jPanel2.add(getJPanel3(), null);
			jPanel2.add(jLabel11, null);
			jPanel2.add(jLabel12, null);
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
			jPanel3.setLayout(new GridBagLayout());
			jPanel3.setBounds(new Rectangle(0, 0, 0, 0));
		}
		return jPanel3;
	}

	/**
	 * This method initializes jComboBox2
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox2() {
		if (jComboBox2 == null) {
			jComboBox2 = new JComboBox(fontSizeModel);
			jComboBox2.setBounds(new Rectangle(210, 135, 225, 24));
		}
		return jComboBox2;
	}

	/**
	 * This method initializes jComboBox3
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox3() {
		if (jComboBox3 == null) {
			jComboBox3 = new JComboBox(fontSizeModel2);
			jComboBox3.setBounds(new Rectangle(210, 168, 225, 24));
		}
		return jComboBox3;
	}

	/**
	 * This method initializes jPanel4
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jLabel16 = new JLabel();
			jLabel16.setBounds(new Rectangle(12, 12, 198, 49));
			jLabel16.setText("タイムラインのテーブルの高さ");
			jPanel4 = new JPanel();
			jPanel4.setLayout(null);
			jPanel4.add(jLabel16, null);
			jPanel4.add(getJSlider(), null);
		}
		return jPanel4;
	}

	/**
	 * This method initializes jSlider
	 * 
	 * @return javax.swing.JSlider
	 */
	private JSlider getJSlider() {
		if (jSlider == null) {
			jSlider = new JSlider();
			jSlider.setBounds(new Rectangle(216, 12, 222, 49));
			jSlider.setMaximum(200);
			jSlider.setMinimum(1);
			jSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					// テーブルの高さを更新
					mainAction.updateTableHeight(jSlider.getValue());
				}
			});
		}
		return jSlider;
	}
}
