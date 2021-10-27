package com.example.progettoium.ui.home.courses;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.progettoium.R;
import com.example.progettoium.data.CoursesTimeTable;

public class CoursesCustomViewAdapter extends RecyclerView.Adapter<CoursesCustomViewAdapter.ViewHolder> {

    private List<CoursesTimeTable> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public CoursesCustomViewAdapter(Context context, List<CoursesTimeTable> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.course_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CoursesTimeTable course = mData.get(position);
        holder.lbl_CourseStartTime.setText(course.getStartTime());
        holder.lbl_CourseEndTime.setText(course.getEndTime());
        holder.lbl_CourseDisplayer.setText(""+course.getCodCourse()); //E' UN INT
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView lbl_CourseStartTime;
        TextView lbl_CourseEndTime;
        TextView lbl_CourseDisplayer;
        Button btn_bookLesson;

        ViewHolder(View itemView) {
            super(itemView);
            lbl_CourseStartTime = itemView.findViewById(R.id.lbl_CourseStartTime);
            lbl_CourseEndTime = itemView.findViewById(R.id.lbl_CourseEndTime);
            lbl_CourseDisplayer = itemView.findViewById(R.id.lbl_CourseDisplayer);
            btn_bookLesson = itemView.findViewById(R.id.btn_bookLesson);
            btn_bookLesson.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(),"CLICK: "+getAdapterPosition(),Toast.LENGTH_LONG).show();
        }
    }

    public void setData(List<CoursesTimeTable> newData) {

        // Versione vecchia:  notifyDataSetChanged(); aggiorna TUTTA la lista
        if (mData != null) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(mData, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);
            mData.clear();
            mData.addAll(newData);
            diffResult.dispatchUpdatesTo(this);// richiama onBindviewHolder
        } else {
            // first initialization
            mData = newData;
        }
    }

    class PostDiffCallback extends DiffUtil.Callback {

        private final List<CoursesTimeTable> oldPosts, newPosts;

        public PostDiffCallback(List<CoursesTimeTable> oldPosts, List<CoursesTimeTable> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).getCodCourse() == newPosts.get(newItemPosition).getCodCourse();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }
}