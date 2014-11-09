package com.ricazhang.tumblrtips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tumblr.jumblr.types.Blog;

import java.util.ArrayList;

/**
 * Created by Rica on 11/9/2014.
 */
public class ReblogRecommendationsAdapter extends ArrayAdapter<Blog> {
    public ReblogRecommendationsAdapter(Context context, ArrayList<Blog> blogs) {
        super(context, 0, blogs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Blog myBlog = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_blog_list, parent, false);
        }
        ListView blogsList = (ListView) convertView.findViewById(R.id.recommendedBlogsList);
        TextView blogTitle = (TextView) convertView.findViewById(R.id.blogListItem);
        //ImageView blogImage = (ImageView) convertView.findViewById(R.id.blogListImage);
        blogTitle.setText(myBlog.getTitle());
        //blogImage.setImageURI(myBlog.get);
        return convertView;
    }

}
