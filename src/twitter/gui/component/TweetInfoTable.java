package twitter.gui.component;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import twitter4j.Status;

/**
 * Tweet情報を表示するテーブル
 * 
 * @author nishio
 * 
 */
public class TweetInfoTable extends JTable {

	public TweetInfoTable() {
		super();
	}

	public TweetInfoTable(int numRows, int numColumns) {
		super(numRows, numColumns);

	}

	public TweetInfoTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);

	}

	public TweetInfoTable(TableModel dm, TableColumnModel cm,
			ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	public TweetInfoTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
	}

	public TweetInfoTable(TableModel dm) {
		super(dm);
	}

	public TweetInfoTable(Vector rowData, Vector columnNames) {
		super(rowData, columnNames);
	}

	/**
	 * ツールチップ表示
	 */
	@Override
	public String getToolTipText(MouseEvent e) {
		// 現在マウスはどのテーブルのどのセルをさしているか
		int row = rowAtPoint(e.getPoint());
		int column = columnAtPoint(e.getPoint());
		TableModel m = getModel();
		String retValue = null;

		if (m instanceof TweetTableModel) {
			Status s = ((TweetTableModel) m).getTweetStatus(row);
			// マウスが指しているコラムに応じて表示する情報を変更
			if (column == 0) {
				// ユーザの情報を表示
				/*
				 * retValue = "<html>Follow:" + s.getUser().getFollowersCount()
				 * + "</html>";
				 */

				// ユーザの場所を表示するか
				String location = "";
				if (s.getUser().getLocation() != null
						&& s.getUser().getLocation().length() > 0) {
					location = "Location:" + s.getUser().getLocation() + "<br>";
				}
				// ユーザのURLを表示するか
				String userURL = "";
				if (s.getUser().getURL() != null) {
					userURL = s.getUser().getURL() + "<br>";
				}
				retValue = "<html><b>" + s.getUser().getName() + "</b><br><br>"
						+ userURL + location + s.getUser().getFollowersCount()
						+ " followers<br>" + s.getUser().getFriendsCount()
						+ " following<br></html>";
			} else {
				// TweetTextを表示
				// retValue = "<html>" + HTMLEncode.encode(s.getText()) +
				// "<html>";
			}
		} else {
			retValue = "<html>" + m.getValueAt(row, 0) + "<br>"
					+ m.getValueAt(row, 1) + "</html>";
		}
		return retValue;
	}

}
