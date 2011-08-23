/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter.action.streaming.TweetStreamingListener;
import twitter.gui.action.TweetMainAction;
import twitter.log.TwitterLogManager;
import twitter.manage.TweetManager;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 *
 * @author nishio
 */
public class TweetTimelineGetter implements TweetGetter{

    //tweet管理用
    private TweetManager tweetManager;
    private TweetMainAction mainAction = null;
    private TwitterLogManager logManager = null;

    /**
     *
     * @param tweetManager
     */
    public TweetTimelineGetter(TweetManager tweetManager, TweetMainAction mainAction) {
        this.tweetManager = tweetManager;
        this.mainAction = mainAction;
        this.logManager = new TwitterLogManager();
    }

    /**
     * timelineツイートを指定した数だけ取得
     * @param num
     * @return
     */
    @Override
    public List<Status> getTweetData(int num) {
        List<Status> status = null;
        try {
            status = tweetManager.getTimeline(num);
            if( mainAction.isSaveLog() == true && status != null ) {
                try {
                    // ログ保存
                    logManager.add(status);
                } catch (IOException ex) {
                    Logger.getLogger(TweetTimelineGetter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (TwitterException ex) {
            Logger.getLogger(TweetMentionGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    /**
     * timelineツイートの新しく投稿されたものだけを取得
     * @return
     */
    @Override
    public List<Status> getNewTweetData() {
        List<Status> status = null;
        try {
            status = tweetManager.getNewTimelineData();
             if( mainAction.isSaveLog() == true && status != null ) {
                try {
                    // ログ保存
                    logManager.add(status);
                } catch (IOException ex) {
                    Logger.getLogger(TweetTimelineGetter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (TwitterException ex) {
            Logger.getLogger(TweetMentionGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    /**
     * streaming apiのリスナー登録
     */
	@Override
	public void setUpdateListener(TweetStreamingListener listener) {
		if( listener != null ) {
			tweetManager.getStreamManager().setTimelineListener(listener);
		}
	}

	/**
     * streaming api有効時のアップデートを受け取るlistenerを削除
     */
    public void stopUpdateListener() {
    	this.tweetManager.getStreamManager().stopTimelineListener();
    }
}

