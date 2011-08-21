package twitter.action.streaming;

import twitter4j.Status;

public interface TweetStreamingListener {
	public void update(Status status);
}
