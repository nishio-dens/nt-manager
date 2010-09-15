/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.task;

/**
 *
 * @author nishio
 */
public class ExistTimerIDException extends Exception {

    /**
     * Creates a new instance of <code>ExistTimerIDException</code> without detail message.
     */
    public ExistTimerIDException() {
    }


    /**
     * Constructs an instance of <code>ExistTimerIDException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ExistTimerIDException(String msg) {
        super(msg);
    }
}
