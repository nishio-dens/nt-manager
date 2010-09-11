package twitter.gui.component;

import java.awt.Desktop;
import java.net.URL;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * 
 * @author nishio
 * 
 */
public class TweetHyperlinkHandler implements HyperlinkListener {

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			// クリック時
			URL url = e.getURL();
			// デフォルトのブラウザを使ってリンク先を表示
			Desktop dp = Desktop.getDesktop();
			try {
				dp.browse(url.toURI());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
