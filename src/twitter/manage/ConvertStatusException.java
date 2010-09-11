package twitter.manage;

/**
 * Status XML変換エラー
 * 
 * @author nishio
 * 
 */
public class ConvertStatusException extends Exception {

	public ConvertStatusException() {
		super();
	}

	public ConvertStatusException(String message) {
		super(message);
	}
}
