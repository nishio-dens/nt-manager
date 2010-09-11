package twitter.gui.action;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import twitter.manage.TweetManager;
import twitter4j.TwitterException;

/**
 * ダイレクトメッセージ送信用ダイアログのアクション
 * 
 * @author nishio
 * 
 */
public class DirectMessageAction {

	// Tweet管理
	private TweetManager tweetManager = null;
	// ユーザ名表示ラベル
	private JLabel userNameLabel = null;
	// ユーザイメージ表示ラベル
	private JLabel userImageLabel = null;
	// Statusを書くTextbox
	private JTextPane statusTextBox = null;
	// つぶやくことができる文字数を表示するラベル
	private JLabel tweetMessageCountLabel = null;

	public DirectMessageAction(JLabel userNameLabel, JLabel userImageLabel,
			JLabel tweetMessageCountLabel, JTextPane statusTextBox) {
		this.tweetManager = tweetManager;
		this.userNameLabel = userNameLabel;
		this.userImageLabel = userImageLabel;
		this.tweetMessageCountLabel = tweetMessageCountLabel;
		this.statusTextBox = statusTextBox;
	}

	/**
	 * Tweet管理マネージャをセット
	 * 
	 * @param tweetManager
	 */
	public void setTweetManager(TweetManager tweetManager) {
		this.tweetManager = tweetManager;
	}

	/**
	 * ダイレクトメッセージ送信ユーザ情報を設定
	 * 
	 * @param username
	 * @param userImageURL
	 */
	public void setUserInformation(String username, URL userImageURL) {
		userNameLabel.setText(username);
		// アイコン設定
		userImageLabel.setIcon(new ImageIcon(userImageURL));
	}

	/**
	 * ダイレクトメッセージを送信
	 * 
	 * @throws TwitterException
	 */
	public void sendDirectMessage() throws TwitterException {
		this.tweetManager.sendDirectMessage(this.userNameLabel.getText(),
				this.statusTextBox.getText());
		this.statusTextBox.setText("");
	}

	/**
	 * つぶやける残り文字数の更新
	 * 
	 * @param e
	 */
	public void actionUpdateTweetMessageCount() {
		int len = 140 - (statusTextBox.getText().length());
		if (len < 0) {
			len = 0;
		}
		tweetMessageCountLabel.setText(len + "");
	}

}
