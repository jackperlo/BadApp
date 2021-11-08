package com.example.progettoium.ui.home.bookedRepetitions;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.progettoium.R;
import com.example.progettoium.data.BookedRepetitions;

public class BookedRepetitionsCustomViewAdapter extends RecyclerView.Adapter<BookedRepetitionsCustomViewAdapter.ViewHolder> {

    private List<BookedRepetitions> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public BookedRepetitionsCustomViewAdapter(Context context, List<BookedRepetitions> data) {
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
    //TODO: PER OGNI ROW METTO TUTTO, ESCLUDO LE ROW CHE SONO NEI LIVEDATA E QUINDI SONO GIA PRENOTATE
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookedRepetitions course = mData.get(position);
        holder.lbl_CourseStartTime.setText(""+course.getStartTime());
        String [] temp = course.getStartTime().split(":");
        int endTime = Integer.parseInt(temp[0]) + 1;
        holder.lbl_CourseEndTime.setText(""+endTime+":00");

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Item 1");
        categories.add("Item 2");
        categories.add("Item 3");
        categories.add("Item 4");
        categories.add("Item 5");
        categories.add("Item 6");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mInflater.getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner_CoursesDisplayer.setAdapter(dataAdapter);
        holder.spinner_TeachersDisplayer.setAdapter(dataAdapter);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener {
        TextView lbl_CourseStartTime;
        TextView lbl_CourseEndTime;
        Spinner spinner_CoursesDisplayer;
        Spinner spinner_TeachersDisplayer;
        Button btn_bookLesson;

        ViewHolder(View itemView) {
            super(itemView);
            lbl_CourseStartTime = itemView.findViewById(R.id.lbl_CourseStartTime);
            lbl_CourseEndTime = itemView.findViewById(R.id.lbl_CourseEndTime);
            spinner_CoursesDisplayer = itemView.findViewById(R.id.spinner_Courses);
            spinner_CoursesDisplayer.setOnItemSelectedListener(this);
            spinner_TeachersDisplayer = itemView.findViewById(R.id.spinner_Teachers);
            spinner_TeachersDisplayer.setOnItemSelectedListener(this);
            btn_bookLesson = itemView.findViewById(R.id.btn_bookLesson);
            btn_bookLesson.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(),"CLICK: "+getAdapterPosition(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    public void setData(List<BookedRepetitions> newData) {

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

        private final List<BookedRepetitions> oldPosts, newPosts;

        public PostDiffCallback(List<BookedRepetitions> oldPosts, List<BookedRepetitions> newPosts) {
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