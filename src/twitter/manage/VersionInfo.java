package twitter.manage;

/**
 * Version情報
 * @author nishio
 *
 */
public class VersionInfo{
	private String version = null;
	private String log = null;

	public VersionInfo(String version, String log) {
		this.version = version;
		this.log = log;
	}

	public String getVersion() {
		return this.version;
	}

	public String getLog() {
		return this.log;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setLog(String log) {
		this.log = log;
	}

	@Override
	public String toString() {
		return "VersionInfo [version=" + version + ", log=" + log + "]";
	}
}