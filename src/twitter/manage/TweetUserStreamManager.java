package twitter.manage;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserStreamAdapter;
import twitter4j.auth.AccessToken;

public class TweetUserStreamManager extends UserStreamAdapter {

	//streaming
	private TwitterStream twitterStream = null;

	/**
	 *
	 * @param consumerKey
	 * @param consumerSecret
	 * @param ac アクセストークン
	 */
	public TweetUserStreamManager(String consumerKey, String consumerSecret, AccessToken ac) {
		this.twitterStream = new TwitterStreamFactory().getInstance();
		this.twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
		this.twitterStream.setOAuthAccessToken(ac);
		this.twitterStream.addListener(this);
		this.twitterStream.user();
	}

	@Override
	public void onStatus(Status status) {
		if( status.isRetweetedByMe() ) {
			System.out.println( status.getUser().getScreenName() + " Retweet my message");
		}
		System.out.println("@" + status.getUser().getScreenName() + " - "
				+ status.getText());
	}

	@Override
	public void onException(Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onDirectMessage(DirectMessage directmessage) {
		System.out.println("Recipient: "
				+ directmessage.getRecipientScreenName() + " from "
				+ "Sender   : " + directmessage.getSenderScreenName()
				+ " text     : " + directmessage.getText() );
	}

	@Override
	public void onFavorite(User source, User target, Status favoritedStatus) {
		System.out.println(source.getScreenName() + " favorited "
				+ target.getScreenName() + "'s Status. StatusId: "
				+ favoritedStatus.getId() );
		System.out.println("FAV MESSAGE:" + favoritedStatus.getText());
	}

	@Override
	public void onFriendList(long[] friendIds) {
		String tempString = "FriendIds(Up to 2000): \n";
		int max = 2000;
		int count = friendIds.length;

		if (count > 0) {
			if (count < max) {
				max = count;
			}

			for (int i = 0; i < max; i++) {
				tempString += friendIds[i] + " ";
			}
			System.out.println(tempString);
		}
	}
}
