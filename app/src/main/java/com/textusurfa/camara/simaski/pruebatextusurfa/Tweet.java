package com.textusurfa.camara.simaski.pruebatextusurfa;

public class Tweet {
	String tweetBy;
	String tweet;
	String tweetName;

	public Tweet(String tweetBy, String tweet, String tweetName) {
		this.tweetBy = tweetBy;
		this.tweet = tweet;
		this.tweetName = tweetName;
	}

	public String getTweetBy() {
		return tweetBy;
	}

	public void setTweetBy(String tweetBy) {
		this.tweetBy = tweetBy;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getTweetName() {
		return tweetName;
	}

	public void setTweetName(String tweetName) {
		this.tweetName = tweetName;
	}

}
