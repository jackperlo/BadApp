package com.example.progettoium.ui.booked.bookedHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettoium.R;
import com.example.progettoium.data.BookedRepetitions;

import java.util.List;

public class BookedHistoryCustomViewAdapter extends RecyclerView.Adapter<BookedHistoryCustomViewAdapter.ViewHolder> {
    private List<BookedRepetitions> mData;
    private LayoutInflater mIflater;

    public BookedHistoryCustomViewAdapter(Context context, List<BookedRepetitions> data) {
        this.mIflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mIflater.inflate(R.layout.course_history_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookedRepetitions course = mData.get(position);

        if(course != null) {
            holder.lbl_CourseStartTime.setText("" + course.getStartTime());
            String[] temp = course.getStartTime().split(":");
            int endTime = Integer.parseInt(temp[0]) + 1;
            holder.lbl_CourseEndTime.setText("" + endTime + ":00");

            holder.lbl_TeacherDisplayer.setText(String.valueOf(course.getIDTeacher()));
            holder.lbl_CourseDisplayer.setText(String.valueOf(course.getIDCourse()));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener {
        TextView lbl_CourseStartTime;
        TextView lbl_CourseEndTime;
        TextView lbl_CourseDisplayer;
        TextView lbl_TeacherDisplayer;

        ViewHolder(View itemView) {
            super(itemView);
            lbl_CourseStartTime = itemView.findViewById(R.id.lbl_CourseStartTime);
            lbl_CourseEndTime = itemView.findViewById(R.id.lbl_CourseEndTime);
            lbl_CourseDisplayer = itemView.findViewById(R.id.lbl_CourseDisplayer);
            lbl_TeacherDisplayer = itemView.findViewById(R.id.lbl_TeacherDisplayer);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(),"CLICK: "+getAdapterPosition(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = parent.getItemAtPosition(position).toString();
            //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            //Auto-generated method stub
        }
    }

    public void setData(List<BookedRepetitions> newData) {
        if(mData != null) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(mData, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);
            mData.clear();
            mData.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            mData = newData;
        }
    }

    class PostDiffCallback extends DiffUtil.Callback {
        private final List<BookedRepetitions> oldPosts, newPosts;

        public PostDiffCallback(List<BookedRepetitions> oldPosts, List<BookedRepetitions> newPosts){
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
            return ((oldPosts.get(oldItemPosition).getDay() == newPosts.get(newItemPosition).getDay()) && (oldPosts.get(oldItemPosition).getStartTime() == newPosts.get(newItemPosition).getStartTime()) && (oldPosts.get(oldItemPosition).getIDCourse() == newPosts.get(newItemPosition).getIDCourse()) && (oldPosts.get(oldItemPosition).getIDTeacher() == newPosts.get(newItemPosition).getIDTeacher()));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }
}


