/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter.action.streaming.TweetStreamingListener;
import twitter.manage.TweetManager;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * 指定したユーザの発言を取得
 * @author nishio
 */
public class TweetUserTimelineGetter implements TweetGetter{

    //tweet管理用
    private TweetManager tweetManager;
    //検索したいユーザ
    private String screenName;
    //検索したいユーザid(streaming api利用時)
    private long userid = 0;
    //sinceid
    private long sinceID;

    /**
     *
     * @param tweetManager
     * @param userID2
     */
    public TweetUserTimelineGetter(TweetManager tweetManager, String screenName) {
        this.tweetManager = tweetManager;
        this.screenName = screenName;
    }

    /**
     * 指定した人のツイートを指定した数だけ取得
     * @param num
     * @return
     */
    @Override
    public List<Status> getTweetData(int num) {
        List<Status> status = null;
        try {
            status = tweetManager.getUserTimeline(num, screenName);
        } catch (TwitterException ex) {
            Logger.getLogger(TweetUserTimelineGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if( status != null ) {
            //一番最後のtweetのsinceIDを取得する
            int lastnum = status.size();
            if( lastnum > 0 ) {
                sinceID = status.get(lastnum - 1).getId();
            }
        }
        return status;
    }

    /**
     * 指定したワードのツイートの新しく投稿されたものだけを取得
     * @param sinceID
     * @return
     */
    @Override
    public List<Status> getNewTweetData() {
    	//streaming利用時のsinceidを取得
    	long streamingSinceID = tweetManager.getStreamManager().getUserLastUpdateID(userid);
    	if( streamingSinceID > 0 ) {
    		sinceID = streamingSinceID;
    	}

        List<Status> status = null;
        try {
            status = tweetManager.getNewUserTimeline(screenName, sinceID);
        } catch (TwitterException ex) {
            Logger.getLogger(TweetUserTimelineGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if( status != null ) {
            //一番最後のtweetのsinceIDを取得する
            int lastnum = status.size();
            if( lastnum > 0 ) {
                sinceID = status.get(lastnum - 1).getId();
            }
        }
        return status;
    }

	@Override
	public void setUpdateListener(TweetStreamingListener listener) {
		this.userid = tweetManager.getUserID(this.screenName);
		if( this.userid != 0 && listener != null ) {
			tweetManager.getStreamManager().setUserListener(userid, listener);
		}
	}

	/**
     * streaming api有効時のアップデートを受け取るlistenerを削除
     */
    public void stopUpdateListener() {
    	tweetManager.getStreamManager().stopUserListener(userid);
    }

}