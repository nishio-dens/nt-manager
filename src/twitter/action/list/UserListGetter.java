/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.action.list;

import java.util.List;
import twitter4j.UserList;

/**
 * 
 * @author nishio
 */
public interface  UserListGetter {
    List<UserList> getUserLists(String screenName);
}
