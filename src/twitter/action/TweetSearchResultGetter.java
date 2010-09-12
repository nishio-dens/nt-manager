/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action;

import java.util.List;
import twitter.manage.TweetManager;
import twitter4j.Status;

/**
 * 
 * @author nishio
 */
public class TweetSearchResultGetter implements TweetGetter{

    //tweet管理用
    private TweetManager tweetManager;

    /**
     *
     * @param tweetManager
     */
    public TweetSearchResultGetter(TweetManager tweetManager) {
       this.tweetManager = tweetManager;
    }

    public List<Status> getTweetData(int num) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Status> getNewTweetData(long sinceID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
