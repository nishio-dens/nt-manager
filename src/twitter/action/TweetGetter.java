/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action;

import java.util.List;
import twitter4j.Status;

/**
 *
 * @author nishio
 */
public interface TweetGetter {
    
    /**
     * 指定したnum分だけtweetを取得
     * @param num
     * @return
     */
    public List<Status> getTweetData(int num);

    /**
     * 最新のtweet情報を取得
     * @return
     */
    public List<Status> getNewTweetData();
}
