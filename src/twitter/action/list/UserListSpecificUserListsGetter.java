/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action.list;

import java.util.List;
import twitter.manage.TweetManager;
import twitter4j.UserList;

/**
 * 指定したユーザが保持しているリスト一覧を取得
 * @author nishio
 */
public class UserListSpecificUserListsGetter implements UserListGetter{

    private TweetManager tweetManager = null;

    /**
     *
     * @param manager
     */
    public UserListSpecificUserListsGetter(TweetManager manager) {
        this.tweetManager = manager;
    }

    @Override
    public List<UserList> getUserLists(String screenName) {
        return tweetManager.getUserLists(screenName);
    }

}
