/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private int userID;
    //sinceid
    private long sinceID;

    /**
     *
     * @param tweetManager
     * @param userID
     */
    public TweetUserTimelineGetter(TweetManager tweetManager, int userID) {
        this.tweetManager = tweetManager;
        this.userID = userID;
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
            status = tweetManager.getUserTimeline(num, userID);
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
        List<Status> status = null;
        try {
            status = tweetManager.getNewUserTimeline(userID, sinceID);
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

}