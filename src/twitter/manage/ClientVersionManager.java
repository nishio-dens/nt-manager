package twitter.manage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;



/**
 * バージョン管理
 * @author nishio
 *
 */
public class ClientVersionManager {
	public static final String currentVersion = "0.61";
	public static final String nishioTweetManagerURL = "http://densan-labs.net/software/ntm/";
	private static final String characterSet = "UTF-8";
	private static final String latestInformationURL = "http://git.densan-labs.net/ntm/latest.txt";

	//xml tag
	private static final String versionTag = "version";
	private static final String logTag = "log";

	//for singleton
	private static ClientVersionManager versionManager = null;

	/**
	 * singleton
	 */
	private ClientVersionManager() {

	}

	/**
	 * インスタンス取得
	 * @return
	 */
	public static ClientVersionManager getInstance() {
		if( versionManager == null ) {
			versionManager = new ClientVersionManager();
		}
		return versionManager;
	}

	/**
	 * currentVersionを取得します。
	 * @return currentVersion
	 */
	public String getCurrentversion() {
	    return currentVersion;
	}

	/**
	 * 最新情報を取得
	 * @return
	 */
	private String getLatestInformation() {
		StringBuffer buf = new StringBuffer("");
		try {
			URL url = new URL(latestInformationURL);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader( conn.getInputStream(), characterSet));
			String line = null;
			while( (line = reader.readLine()) != null ) {
				buf.append(line + "\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * クライアントの最新バージョンの情報を取得する
	 * @return
	 */
	public VersionInfo getLatestVersionInfo() {
		String log = getLatestInformation();
		VersionInfo info = null;
		try {
			info = XMLToVersionInfo(log);
		} catch (SAXParseException e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * nishioTweetManagerURLを取得します。
	 * @return nishioTweetManagerURL
	 */
	public String getNishiotweetmanagerurl() {
	    return nishioTweetManagerURL;
	}

	/**
	 * 指定されたエレメントから子要素の内容を取得
	 *
	 * @param element
	 *            指定エレメント
	 * @param tagName
	 *            指定タグ名
	 * @return 取得した内容
	 */
	private static String getChildren(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		Element cElement = (Element) list.item(0);
		if (cElement == null || cElement.getFirstChild() == null) {
			return null;
		}
		return cElement.getFirstChild().getNodeValue();
	}

	/**
	 * latest info XMLからデータ取得
	 *
	 * @param xmlData
	 * @return
	 * @throws SAXParseException
	 */
	public VersionInfo XMLToVersionInfo(String xmlData)
			throws SAXParseException {
		if (xmlData == null) {
			return null;
		}
		try {
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			Document doc = null;
			// 入力ストリーム作成
			StringReader sr = new StringReader(xmlData);
			InputSource is = new InputSource(sr);

			doc = builder.parse(is);

			// ルート要素になっている子ノードを取得
			Element root = doc.getDocumentElement();
			String version = getChildren(root, versionTag);
			String log = getChildren(root, logTag);

			return new VersionInfo(version, log);
		} catch (SAXParseException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
