/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.gui.component;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import twitter.util.HTMLEncode;
import twitter4j.UserList;

/**
 * リスト情報一覧のテーブルモデル
 * @author nishio
 */
public class TweetListTableModel extends DefaultTableModel {

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
        new ColumnContext("UserIcon", ImageIcon.class, false),
        new ColumnContext("ListName", String.class, false),
        new ColumnContext("ListUser", String.class, false),
        new ColumnContext("Description", String.class, false),
    };

	// 取得した情報を蓄えておく
	private List<UserList> listInfo = null;

	/**
     *
     */
	public TweetListTableModel() {
		super();
		listInfo = new ArrayList<UserList>();
	}

	/**
	 * 取得していた情報をすべて削除する
	 */
	public void clearStatus() {
		listInfo.clear();
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
	 * 指定した行のリスト情報を取得
	 *
	 * @param row
	 * @return
	 */
	public UserList getTweetStatus(int row) {
        UserList s = null;
        try {
            s = listInfo.get(row);
        }catch(Exception e) {
            e.printStackTrace();
        }
		return s;
	}

	/**
	 * テーブルに追加
	 *
	 * @param t
	 */
	public void insertTweet(UserList t) {
        Object[] obj = {
            new ImageIcon(t.getUser().getProfileImageURL()),
            new String(t.getName()),
            new String(t.getUser().getScreenName()),
            new String(t.getDescription())
        };

		try {
			super.insertRow(0, obj);
			listInfo.add(0, t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * テーブルに存在する要素数
	 *
	 * @return
	 */
	public int getTweetTableSize() {
		return this.listInfo.size();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
		//return true;
	}
}
