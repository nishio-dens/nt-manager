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
        private long lastUpdatedTime = 0;

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
            	long currentUpdated = System.currentTimeMillis();
            	if( currentUpdated - lastUpdatedTime > 5000) {
            		//スリープから復帰後、スリープ中に行っていなかったタスクを全部実行しようとするので阻止
            		task.runTask();
            	}
//            	else {
//            		System.out.println("Update Canceled");
//            	}
            	lastUpdatedTime = currentUpdated;
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
        private ScheduledExecutorService scheduler;
        private final Runnable task;
        private long period = 0;
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
         * @param immediatelyUpdate すぐに更新するかどうか
         */
        public void reset(boolean immediatelyUpdate) {
            stop();
            if (future != null) {
                if( immediatelyUpdate == true ) {
                    future = scheduler.scheduleAtFixedRate(task, 0, getPeriod(),
                            TimeUnit.MILLISECONDS);
                }else {
                    future = scheduler.scheduleAtFixedRate(task, getPeriod(), getPeriod(),
                            TimeUnit.MILLISECONDS);
                }
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
         * @param period 実行間隔[ms]
         */
        public void start(long period) {
            future = scheduler.scheduleAtFixedRate(task, 0, period,
                    TimeUnit.MILLISECONDS);
            this.setPeriod(period);
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

        /**
         * @return the period
         */
        public long getPeriod() {
            return period;
        }

        /**
         * 周期情報を変更
         * 周期を変更した際は，必ずresetをしなければタスクに反映されない
         * @param period the period to set
         */
        public void setPeriod(long period) {
            this.period = period;
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
        TimerData removeData = null;
        for (TimerData t : timerList) {
            if (t.getTimerID().equals(timerID)) {
                t.stop();
                t.shutdown();
                found = true;
                removeData = t;
                break;
            }
        }
        if( removeData != null ) {
            this.timerList.remove(removeData);
        }
        return found;
    }

    /**
     * タイマーの更新間隔をリセットする
     * @param timerID
     * @param immediatelyUpdate すぐに更新するかどうか
     * @return
     */
    public boolean resetTask(String timerID, boolean immediatelyUpdate) {
        boolean found = false;
        for (TimerData t : timerList) {
            if (t.getTimerID().equals(timerID)) {
                t.reset( immediatelyUpdate );
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * タスクの周期を更新する
     * @param timerID
     * @param period 周期[sec]
     * @param immediatelyUpdate すぐに情報を更新するかどうか
     * @return
     */
    public boolean updateTaskPeriod(String timerID, int period, boolean immediatelyUpdate) {
        boolean found = false;
        for (TimerData t : timerList) {
            if (t.getTimerID().equals(timerID)) {
                t.setPeriod(period * 1000);
                t.reset( immediatelyUpdate );
                found = true;
                break;
            }
        }
        return found;
    }

}
