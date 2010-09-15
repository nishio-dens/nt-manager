/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.task;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author nishio
 */
public class TimerID {
    //singleton pattern
    private static TimerID timerID;
    //ID情報を管理するクラス
    private Set<String> idList;

    /**
     * singleton コンストラクタ
     */
    private TimerID() {
        idList = new HashSet<String>();
    }

    /**
     * TimerIDインスタンスを取得
     * @return
     */
    public static synchronized TimerID getInstance() {
        if( timerID == null ) {
            timerID = new TimerID();
        }
        return timerID;
    }

    /**
     * 指定したIDがすでに存在するかどうか
     * @param id
     * @return
     */
    public boolean contains(String id) {
        return idList.contains(id);
    }

    /**
     * 利用したいIDを追加
     * @param id
     * @throws ExistTimerIDException 既に指定したタイマーIDが存在している
     */
    public void addID(String id) throws ExistTimerIDException {
        if( contains(id) == true ) {
            throw new ExistTimerIDException("既にそのIDは存在しています");
        }
        if( id == null ) {
            throw new NullPointerException();
        }
        this.idList.add(id);
    }

    /**
     * 利用していたIDを削除
     * @param id
     * @return idがそもそも存在していたかどうか 存在していた場合trueを返す
     */
    public boolean removeID(String id) {
        return this.idList.remove(id);
    }
    
    /**
     * 情報検索の際に利用するタイマーのIDを生成
     * @param searchWord
     * @return
     */
    public static String createSearchTimerID(String searchWord) {
        return "SEARCH:" + searchWord;
    }

    /**
     * timelineのIDを生成
     * @return
     */
    public static String createTimelineID() {
        return "TIMELINE";
    }

    /**
     * MentionのIDを生成
     * @return
     */
    public static String createMentionID() {
        return "MENTION";
    }

    /**
     * DMのIDを生成
     * @return
     */
    public static String createDirectMessageID() {
        return "DIRECTMESSAGE";
    }

    /**
     * 送信したDMのIDを生成
     * @return
     */
    public static String createSendDirectMessageID() {
        return "SENDDIRECTMESSAGE";
    }
}
