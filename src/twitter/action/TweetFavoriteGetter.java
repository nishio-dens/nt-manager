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
 *
 * @author nishio
 */
public class TweetFavoriteGetter implements TweetGetter{

    //tweet管理用
    private TweetManager tweetManager;
    //sinceid
    private long sinceID;
    //検索するユーザの名前
    private String screenName = null;

    /**
     * 自分自身のお気に入りを取得
     * @param tweetManager
     */
    public TweetFavoriteGetter(TweetManager tweetManager) {
        this.tweetManager = tweetManager;
    }

    /**
     * screenNameのお気に入りを取得
     * @param tweetManager
     */
    public TweetFavoriteGetter(TweetManager tweetManager, String screenName) {
        this.tweetManager = tweetManager;
        this.screenName = screenName;
    }

    /**
     * お気に入りを取得
     * @param num この部分は無視される
     * @return
     */
    @Override
    public List<Status> getTweetData(int num) {
        List<Status> status = null;
        try {
            status = tweetManager.getFavoritesTweet(screenName);
        } catch (TwitterException ex) {
            Logger.getLogger(TweetFavoriteGetter.class.getName()).log(Level.SEVERE, null, ex);
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
        return getTweetData(0);
    }

	@Override
	public void setUpdateListener(TweetStreamingListener listener) {
		// TODO 自動生成されたメソッド・スタブ

	}

	/**
     * streaming api有効時のアップデートを受け取るlistenerを削除
     */
    public void stopUpdateListener() {
    	//TODO
    }

}