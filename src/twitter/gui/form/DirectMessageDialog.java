package twitter.gui.form;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import twitter.gui.action.DirectMessageAction;
import twitter.manage.TweetManager;
import twitter4j.TwitterException;

/**
 * ダイレクトメッセージ送信用ダイアログ
 * 
 * @author nishio
 * 
 */
public class DirectMessageDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JDialog dialog = null;
	private JPanel jContentPane = null;
	private JLabel toUserImage = null;
	private JLabel jLabel = null;
	private JScrollPane jScrollPane = null;
	private JTextPane jTextPane = null;
	private JLabel jLabel1 = null;
	private JButton jButton = null;
	private DirectMessageAction messageAction = null;

	/**
	 * @param owner
	 */
	public DirectMessageDialog(Frame owner) {
		super(owner);
		initialize();
		messageAction = new DirectMessageAction(jLabel, toUserImage, jLabel1,
				jTextPane);
		dialog = this;
	}

	/**
	 * ダイレクトメッセージ送信ユーザの情報を設定
	 * 
	 * @param username
	 * @param userImageURL
	 */
	public void setUserInformation(String username, URL userImageURL,
			TweetManager tweetManager) {
		this.messageAction.setUserInformation(username, userImageURL);
		this.messageAction.setTweetManager(tweetManager);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(705, 143);
		this.setTitle("Direct Message Dialog");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(605, 16, 89, 24));
			jLabel1.setText("140");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(4, 82, 134, 21));
			jLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			jLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jLabel.setText("UserName");
			toUserImage = new JLabel();
			toUserImage.setBounds(new Rectangle(47, 25, 55, 51));
			toUserImage.setText("");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(toUserImage, null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getJScrollPane(), null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJButton(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(148, 14, 453, 91));
			jScrollPane.setViewportView(getJTextPane());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextPane
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.setBackground(new Color(255, 255, 255));
			jTextPane.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyReleased(java.awt.event.KeyEvent e) {
					// 残りつぶやける文字数情報を更新
					messageAction.actionUpdateTweetMessageCount();
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
					messageAction.actionUpdateTweetMessageCount();
				}
			});
		}
		return jTextPane;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(606, 45, 87, 57));
			jButton.setText("送信");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						messageAction.sendDirectMessage();
						// ダイアログを閉じる
						dialog.setVisible(false);
						// メッセージを送信しました
						JOptionPane.showMessageDialog(null,
								"ダイレクトメッセージ送信に成功しました",
								"Direct Message Information",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (TwitterException e1) {
						JOptionPane.showMessageDialog(null,
								"ダイレクトメッセージを送信できませんでした．",
								"Send DirectMessage Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
		return jButton;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
