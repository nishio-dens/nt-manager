package twitter.gui.component;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

/**
 * Tweetボックスの右クリックに対応
 * @author nishio
 *
 */
public class TweetTextFieldPopupMenu extends JPopupMenu {
	private JEditorPane field = null;
	private final Action cutAction = new DefaultEditorKit.CutAction();
	private final Action copyAction = new DefaultEditorKit.CopyAction();
	private final Action pasteAction = new DefaultEditorKit.PasteAction();
	
	public TweetTextFieldPopupMenu(JEditorPane field) {
		super();
		this.field = field;
		
		cutAction.putValue(Action.NAME, "切り取り");
		copyAction.putValue(Action.NAME, "コピー");
		pasteAction.putValue(Action.NAME, "貼り付け");
		add(cutAction);
		add(copyAction);
		add(pasteAction);
		this.field.add( this );
	}
	
	/**
	 * 
	 */
	public void show(Component c, int x, int y) {
		JEditorPane field = (JEditorPane) c;
		boolean flg = field.getSelectedText() != null;
		cutAction.setEnabled(flg);
		copyAction.setEnabled(flg);
		super.show(c, x, y);
	}

}
