/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.manage;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;
import twitter4j.Status;

/**
 * 通知すべきメッセージの管理を行う
 * @author nishio
 */
public class TweetNotifyManager {

    //トレイアイコン
    private TrayIcon trayIcon = null;
    //タイトル
    private String title = "あなた宛のメッセージ";

    /**
     *
     * @param trayIcon
     */
    public TweetNotifyManager(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
    }

    public TweetNotifyManager(TrayIcon trayIcon, String title) {
        this.trayIcon = trayIcon;
        this.title = title;
    }

    /**
     * ツイート情報を通知バーに表示
     * @param status
     */
    public void showNotifyMessage(List<Status> status) {
        for(Status s : status) {
            if( s.getUser() == null ) {
                String message = s.getText();
                this.trayIcon.displayMessage(title, message,TrayIcon.MessageType.INFO);
            }else {
                String name = s.getUser().getScreenName();
                String message = s.getText();
                this.trayIcon.displayMessage(title, name + "さんの発言: " + message, TrayIcon.MessageType.INFO);
            }
        }
    }

    /**
     * ツイート情報を通知バーに表示
     * @param status
     */
    public void showNotifyMessage(Status status) {
    	List<Status> statuses = new ArrayList<Status>();
    	statuses.add(status);
    	showNotifyMessage(statuses);
    }

}
