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
 *
 * @author nishio
 */
public class TweetSendDirectMessageGetter implements TweetGetter{

    //tweet管理用
    private TweetManager tweetManager;

    /**
     *
     * @param tweetManager
     */
    public TweetSendDirectMessageGetter(TweetManager tweetManager) {
        this.tweetManager = tweetManager;
    }

    /**
     * SendDMツイートを指定した数だけ取得
     * @param num
     * @return
     */
    @Override
    public List<Status> getTweetData(int num) {
        List<Status> status = null;
        try {
            status = tweetManager.getSendDirectMessages(num);
        } catch (TwitterException ex) {
            Logger.getLogger(TweetMentionGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    /**
     * SendDMツイートの新しく投稿されたものだけを取得
     * @param sinceID
     * @return
     */
    @Override
    public List<Status> getNewTweetData() {
        List<Status> status = null;
        try {
            status = tweetManager.getNewSendDirectMessages();
        } catch (TwitterException ex) {
            Logger.getLogger(TweetMentionGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

}

