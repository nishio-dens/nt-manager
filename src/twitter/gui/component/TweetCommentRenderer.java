package twitter.gui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

/**
 * 
 * @author nishio
 * 
 */
public class TweetCommentRenderer extends JEditorPane implements
		TableCellRenderer, MouseListener, MouseMotionListener {

	private int row = -1;
	private int col = -1;
	// デフォルトのテーブルカラー
	private final Color currentTableColor = Color.WHITE;
	// あたらしく取得したTweetのセルを塗りつぶす色
	private Color newTableColor = new Color(224, 255, 255);
	// あたらしく取得したセルが何行目までか
	private int newTableRow = -1;

	/**
	 * 
	 */
	public TweetCommentRenderer() {
		super();
		// setLineWrap(true);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		// HTMLコードをそのまま表示できるようにする
		super.setEditable(false);
		this.setContentType("text/html");
	}

	// @Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		/*
		 * if (isSelected) { setForeground(table.getSelectionForeground());
		 * setBackground(table.getSelectionBackground()); } else {
		 * setForeground(table.getForeground());
		 * setBackground(table.getBackground()); }
		 */
		// TODO: あとでここのカラーを変える
		// NewCell
		if (this.newTableRow >= 0 && row < this.newTableRow) {
			setBackground(newTableColor);
		} else {
			setBackground(currentTableColor);
		}

		// フォントを変更
		setFont(table.getFont());
		try {
			// htmlフォント変更
			HTMLDocument doc = (HTMLDocument) getDocument();
			StyleSheet[] style = doc.getStyleSheet().getStyleSheets();
			for (int i = style.length - 1; i >= 0; i--) {
				Style body = style[i].getStyle("body");
				if (body != null) {
					StyleConstants.setFontFamily(body, table.getFont()
							.getFontName());
					StyleConstants.setFontSize(body, table.getFont().getSize());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setText((value == null) ? "" : value.toString());

		if (!table.isEditing() && this.row == row && this.col == column) {
			setText("<html><u><font color='blue'>" + value.toString());
		} else if (hasFocus) {
			setText("<html><font color='blue'>" + value.toString());
		} else {
			setText(value.toString());
		}
		return this;
	}

	/**
	 * 何行目までのセルを新しいセルとしてnewTableColorで塗りつぶすか
	 * 
	 * @param row
	 *            0以上の値で新しいセルとして指定した行を塗りつぶす
	 * @param newTableColor
	 *            新しい部分のテーブルカラー
	 */
	public void updateNewCellRow(int row, Color newTableColor) {
		this.newTableRow = row;
		this.newTableColor = newTableColor;
	}

	/**
	 * マウスがある位置のセルをrepaint
	 */
	public void mouseMoved(MouseEvent e) {
		//この部分は処理が重いのでカットすることとした
		/*JTable table = (JTable) e.getSource();
		Point pt = e.getPoint();
		row = table.rowAtPoint(pt);
		col = table.columnAtPoint(pt);
		if (row < 0 || col < 0) {
			row = -1;
			col = -1;
		}
		table.repaint();*/
	}

	/**
	 * マウスが存在するとき
	 */
	public void mouseExited(MouseEvent e) {
		JTable table = (JTable) e.getSource();
		row = -1;
		col = -1;
		table.repaint();
	}

	/**
	 * マウスをクリックした時の動作
	 */
	public void mouseClicked(MouseEvent e) {
		JTable table = (JTable) e.getSource();
		Point pt = e.getPoint();
		int crow = table.rowAtPoint(pt);
		int ccol = table.columnAtPoint(pt);
		// if(table.convertColumnIndexToModel(ccol) == 2)
		if (table.getColumnClass(ccol).equals(URL.class)) {
			URL url = (URL) table.getValueAt(crow, ccol);
			System.out.println(url);
			// ブラウザを起動
			try {
				Desktop.getDesktop().browse(url.toURI());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		mouseClicked(e);
	}

	public void mouseReleased(MouseEvent e) {
	}

}