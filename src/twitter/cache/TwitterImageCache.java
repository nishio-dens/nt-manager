package twitter.cache;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import twitter.manage.TweetConfiguration;

/**
 * イメージファイルのキャッシュを行うクラス
 * 
 * @author nishio
 * 
 */
public class TwitterImageCache {

	// イメージのキャッシュを保存するディレクトリ
	private static final String CACHE_DIRECTORY = TweetConfiguration.CACHE_DIRECTORY;
	// ディスク上に存在しているイメージ
	private Set<String> existImageList = null;
	//キャッシュ
	private static TwitterImageCache cache = new TwitterImageCache();

	/**
	 * コンストラクタ
	 */
	private TwitterImageCache() {
		existImageList = new TreeSet<String>();

		// キャッシュを保存するディレクトリを作成
		File logDir = new File("./" + CACHE_DIRECTORY);
		if (!logDir.exists()) {
			// ディレクトリが存在しないので作成する
			if (logDir.mkdir() == false) {
				System.err.println(CACHE_DIRECTORY + "ディレクトリを作成できませんでした");
			}
		}
		//HDD上にあるキャッシュが存在することを登録
		loadExistImageList();
	}
	
	/**
	 * ローカルに保存されているキャッシュデータ一覧を登録
	 */
	public void loadExistImageList() {
		String path = "./" + CACHE_DIRECTORY;
		File directory = new File( path );
		//ファイル一覧を取得
		File[] fileList = directory.listFiles();
		for( File f : fileList ) {
			String filename = f.getName();
			//ファイルが存在することを登録
			this.existImageList.add( filename );
		}
	}
	
	/**
	 * インスタンスを取得する
	 * @return
	 */
	public static TwitterImageCache getInstance() {
		return cache;
	}

	/**
	 * Imageを取得する
	 * 
	 * @param imageURL
	 * @return
	 */
	public ImageIcon getProfileImage(String imageURL) {
		// キャッシュ名取得
		String cacheFilename = getCacheFilenameFromURL(imageURL);
		if( cacheFilename == null ) {
			return null;
		}
		// イメージ
		ImageIcon icon = null;
		//保存するファイルのパス
		String path = "./" + CACHE_DIRECTORY + "/" + cacheFilename;

		// キャッシュが存在するかどうか
		if ( !existImageList.contains(cacheFilename)) {
			// ネットからイメージをダウンロードする
			URL url;
			try {
				url = new URL(imageURL);
				URLConnection connection = url.openConnection();
				InputStream inputStream = connection.getInputStream();
				// ファイルに保存
				File file = new File( path );
				FileOutputStream outputStream = new FileOutputStream(file, false);
				/*int data;
				while( (data = inputStream.read() ) != -1 ) {
					outputStream.write( data );
				}*/
				//データダウンロードを高速化するため、512バイトずつ取得
				byte buf[] = new byte[512];
				int len;
				while( (len = inputStream.read( buf )) != -1 ) {
					outputStream.write( buf, 0, len );
				}
				//ストリームを閉じる
				outputStream.close();
				inputStream.close();
				
				//HDD上に存在するイメージとして記録
				existImageList.add( cacheFilename );
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		
		if( existImageList.contains( cacheFilename ) ) {
			//HDDからイメージを取得
			icon = new ImageIcon( path );
		}/*else {
			//真っ黒のアイコン画像
			Image image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			g.setColor( Color.BLACK );
			g.fillRect(0, 0, 100, 100);
			g.dispose();
			icon = new ImageIcon( image );
		}*/
		
		return icon;
	}

	/**
	 * URLからキャッシュ上に保存されている画像データ名に変換する URLは、例えば
	 * http://a3.twimg.com/profile_images/870379120/nemui_tora_normal.png であったら、
	 * 870379120_nemui_tora_normal.png というキャッシュ名に変換する
	 * 
	 * @param url
	 * @return
	 */
	public String getCacheFilenameFromURL(String url) {
		String[] urlSplit = url.split("/");
		int len = urlSplit.length;
		String filename = null;
		// キャッシュ名の変換
		if (urlSplit.length >= 2) {
			filename = urlSplit[len - 2] + "_" + urlSplit[len - 1];
		}
		return filename;
	}
}
