/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter.gui.component;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import twitter.cache.TwitterImageCache;
import twitter4j.User;

/**
 *
 * @author nishio
 */
public class UserTableModel extends DefaultTableModel {

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
        new ColumnContext("UserName", String.class, false),
        new ColumnContext("ScreenName", String.class, false)
    };
    // 取得した情報を蓄えておく
    private List<User> listInfo = null;

    /**
     *
     */
    public UserTableModel() {
        super();
        listInfo = new ArrayList<User>();
    }

    /**
     * 取得していた情報をすべて削除する
     */
    public void clearStatus() {
        listInfo.clear();
        super.setRowCount(0);
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
    public User getUserList(int row) {
        User s = null;
        try {
            s = listInfo.get(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * テーブルに追加
     *
     * @param t
     */
    public void insertUserList(User t) {
        //イメージデータをキャッシュから取得
        TwitterImageCache imageCache = TwitterImageCache.getInstance();

        Object[] obj = {
            imageCache.getProfileImage( t.getProfileImageURL().toString() ),
            new String( t.getName() ),
            new String( t.getScreenName() )
        };

        try {
            super.addRow(obj);
            listInfo.add(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * テーブルに追加
     *
     * @param t
     */
    public void insertUserList(List<User> list) {
        //イメージデータをキャッシュから取得
        TwitterImageCache imageCache = TwitterImageCache.getInstance();

        //イメージだけあらかじめ取得しておく
        ImageIcon[] icons = new ImageIcon[ list.size() ];
        int i = 0;
        for (User t : list) {
            icons[i] = imageCache.getProfileImage(t.getProfileImageURL().toString());
            i++;
        }

        int j=0;
        for (User t : list) {
            //キャッシュを使ってイメージを取得
            Object[] obj = {
                icons[j],
                new String(t.getName()),
                new String(t.getScreenName())
            };

            try {
                super.addRow(obj);
                listInfo.add(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            j++;
        }

    }

    /**
     * テーブルに存在する要素数
     *
     * @return
     */
    public int getTableSize() {
        return this.listInfo.size();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
        //return true;
    }
}
