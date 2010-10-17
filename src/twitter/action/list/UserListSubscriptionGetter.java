/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action.list;

import java.util.List;
import twitter.manage.TweetManager;
import twitter4j.UserList;

/**
 * 指定したユーザをフォローしているリストを返す
 * @author nishio
 */
public class UserListSubscriptionGetter implements UserListGetter{

    private TweetManager tweetManager = null;

    /**
     * 
     * @param manager
     */
    public UserListSubscriptionGetter(TweetManager manager) {
        this.tweetManager = manager;
    }

    @Override
    public List<UserList> getUserLists(String screenName) {
        return tweetManager.getUserListSubscriptions(screenName);
    }

}
