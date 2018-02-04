package com.example.joanabeleza.hobbytv.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.joanabeleza.hobbytv.Models.Review;
import com.example.joanabeleza.hobbytv.R;

import java.util.List;

/**
 * Project HobbyTV refactored by joanabeleza on 20/05/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mReviewAuthor;
        TextView mReviewTextView;

        MyViewHolder(View view) {
            super(view);

            mReviewAuthor = view.findViewById(R.id.review_author);
            mReviewTextView = view.findViewById(R.id.review_text);
        }
    }

    private List<Review> mReviewsList;

    public ReviewsAdapter(List<Review> reviews) {
        this.mReviewsList = reviews;
    }

    public void update(List<Review> data) {
        this.mReviewsList.clear();
        this.mReviewsList.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public ReviewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reviewView = inflater.inflate(R.layout.item_review, parent, false);

        return new ReviewsAdapter.MyViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.MyViewHolder holder, int position) {
        Review review = mReviewsList.get(position);

        TextView reviewAuthorTextView = holder.mReviewAuthor;
        TextView reviewTextView = holder.mReviewTextView;
        reviewAuthorTextView.setText(review.getAuthor());
        reviewTextView.setText(review.getReview());
    }


    @Override
    public int getItemCount() {
        return mReviewsList.size();
    }
}
