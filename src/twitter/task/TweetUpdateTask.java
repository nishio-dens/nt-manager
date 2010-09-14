/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.task;

/**
 *
 * @author nishio
 */
public interface TweetUpdateTask {
    /**
     * 一定間隔で実行するタスクの処理
     * @throws TweetTaskException
     */
    void runTask() throws TweetTaskException;
}
