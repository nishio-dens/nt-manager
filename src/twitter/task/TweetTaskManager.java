/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * タスクを放り込むと指定した間隔で実行してくれるクラス
 * @author nishio
 */
public class TweetTaskManager {

    /**
     * 実際に行うタスク
     */
    private class TweetTaskTimerTask implements Runnable {
        private TweetUpdateTask task;

        /**
         *
         * @param task
         */
        public TweetTaskTimerTask( TweetUpdateTask task ) {
            this.task = task;
        }

        /**
         * 実際に実行するタスク
         */
        @Override
        public void run() {
            try {
                //タスク実行
                task.runTask();
            } catch (TweetTaskException ex) {
                Logger.getLogger(TweetTaskManager.class.getName()).log(
                        Level.SEVERE, "TimerTask内でエラーが発生しました", ex);
            }
        }
    }

    /**
     * Timerに関するデータを保持
     */
    private class TimerData {

        private ScheduledFuture<?> future;
        private final ScheduledExecutorService scheduler;
        private final Runnable task;
        private long time = 0;
        private String timerID = null;

        /**
         * 
         * @param task
         * @param timerID タイマー識別の為に名前を付ける
         */
        public TimerData(TweetUpdateTask task, String timerID) {
            this.task = new TweetTaskTimerTask(task);
            scheduler = Executors.newSingleThreadScheduledExecutor();
            if( timerID == null ) {
                throw new NullPointerException("TimerIDが設定されていません");
            }
            this.timerID = timerID;
        }

        /**
         * 更新リセット
         */
        public void reset() {
            stop();
            if (future != null) {
                future = scheduler.scheduleAtFixedRate(task, time, time,
                        TimeUnit.MILLISECONDS);
            }
        }

        /**
         * シャットダウン
         */
        public void shutdown() {
            scheduler.shutdown();
        }

        /**
         * 一定時間毎にTweetUpdateTaskを実行
         *
         * @param time 実行間隔[ms]
         */
        public void start(long time) {
            future = scheduler.scheduleAtFixedRate(task, 0, time,
                    TimeUnit.MILLISECONDS);
            this.time = time;
        }

        /**
         * タスク終了
         */
        public void stop() {
            if (future != null) {
                future.cancel(true);
            }
        }

        /**
         * @return the timerID
         */
        public String getTimerID() {
            return timerID;
        }

        /**
         * @param timerID the timerID to set
         */
        public void setTimerID(String timerID) {
            this.timerID = timerID;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TimerData other = (TimerData) obj;
            if ((this.timerID == null) ? (other.timerID != null) : !this.timerID.equals(other.timerID)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 23 * hash + (this.timerID != null ? this.timerID.hashCode() : 0);
            return hash;
        }

    }

    //タイマー
    private List<TimerData> timerList = null;


    /**
     *
     */
    public TweetTaskManager() {
       timerList = new ArrayList<TimerData>();
    }

    /**
     * タスクを追加
     * @param timerID タイマー識別の為に名前をつける
     * @param task 実行したいタスク
     */
    public void addTask(String timerID, TweetUpdateTask task) throws TweetTaskException {
        //すでに同じIDのタスクが存在していたら追加できないようにする
        if( timerList.contains( new TimerData(null, timerID) ) ) {
            throw new TweetTaskException("すでにタスクが存在しています");
        }
        //新しいタスクを追加
        TimerData t = new TimerData(task, timerID);
        timerList.add(t);
    }

    /**
     * タスク実行
     * @param timerID タイマー識別子
     * @param period 実行間隔[ms]
     * @return タイマーの実行に成功したかどうか
     */
    public boolean startTask(String timerID, long period) throws TweetTaskException {
        boolean found = false;
        for( TimerData t : timerList ) {
            if( t.getTimerID().equals(timerID) ) {
                t.start(period);
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * タスクを終了する
     * @param timerID
     * @return
     */
    public boolean shutdownTask(String timerID) {
        boolean found = false;
        for (TimerData t : timerList) {
            if (t.getTimerID().equals(timerID)) {
                t.stop();
                t.shutdown();
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * タイマーの更新間隔をリセットする
     * @param timerID
     * @return
     */
    public boolean resetTask(String timerID) {
        boolean found = false;
        for (TimerData t : timerList) {
            if (t.getTimerID().equals(timerID)) {
                t.reset();
                found = true;
                break;
            }
        }
        return found;
    }

}
