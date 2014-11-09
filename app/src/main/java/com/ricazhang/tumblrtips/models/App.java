package com.ricazhang.tumblrtips.models;

import com.tumblr.jumblr.types.Blog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Example usage of Jumblr
 * @author jc
 */
public class App {

    public static void main(String[] args) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {

        ReblogRecommender myRecommender = new ReblogRecommender();

        List<Blog> followingBlogs = myRecommender.getRecommendationsFor("their-fire-retain");
        for(Blog blog : followingBlogs) {
            System.out.println(blog.getTitle() + "\n");
        }


    }

}
