/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.gui.component;

import java.awt.Desktop;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import twitter.gui.action.TweetMainAction;

/**
 * hashtagをクリックしたときの動作
 * @author nishio
 */
public class TweetHashtagHyperlinkHandler implements HyperlinkListener {

    private TweetMainAction mainAction;

    public TweetHashtagHyperlinkHandler() {
        
    }

    /**
     * mainActionセット
     * @param mainAction
     */
    public void setMainAction(TweetMainAction mainAction) {
        this.mainAction = mainAction;
    }
    
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			// クリック時
			URL url = e.getURL();
            String host = url.getHost();

            //search.twitter.comかどうか
            if( host.equals(twitter.manage.TweetConfiguration.SEARCH_TWITTER_HOSTNAME) ) {
                String query = url.getQuery();

                String searchWord = null;
                if (query != null) {
                    try {
                        searchWord = URLDecoder.decode(query, twitter.manage.TweetConfiguration.CHARACTER_ENCODING);
                        //q=の部分を削除
                        if(searchWord.startsWith("q=") ) {
                            searchWord = searchWord.substring(2);
                        }
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(TweetHashtagHyperlinkHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //TODO:ここの更新周期はTLのものと同じとなっている．いつかサーチ専用の更新間隔をセットするようにする
                this.mainAction.actionAddNewSearchResultTab(searchWord, mainAction.getGetTimelinePeriod());
            }else {
                //普通のurl
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

}
