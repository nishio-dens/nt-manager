package twitter.gui.component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import twitter.util.HTMLEncode;
import twitter4j.Status;

/**
 * 
 * @author nishio
 * 
 */
public class TweetTableModel extends DefaultTableModel {

	private static class ColumnContext {
		public final Class columnClass;
		public final String columnName;
		public final boolean isEditable;

		public ColumnContext(String columnName, Class columnClass,
				boolean isEditable) {
			this.columnName = columnName;
			this.columnClass = columnClass;
			this.isEditable = isEditable;
		}
	}

	private static final ColumnContext[] columnArray = {
			new ColumnContext("User", ImageIcon.class, false),
			new ColumnContext("Comment", String.class, false),
			new ColumnContext("Info", String.class, false) };
	// 時間表示フォーマット
	private final SimpleDateFormat tweetDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	// 取得した情報を蓄えておく
	private List<Status> tweetStatus = null;

	/**
     * 
     */
	public TweetTableModel() {
		super();
		tweetStatus = new LinkedList<Status>();
	}

	/**
	 * 取得していた情報をすべて削除する
	 */
	public void clearStatus() {
		tweetStatus.clear();
	}

	/**
	 * 時間の秒の差を求める
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	private long differenceTime(Date date1, Date date2) {
		long datetime1 = date1.getTime();
		long datetime2 = date2.getTime();
		return (datetime1 - datetime2);
	}

	/**
	 * 時間の秒の差を求める
	 * 
	 * @param strDate1
	 * @param strDate2
	 * @return
	 * @throws ParseException
	 */
	private long differenceTime(String strDate1, String strDate2)
			throws ParseException {
		Date date1 = DateFormat.getDateInstance().parse(strDate1);
		Date date2 = DateFormat.getDateInstance().parse(strDate2);
		return differenceTime(date1, date2);
	}

	@Override
	public Class<?> getColumnClass(int modelIndex) {
		return columnArray[modelIndex].columnClass;
	}

	@Override
	public int getColumnCount() {
		return columnArray.length;
	}

	@Override
	public String getColumnName(int modelIndex) {
		return columnArray[modelIndex].columnName;
	}

	/**
	 * 指定した行のユーザステータスを取得
	 * 
	 * @param row
	 * @return
	 */
	public Status getTweetStatus(int row) {
		// TODO:リンクリストだと行数が多くなると読み込み遅くなりそう 改良を検討
        Status s = null;
        try {
            s = tweetStatus.get(row);
        }catch(Exception e) {
            e.printStackTrace();
        }
		return s;
	}

	/**
	 * Tweetをテーブルに追加
	 * 
	 * @param t
	 */
	public void insertTweet(Status t) {
		// 1:ユーザのイメージ
		// 2:ユーザのつぶやき
		// 3.つぶやいた時間，つぶやいたクライアントなど

		Object[] obj = {
				new ImageIcon(t.getUser().getProfileImageURL()),
				"<b>" + t.getUser().getScreenName() + "</b> "
						+ HTMLEncode.encode(t.getText()),
				tweetDateFormat.format(t.getCreatedAt()) + "<br> "
						+ t.getSource() + "から" };
		try {
			super.insertRow(0, obj);
			tweetStatus.add(0, t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// super.addRow(obj);
	}

	/**
	 * Tweetテーブルに存在する要素数
	 * 
	 * @return
	 */
	public int getTweetTableSize() {
		return this.tweetStatus.size();
	}

	/**
	 * num件数分の古いtweetを削除
	 * 
	 * @param num
	 */
	public void removeOldTweet(int num) {
		if (this.tweetStatus.size() < num) {
			return;
		}
		try {
			int deleteNum = tweetStatus.size() - num;
			for (int i = 0; i < deleteNum; i++) {
				this.tweetStatus.remove(tweetStatus.size() - 1);
				super.removeRow(tweetStatus.size());
			}
			// TODO: check用 あとでここの部分を検討 たまにテーブルサイズとlistのサイズが違うことがある
			if (this.tweetStatus.size() != super.getRowCount()) {
				System.err
						.println("Tweet Table List Size and Table Size are different.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
		//return true;
	}

	/**
	 * 時間情報を更新
	 */
	public void refreshTime() {
		// 現在の時間を取得
		Date currentTime = new Date();
		String info = "";
		// Rowの数
		int numOfRow = super.getRowCount();

		for (int i = 0; i < numOfRow; i++) {
			Status st = getTweetStatus(i);
			// 現在の時間とつぶやかれた時間との差を求める
			// TODO:ここの処理をあとで変える
			// getは遅い
			long diffTime = differenceTime(currentTime, st.getCreatedAt());

			// 時間情報を更新したか
			boolean updateTime = true;
			String timeInfo = "";
			if (diffTime <= 60 * 1000) {
				// １分以内の場合
				timeInfo = "１分前";
			} else if (diffTime <= 1000 * 60 * 60) {
				// １時間以内の場合
				timeInfo = (diffTime / (1000 * 60)) + "分前";
			} else if (diffTime <= 60 * 1000 * 60 * 24) {
				// 24時間以内の場合
				timeInfo = (diffTime / (60 * 1000 * 60)) + "時間前";
			} else {
				updateTime = false;
			}

			if (updateTime) {
				info = timeInfo + "<br> " + st.getSource() + "から";
				super.setValueAt(info, i, 2);
			}
		}
	}

}