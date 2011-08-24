/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action;

import java.awt.TrayIcon;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter.action.streaming.TweetStreamingListener;
import twitter.manage.TweetManager;
import twitter.manage.TweetNotifyManager;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 *
 * @author nishio
 */
public class TweetDirectMessageGetter implements TweetGetter{

    //tweet管理用
    private TweetManager tweetManager;
    //通知
    private TweetNotifyManager notifyManager = null;
    //1回目の最初の呼び出しかどうか, 1回目の呼び出しの際は通知バーにメッセージを表示しない
    private boolean isFirstTime = true;

    /**
     *
     * @param tweetManager
     */
    public TweetDirectMessageGetter(TweetManager tweetManager) {
        this.tweetManager = tweetManager;
    }

    /**
     *
     * @param tweetManager
     * @param trayIcon
     */
    public TweetDirectMessageGetter(TweetManager tweetManager, TrayIcon trayIcon) {
        this.tweetManager = tweetManager;
        this.notifyManager = new TweetNotifyManager(trayIcon, "あなた宛のダイレクトメッセージ");
    }

    /**
     * DirectMessageツイートを指定した数だけ取得
     * @param num
     * @return
     */
    @Override
    public List<Status> getTweetData(int num) {
        List<Status> status = null;
        try {
            status = tweetManager.getDirectMessages(num);
            if( notifyManager != null && isFirstTime == false) {
                this.notifyManager.showNotifyMessage(status);
            }
            isFirstTime = false;
        } catch (TwitterException ex) {
            Logger.getLogger(TweetMentionGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    /**
     * DirectMessageツイートの新しく投稿されたものだけを取得
     * @param sinceID
     * @return
     */
    @Override
    public List<Status> getNewTweetData() {
        List<Status> status = null;
        try {
            status = tweetManager.getNewDirectMessages();
            if( notifyManager != null && isFirstTime == false) {
                this.notifyManager.showNotifyMessage(status);
            }
            isFirstTime = false;
        } catch (TwitterException ex) {
            Logger.getLogger(TweetMentionGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

	@Override
	public void setUpdateListener(TweetStreamingListener listener) {
		if( listener != null ) {
			tweetManager.getStreamManager().setDirectMessageListener(listener, this.notifyManager);
		}
	}

	/**
     * streaming api有効時のアップデートを受け取るlistenerを削除
     */
    public void stopUpdateListener() {
    	tweetManager.getStreamManager().stopDirectMessageListener();
    }

}

