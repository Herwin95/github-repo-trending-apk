package com.trending.gittrends.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trending.gittrends.R;
import com.trending.gittrends.data.model.RepoGit;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class RepoAdapter extends PagedListAdapter<RepoGit, RepoAdapter.CustomViewHolder> {

    private static DiffUtil.ItemCallback<RepoGit> DIFF_CALLBACK
            = new DiffUtil.ItemCallback<RepoGit>() {
        @Override
        public boolean areItemsTheSame(RepoGit oldItem, RepoGit newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(RepoGit oldItem, RepoGit newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    public RepoAdapter() {
        super(DIFF_CALLBACK);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle, mDesc, mLang, mVote;

        private ImageView mCircle;

        private CustomViewHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.repo_title);
            mDesc = itemView.findViewById(R.id.repo_desc);
            mLang = itemView.findViewById(R.id.language);
            mVote = itemView.findViewById(R.id.votes);
            mCircle = itemView.findViewById(R.id.lang_circle);
        }
    }

    @NonNull
    @Override
    public RepoAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new RepoAdapter.CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RepoAdapter.CustomViewHolder holder, final int pos) {

        RepoGit repoGit = getItem(pos);

        if(repoGit != null) {

            holder.mLang.setVisibility(View.INVISIBLE);
            holder.mCircle.setVisibility(View.INVISIBLE);
            holder.mTitle.setText(repoGit.getTitle());
            holder.mDesc.setText(repoGit.getDescription());

            /*Checking if language and language color is null*/

            if (!repoGit.getLanguage().equals("") && (!repoGit.getLanguage().equals("null"))) {
                holder.mLang.setVisibility(View.VISIBLE);
                holder.mCircle.setVisibility(View.VISIBLE);
                holder.mLang.setText(repoGit.getLanguage());
                Drawable background = holder.mCircle.getBackground();

                if (background instanceof ShapeDrawable) {
                    ((ShapeDrawable) background).getPaint().setColor(Color.parseColor(repoGit.getLanguageColor().trim()));
                } else if (background instanceof GradientDrawable) {
                    ((GradientDrawable) background).setColor(Color.parseColor(repoGit.getLanguageColor().trim()));
                } else if (background instanceof ColorDrawable) {
                    ((ColorDrawable) background).setColor(Color.parseColor(repoGit.getLanguageColor().trim()));
                }

            }
            holder.mVote.setText(String.valueOf(repoGit.getStars()));

        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
}
