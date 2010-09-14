/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.task;

/**
 *
 * @author nishio
 */
public class TweetTaskException extends Exception {

    /**
     * Creates a new instance of <code>TweetTaskException</code> without detail message.
     */
    public TweetTaskException() {
    }


    /**
     * Constructs an instance of <code>TweetTaskException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TweetTaskException(String msg) {
        super(msg);
    }
}
