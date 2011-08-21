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
 * 指定したワードを含むツイートを取得する
 * @author nishio
 */
public class TweetSearchResultGetter implements TweetGetter{

    //tweet管理用
    private TweetManager tweetManager;
    //検索したいワード
    private String searchWord;
    //sinceid
    private long sinceID;

    /**
     *
     * @param tweetManager
     */
    public TweetSearchResultGetter(TweetManager tweetManager, String searchWord) {
        this.tweetManager = tweetManager;
        this.searchWord = searchWord;
    }

    /**
     * 指定したワードのツイートを指定した数だけ取得
     * @param num
     * @return
     */
    public List<Status> getTweetData(int num) {
        List<Status> status = tweetManager.getSearchResult(num, searchWord);
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
    public List<Status> getNewTweetData() {
        List<Status> status = tweetManager.getNewSearchResult(this.sinceID, this.searchWord);
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
		if( listener != null ) {
			tweetManager.getStreamManager().setSearchListener(this.searchWord, listener);
		}
	}

}
