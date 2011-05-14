package twitter.manage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * URLをbitlyに変換するクラス
 * @author nishio
 *
 */
public class URLBitlyConverter {

	//bitlyのURL
	private static final String BITLY_URL = "http://bit.ly/";
	//bitlyのAPI URL
	private static final String BITLY_SHORTEN_API_URL = "http://api.bit.ly/shorten?";
	//bitlyログイン
	private static final String BITLY_LOGIN = "login";
	//bitly longurl
	private static final String BITLY_LONGURL = "longUrl";
	//bitly format
	private static final String BITLY_FORMAT = "format";
	//bitly api
	private static final String BITLY_APIKEY = "apiKey";
	//bitly format text
	private static final String BITLY_FORMAT_TEXT = "txt";
	//timeout time
	private static final int timeout = 5 * 1000; //5 seconds
	
	//account(あんまり情報が外部に漏れると良くない)
	//ユーザ名
	private static final String BITLY_USER_ACCOUNT = "nishiodens"; //これはいつか変えないとね
	//API key
	private static final String BITLY_USER_APIKEY = "R_684abae6ae215105939c8b79effa1077";
	
	/**
	 * URLをbitlyに変換
	 * @param url
	 * @return
	 */
	public static String convertUrlToBitly(String url) {
		String convertUrl = null;
		//urlがhttpで始まるかどうか
		if( url.startsWith("http://") || url.startsWith("https://") ) {
			String requestURL = new String( BITLY_SHORTEN_API_URL + BITLY_APIKEY + "=" + BITLY_USER_APIKEY);
	System.out.println(" request :" + requestURL );		
			requestURL = addURLAddressParameter(requestURL, BITLY_LOGIN, BITLY_USER_ACCOUNT );
			requestURL = addURLAddressParameter(requestURL, BITLY_FORMAT, BITLY_FORMAT_TEXT );
			requestURL = addURLAddressParameter(requestURL, BITLY_LONGURL, url );
			System.out.println(" request :" + requestURL );		
			convertUrl = getResultFromWeb( requestURL );
		}
		return convertUrl;
	}
	
	/**
	 * インターネット上からデータを取得する
	 * @param target
	 * @return
	 */
	private static String getResultFromWeb(String target) {
		String result = null;
		try {
			URL url = new URL( target );
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout( timeout );
			connection.setRequestMethod("GET");
			connection.connect();
			
			if( connection.getResponseCode() == 200 ) {
				//200 OKが返ってきた場合のみ値を返す
				BufferedReader reader = new BufferedReader(
						new InputStreamReader( connection.getInputStream() ));
				StringBuffer buf = new StringBuffer("");
				String line;
				while( (line = reader.readLine()) != null ) {
					buf.append(line + "\n");
				}
				result = buf.toString();
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * URLにパラメータ追加
	 * @param url
	 * @param key
	 * @param value
	 * @return
	 */
	private static String addURLAddressParameter(String url, String key, String value) {
		return url + "&" + key + "=" + value;
	}
}
