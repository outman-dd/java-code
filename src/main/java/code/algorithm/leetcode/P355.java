package code.algorithm.leetcode;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 〈设计twitter〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/19
 */
public class P355 {

    static class Twitter {

        static AtomicLong tsGen = new AtomicLong();

        static class Tweet {

            int id;

            Long timestamp;

            public Tweet(int id) {
                this(id, tsGen.incrementAndGet());
            }

            public Tweet(int id, long timestamp) {
                this.id = id;
                this.timestamp = timestamp;
            }
        }

        /**
         * followerId => followeeIds
         */
        private Map<Integer, List<Integer>> followMap = new HashMap();

        /**
         * followeeId => fanIds
         */
        private Map<Integer, List<Integer>> fanMap = new HashMap<>();

        private Map<Integer, List<Tweet>> tweetMap = new HashMap<>();

        private static int N = 10;

        /**
         * Initialize your data structure here.
         */
        public Twitter() {

        }

        /**
         * Compose a new tweet.
         */
        public void postTweet(int userId, int tweetId) {
            List<Tweet> tweets = tweetMap.get(userId);
            if (tweets == null) {
                tweets = new ArrayList();
                tweets.add(new Tweet(tweetId));
                tweetMap.put(userId, tweets);
            } else {
                tweets.add(new Tweet(tweetId));
            }
        }

        /**
         * Retrieve the 10 most recent tweet ids in the user's news feed. Each item in the news feed must be posted by users who the user followed or by the user herself. Tweets must be ordered from most recent to least recent.
         */
        public List<Integer> getNewsFeed(int userId) {
            //按更新时间从大到小排序
            Set<Tweet> topTweets = new TreeSet<Tweet>((o1, o2) -> o2.timestamp.compareTo(o1.timestamp));

            //加入自己的tweet
            topTweets.addAll(getTopN(userId, N));

            //加入已关注人的tweet
            List<Integer> followeeIds = followMap.get(userId);
            if (followeeIds != null) {
                for (Integer followeeId : followeeIds) {
                    topTweets.addAll(getTopN(followeeId, N));
                }
            }

            //取前10条
            int n = topTweets.size() >= N ? N : topTweets.size();
            List<Integer> top10Tweet = new ArrayList<>(16);

            Iterator<Tweet> ite = topTweets.iterator();
            while (n > 0) {
                top10Tweet.add(ite.next().id);
                n--;
            }
            return top10Tweet;
        }

        private List<Tweet> getTopN(int userId, int n) {
            List<Tweet> list = tweetMap.get(userId);
            if (list != null) {
                int from = list.size() - n > 0 ? list.size() - n : 0;
                return list.subList(from, list.size());
            }
            return Collections.emptyList();
        }

        /**
         * Follower follows a followee. If the operation is invalid, it should be a no-op.
         */
        public void follow(int followerId, int followeeId) {
            //查询已关注
            List<Integer> followeeIds = followMap.get(followerId);
            if (followeeIds == null) {
                followeeIds = new ArrayList(4);
                followeeIds.add(followeeId);
                followMap.put(followerId, followeeIds);
            } else {
                followeeIds.add(followeeId);
            }
            //查询粉丝
            List<Integer> fanIds = fanMap.get(followeeId);
            if (fanIds == null) {
                fanIds = new ArrayList(4);
                fanIds.add(followerId);
                fanMap.put(followeeId, fanIds);
            } else {
                fanIds.add(followerId);
            }
        }

        /**
         * Follower unfollows a followee. If the operation is invalid, it should be a no-op.
         */
        public void unfollow(int followerId, int followeeId) {
            //查询已关注
            List<Integer> followeeIds = followMap.get(followerId);
            if (followeeIds == null) {
                return;
            }
            Iterator<Integer> ite = followeeIds.iterator();
            while (ite.hasNext()){
                if(ite.next().equals(followeeId)){
                    ite.remove();
                    break;
                }
            }

            //查询粉丝
            List<Integer> fanIds = fanMap.get(followeeId);
            if (fanIds == null) {
                return;
            }
            Iterator<Integer> fanIte = fanIds.iterator();
            while (fanIte.hasNext()){
                if(fanIte.next().equals(followerId)){
                    fanIte.remove();
                    break;
                }
            }
        }

    }

    public static void main(String[] args) {
        Twitter twitter = new Twitter();

        twitter.postTweet(1, 4);
        twitter.postTweet(2, 5);

        twitter.unfollow(1,2);
        twitter.follow(1,2);

        twitter.getNewsFeed(1);

    }

}
