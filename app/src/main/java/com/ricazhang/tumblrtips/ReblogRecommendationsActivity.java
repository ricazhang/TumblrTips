package com.ricazhang.tumblrtips;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ricazhang.tumblrtips.models.ReblogRecommender;
import com.tumblr.jumblr.types.Blog;

import java.util.ArrayList;
import java.util.List;

public class ReblogRecommendationsActivity extends ActionBarActivity {
    private ReblogRecommender myReblogRecommender;
    private ListView myRecommendedBlogs;
    private ArrayList<Blog> recommendedBlogsData;
    private ReblogRecommendationsAdapter myReblogRecommendationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reblog_recommendations);
        myReblogRecommender = new ReblogRecommender();
        myRecommendedBlogs = (ListView) findViewById(R.id.recommendedBlogsList);
        recommendedBlogsData = new ArrayList<Blog>();
        myReblogRecommendationsAdapter = new ReblogRecommendationsAdapter(this, recommendedBlogsData);
        myRecommendedBlogs.setAdapter(myReblogRecommendationsAdapter);
        createGoButtonListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reblog_recommendations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createGoButtonListener() {
        Button go = (Button) findViewById(R.id.goButton);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("\nGo button clicked!\n");
                EditText chosenUsername = (EditText) findViewById(R.id.editText);
                String username = chosenUsername.getText().toString();
                System.out.println(username);
                getBlogData(username);
            }
        });
    }

    public void getBlogData(String url) {
        List<Blog> blogs = myReblogRecommender.getRecommendationsFor("ru-ij-ia");
        for (Blog b : blogs) {
            myReblogRecommendationsAdapter.add(b);
        }
    }
}
