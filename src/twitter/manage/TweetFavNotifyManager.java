/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter.manage;

import java.awt.TrayIcon;
import java.util.List;
import twitter4j.Status;
import twitter4j.User;

/**
 *
 * @author nishio
 */
public class TweetFavNotifyManager{
    
    //トレイアイコン
    protected TrayIcon trayIcon = null;
    //タイトル
    protected String title = "お気に入り登録情報";
   
    public TweetFavNotifyManager(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
    }
    
    public TweetFavNotifyManager(TrayIcon trayIcon, String title) {
        this.trayIcon = trayIcon;
	this.title = title;
    }
    
    /**
     * ツイート情報を通知バーに表示
     * @param status
     */
    public void showNotifyMessage(User source, User target, Status favoritedStatus) {
        if( trayIcon != null ) {
	    String message = favoritedStatus.getText();
	    if( message.length() > 80) {
		message = message.substring(0, 80);
		message = message + "...";
	    }
	    this.trayIcon.displayMessage(title, source.getScreenName() + "さんが、あなたの発言:\n "
		    + message + " をお気に入りに登録しました",TrayIcon.MessageType.INFO);
	}
    }
}
