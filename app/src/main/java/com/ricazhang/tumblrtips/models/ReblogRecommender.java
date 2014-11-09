package com.ricazhang.tumblrtips.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReblogRecommender {
    JumblrClient myClient;
    private String consumerKey = "wiQw74bDvGzD9Oe7G0Bpn4jx1GAJaBNTlXudvCYebNOnEYsvBw";
    private String consumerSecret = "pcRgMrZaYSWmSm1lZONeQs64MgWZ3o1C2CwFV8SIWYoTtXWKsh";

    /*
    public static void main(String args[]) {
        ReblogRecommender tester = new ReblogRecommender();
        System.out.println(tester.getRecommendationsFor("their-fire-retain"));
    }
    */

    public ReblogRecommender() {
        try{
            //Read in the credentials the client needs to get tumblr info
            FileReader fr = new FileReader("credentials.json");
            BufferedReader br = new BufferedReader(fr);
            StringBuilder json = new StringBuilder();
            try {
                while (br.ready()) { json.append(br.readLine()); }
            } finally {
                br.close();
            }

            // Parse the credentials
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(json.toString());

            // Create a client
            myClient = new JumblrClient(
                    obj.getAsJsonPrimitive("consumer_key").getAsString(),
                    obj.getAsJsonPrimitive("consumer_secret").getAsString()
            );

            // Give it a token
            myClient.setToken(
                    obj.getAsJsonPrimitive("oauth_token").getAsString(),
                    obj.getAsJsonPrimitive("oauth_token_secret").getAsString()
            );

        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //Authenticated blog finder for eliminating people you already follow - not functional
	/*public ReblogRecommender(String username, String password) {
		myClient = new JumblrClient(consumerKey, consumerSecret);
		Token myAccessToken =  myClient.getRequestBuilder().postXAuth(username, password);

		System.out.println(myAccessToken);

		myClient.setToken(myAccessToken);
	}
*/
    public List<Blog> getFollowers(String url) {
        List<Blog> followers = myClient.userFollowing();

        return followers;
    }

    public List<Blog> getRecommendationsFor(String url) {

        List<String> favoriteBlogs = getMostReblogged(url, 3);
        List<String> secondDegree = new ArrayList<String>();
        List<Blog> bestBlogs = new ArrayList<Blog>();


        for(String l2url : favoriteBlogs) {
            //System.out.println(b.getName());
            secondDegree.addAll(getMostReblogged(l2url, 3));
        }

        for(String s : secondDegree) {
            bestBlogs.add(myClient.blogInfo(s));
        }

        return bestBlogs;
    }

    List<String> getMostReblogged(String url, int num) {
        Map<String, Integer> blogsReblogged = new HashMap<String, Integer>();
        List<String> lastRoundFavorites = new ArrayList<String>();
        int count = 0;
        int blognumposts = myClient.blogInfo(url).getPostCount();
        int top_unchanged_count = 0;
        boolean top_blogs_found = false;


        while(!top_blogs_found) {
            Map<String, Object> opts = new HashMap<String, Object>();
            opts.put("reblog_info", true);
            opts.put("offset", 20*count);

            List<Post> mostRecentPosts = myClient.blogPosts(url, opts);

            for(Post p : mostRecentPosts)  {
                String owner = p.getRebloggedFromName();
                if(owner != null) {
                    if(blogsReblogged.get(owner) == null) {
                        blogsReblogged.put(owner, 1);
                    }
                    else {
                        blogsReblogged.put(owner, blogsReblogged.get(owner)+1);
                    }
                }
            }

            //Sort in descending order by number of blogs
            blogsReblogged = sortByComparator(blogsReblogged);

            //IF this is the first round, get a list of the top reblogged blogs
            if(count == 0) {
                //Find the top blogs from this round
                List<String> topnames = new ArrayList<String>(blogsReblogged.keySet());
                for(int i = 0; i < num; i++) {
                    lastRoundFavorites.add(topnames.get(i));
                }

            } //Otherwise, compare the top reblog blog from this large data set to the top rebloged blogs from before
            else {
                List<String> topnames = new ArrayList<String>(blogsReblogged.keySet());
                boolean allsame = true;
                for(int i = 0; i < num; i++) {
                    if(topnames.get(i) != lastRoundFavorites.get(i)) {
                        allsame = false;
                        break;
                    }
                }

                if (allsame) {
                    top_unchanged_count++;
                }
                else {
                    lastRoundFavorites = new ArrayList<String>();
                    for(int i = 0; i < num; i++) {
                        lastRoundFavorites.add(topnames.get(i));
                    }
                }
            }

            //If you are out of posts, or the top num blogs control 25% of your posts so far, or the top blogs haven't changed)
            if(sum(blogsReblogged.values())+20 >= blognumposts || (percentileIndex(blogsReblogged.values(), 0.25) <= num && sum(blogsReblogged.values()) > 60) || top_unchanged_count >= 4)
            {
                System.out.println("We've found the best blogs for: "+url);
                top_blogs_found = true;
            }
            count++;
        }


        System.out.println(lastRoundFavorites);
        return lastRoundFavorites;
    }



	/* UTILITY METHODS */

    public static void printMap(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println("[Key] : " + entry.getKey()
                    + " [Value] : " + entry.getValue());
        }
    }


    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    //Determine how many posts have been processed (sum of an integer array)
    public int sum(Collection<Integer> numarray) {
        int sum = 0;
        for(Integer i : numarray) {
            sum += i;
        }
        return sum;
    }

    //Determine how many of your favorite blogs it takes to control p percentile of your posts
    public int percentileIndex(Collection<Integer> collection, double percentile) {
        List<Integer> numlist = new ArrayList<Integer>(collection);

        int sum = sum(collection);
        int index = 0;
        double partialSum = 0;
        while(partialSum/sum < percentile) {
            partialSum += numlist.get(index);
            index++;
        }

        return index;
    }

}
