package twitter.log;

public class TweetDBObject {
	
	private long id;

	private String date;
	private long replyStatusID;
	private long replyUserID;
	private String text;
	private String created;
	private String description;
	private long userFavorite;
	private long followers;
	private long friend;
	private long userId;
	private String lang;
	private String location;
	private String name;
	private String profileBackgroundColor;
	private String profileBackgroundImageURL;
	private String profileImageURL;
	private String profileSidebarBorderColor;
	private String profileSidebarFillColor;
	private String profileTextColor;
	private String screenName;
	private long statusesCount;
	private String timeZone;
	private String url;
	private long utc;
	private String contributorsEnable;
	private String geoEnable;
	private String profileBackgroundTiled;
	private String isProtected;
	private String verified;
	private String source;
	private String favorite;
	private String retweet;
	private String truncated;
	
	public String getContributorsEnable() {
		return contributorsEnable;
	}
	public String getCreated() {
		return created;
	}
	public String getDate() {
		return date;
	}
	public String getFavorite() {
		return favorite;
	}
	public long getFollowers() {
		return followers;
	}
	public long getFriend() {
		return friend;
	}
	public String getGeoEnable() {
		return geoEnable;
	}
	public long getId() {
		return id;
	}
	public String getIsProtected() {
		return isProtected;
	}
	public String getLang() {
		return lang;
	}
	public String getLocation() {
		return location;
	}
	public String getName() {
		return name;
	}
	public String getProfileBackgroundColor() {
		return profileBackgroundColor;
	}
	public String getProfileBackgroundImageURL() {
		return profileBackgroundImageURL;
	}
	public String getProfileBackgroundTiled() {
		return profileBackgroundTiled;
	}
	public String getProfileImageURL() {
		return profileImageURL;
	}
	public String getProfileSidebarBorderColor() {
		return profileSidebarBorderColor;
	}
	public String getProfileSidebarFillColor() {
		return profileSidebarFillColor;
	}
	public String getProfileTextColor() {
		return profileTextColor;
	}
	public long getReplyStatusID() {
		return replyStatusID;
	}
	public long getReplyUserID() {
		return replyUserID;
	}
	public String getRetweet() {
		return retweet;
	}
	public String getScreenName() {
		return screenName;
	}
	public String getSource() {
		return source;
	}
	public long getStatusesCount() {
		return statusesCount;
	}
	public String getText() {
		return text;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public String getTruncated() {
		return truncated;
	}
	public String getUrl() {
		return url;
	}
	public long getUserFavorite() {
		return userFavorite;
	}
	public long getUserId() {
		return userId;
	}
	public long getUtc() {
		return utc;
	}
	public String getVerified() {
		return verified;
	}
	public void setContributorsEnable(String contributorsEnable) {
		this.contributorsEnable = contributorsEnable;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}
	public void setFollowers(long followers) {
		this.followers = followers;
	}
	public void setFriend(long friend) {
		this.friend = friend;
	}
	public void setGeoEnable(String geoEnable) {
		this.geoEnable = geoEnable;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setIsProtected(String isProtected) {
		this.isProtected = isProtected;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setProfileBackgroundColor(String profileBackgroundColor) {
		this.profileBackgroundColor = profileBackgroundColor;
	}
	public void setProfileBackgroundImageURL(String profileBackgroundImageURL) {
		this.profileBackgroundImageURL = profileBackgroundImageURL;
	}
	public void setProfileBackgroundTiled(String profileBackgroundTiled) {
		this.profileBackgroundTiled = profileBackgroundTiled;
	}
	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}
	public void setProfileSidebarBorderColor(String profileSidebarBorderColor) {
		this.profileSidebarBorderColor = profileSidebarBorderColor;
	}
	public void setProfileSidebarFillColor(String profileSidebarFillColor) {
		this.profileSidebarFillColor = profileSidebarFillColor;
	}
	public void setProfileTextColor(String profileTextColor) {
		this.profileTextColor = profileTextColor;
	}
	public void setReplyStatusID(long replyStatusID) {
		this.replyStatusID = replyStatusID;
	}
	public void setReplyUserID(long replyUserID) {
		this.replyUserID = replyUserID;
	}
	public void setRetweet(String retweet) {
		this.retweet = retweet;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public void setStatusesCount(long statusesCount) {
		this.statusesCount = statusesCount;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public void setTruncated(String truncated) {
		this.truncated = truncated;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setUserFavorite(long userFavorite) {
		this.userFavorite = userFavorite;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public void setUtc(long utc) {
		this.utc = utc;
	}
	public void setVerified(String verified) {
		this.verified = verified;
	}
	@Override
	public String toString() {
		return "TweetDBObject [contributorsEnable=" + contributorsEnable
				+ ", created=" + created + ", date=" + date + ", favorite="
				+ favorite + ", followers=" + followers 
				+  ", friend=" + friend + ", geoEnable=" + geoEnable
				+ ", id=" + id + ", isProtected=" + isProtected + ", lang="
				+ lang + ", location=" + location + ", name=" + name
				+ ", profileBackgroundColor=" + profileBackgroundColor
				+ ", profileBackgroundImageURL=" + profileBackgroundImageURL
				+ ", profileBackgroundTiled=" + profileBackgroundTiled
				+ ", profileImageURL=" + profileImageURL
				+ ", profileSidebarBorderColor=" + profileSidebarBorderColor
				+ ", profileSidebarFillColor=" + profileSidebarFillColor
				+ ", profileTextColor=" + profileTextColor + ", replyStatusID="
				+ replyStatusID + ", replyUserID=" + replyUserID + ", retweet="
				+ retweet + ", screenName=" + screenName + ", source=" + source
				+ ", statusesCount=" + statusesCount + ", text=" + text
				+ ", timeZone=" + timeZone + ", truncated=" + truncated
				+ ", url=" + url + ", userFavorite=" + userFavorite
				+ ", userId=" + userId + ", utc=" + utc + ", verified="
				+ verified + "]";
	}
	
	public TweetDBObject() {
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
}
