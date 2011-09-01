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
import twitter.gui.action.TweetMainAction;

/**
 *
 * @author nishio
 *
 */
public class TweetCommentRenderer extends JEditorPane implements
		TableCellRenderer, MouseListener, MouseMotionListener {

	private int row = -1;
	private int col = -1;
	// あたらしく取得したセルが何行目までか
	private int newTableRow = -1;
	//main action
	private TweetMainAction mainAction = null;

	/**
	 *
	 */
	public TweetCommentRenderer(TweetMainAction mainAction) {
		super();
		this.mainAction = mainAction;
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


		//一行ずつTableの色を変更する
		//TODO: ここも後で色を変更できるようにする
		if( row % 2 == 0 ) {
			setBackground(new Color(240,240,255));
		}else {
			setBackground(Color.white);
		}

		// TODO: あとでここのカラーを変える
		// NewCell
		if( column >= 2 ) {
			if (this.newTableRow >= 0 && row < this.newTableRow) {
				setBackground(this.mainAction.getNewTableColor());
			} else {
				setBackground(Color.white);
			}
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
	 */
	public void updateNewCellRow(int row) {
		this.newTableRow = row;
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