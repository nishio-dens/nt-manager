/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action;

import java.util.List;

import twitter.action.streaming.TweetStreamingListener;
import twitter.manage.TweetManager;
import twitter4j.Status;

/**
 *
 * @author nishio
 */
public class TweetListGetter implements TweetGetter{
    //tweet管理用
    private TweetManager tweetManager;
    //リストID
    private int listID;
    //ユーザ名
    private String username = null;
    //sinceID
    private long sinceID;

    /**
     *
     * @param tweetManager
     * @param username ユーザ名
     * @param listID リストのID
     */
    public TweetListGetter(TweetManager tweetManager, String username, int listID) {
        this.tweetManager = tweetManager;
        this.username = username;
        this.listID = listID;
    }

    @Override
    public List<Status> getTweetData(int num) {
        List<Status> status = tweetManager.getUserListStatuses(username, listID, num);
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
    public List<Status> getNewTweetData() {
        List<Status> status = tweetManager.getNewUserListStatuses(username, listID, sinceID);
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
		// TODO 自動生成されたメソッド・スタブ

	}

}
