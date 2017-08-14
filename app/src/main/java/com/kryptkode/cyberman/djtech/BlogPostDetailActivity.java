package com.kryptkode.cyberman.djtech;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;

import com.kryptkode.cyberman.djtech.models.BlogPosts;
import com.squareup.picasso.Picasso;

import static com.kryptkode.cyberman.djtech.DJTechHomeActivity.EXTRA;
import static com.kryptkode.cyberman.djtech.ui.fragments.HomeScreenFragment.POSTS;

public class BlogPostDetailActivity extends AppCompatActivity {
    private ImageView detailImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.detail_collapsing_toolbar);

        detailImageView = (ImageView)findViewById(R.id.detail_image_view);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getBundleExtra(EXTRA);
        BlogPosts blogPosts = bundle.getParcelable(POSTS);

        if (blogPosts != null) {
           // toolbar.setTitle(blogPosts.getTitle());
            String title = convertStringToTitleCase(blogPosts.getTitle());
            collapsingToolbarLayout.setTitle(title);


            Picasso.with(this).load(blogPosts.getPosterUrl())
                    .placeholder(R.drawable.loading).
                    error(R.drawable.no_image).into(detailImageView);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.detail_root, BlogPostDetailActivityFragment.getInstance(blogPosts));
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    private String convertStringToTitleCase(String input){
        String[] words = input.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0))).append(words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        return sb.toString();
    }

}
