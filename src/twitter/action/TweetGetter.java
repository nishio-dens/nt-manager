/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action;

import java.util.List;

import twitter.action.streaming.TweetStreamingListener;
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

    /**
     * streaming api有効時、アップデートを受け取るlistenerをセット
     * @param listener
     */
    public void setUpdateListener(TweetStreamingListener listener);

    /**
     * streaming api有効時のアップデートを受け取るlistenerを削除
     */
    public void stopUpdateListener();
}
