package com.example.progettoium.ui.home.freeRepetitions;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.progettoium.R;
import com.example.progettoium.data.Courses;
import com.example.progettoium.data.FreeRepetitions;
import com.example.progettoium.data.Teachers;
import com.example.progettoium.data.User;
import com.example.progettoium.utils.NetworkViewModel;
import com.example.progettoium.utils.UserLiveData;
import com.google.android.material.snackbar.Snackbar;

public class FreeRepetitionsCustomViewAdapter extends RecyclerView.Adapter<FreeRepetitionsCustomViewAdapter.ViewHolder> {

    private List<FreeRepetitions> mData;
    private LayoutInflater mInflater;
    private Teachers jollyTeacher;
    private Courses selectedCourse;
    private Teachers selectedTeacher;
    private NetworkViewModel networkViewModel;
    private ProgressDialog bookARepetitionDialog;

    // data is passed into the constructor
    public FreeRepetitionsCustomViewAdapter(Context context, List<FreeRepetitions> data, NetworkViewModel networkViewModel) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.networkViewModel = networkViewModel;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.course_list_item, parent, false);
        return new ViewHolder(view);
    }

    public ProgressDialog getBookARepetitionDialog(){return this.bookARepetitionDialog;}

    // binds the data in the recycler view for each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FreeRepetitions repetition = mData.get(position);
        holder.lbl_CourseStartTime.setText(""+repetition.getStartTime());
        String [] temp = repetition.getStartTime().split(":");
        int endTime = Integer.parseInt(temp[0]) + 1;
        holder.lbl_CourseEndTime.setText(""+endTime+":00");

        ArrayList<Courses> courseList = repetition.getCoursesList();
        String jolly1 = mInflater.getContext().getResources().getString(R.string.select_course);
        Courses jollyCourse = new Courses(-1, String.valueOf(jolly1));
        courseList.add(0, jollyCourse);
        ArrayAdapter<Courses> courseSpinnerAdapter = new ArrayAdapter<Courses>(mInflater.getContext(), android.R.layout.simple_spinner_item, courseList);
        courseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner_CoursesDisplayer.setAdapter(courseSpinnerAdapter);
        holder.spinner_CoursesDisplayer.setSelection(courseSpinnerAdapter.getPosition(jollyCourse));

        String jolly2 = mInflater.getContext().getResources().getString(R.string.select_teacher);

        jollyTeacher = new Teachers(-1, jolly2, "");
        holder.spinner_TeachersDisplayer.setVisibility(View.GONE);

        holder.btn_bookLesson.setVisibility(View.GONE);
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
            spinner_CoursesDisplayer.setOnItemSelectedListener(onItemSeletctedCourseSpinner);
            spinner_TeachersDisplayer = itemView.findViewById(R.id.spinner_Teachers);
            spinner_TeachersDisplayer.setOnItemSelectedListener(onItemSeletctedTeacherSpinner);
            btn_bookLesson = itemView.findViewById(R.id.btn_bookLesson);
            btn_bookLesson.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            networkViewModel.bookARepetition(selectedCourse, selectedTeacher, (String) lbl_CourseStartTime.getText());
            bookARepetitionDialog = new ProgressDialog(mInflater.getContext());
            bookARepetitionDialog.setMessage("Connection...");
            bookARepetitionDialog.show();
        }

        AdapterView.OnItemSelectedListener onItemSeletctedCourseSpinner = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCourse = (Courses) parent.getSelectedItem();

                if(selectedCourse.getIDCourse() != -1){
                    ArrayList<Teachers> teachersList =  selectedCourse.getTeachersList();
                    if(!teachersList.contains(jollyTeacher))
                        teachersList.add(0, jollyTeacher);
                    ArrayAdapter<Teachers> teacherSpinnerAdapter = new ArrayAdapter<Teachers>(mInflater.getContext(), android.R.layout.simple_spinner_item, teachersList);
                    teacherSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_TeachersDisplayer.setAdapter(teacherSpinnerAdapter);
                    spinner_TeachersDisplayer.setSelection(teacherSpinnerAdapter.getPosition(jollyTeacher));

                    if(spinner_TeachersDisplayer.getVisibility() == View.GONE)
                        spinner_TeachersDisplayer.setVisibility(View.VISIBLE);
                }else{
                    if(spinner_TeachersDisplayer.getVisibility() == View.VISIBLE)
                        spinner_TeachersDisplayer.setVisibility(View.GONE);
                    if(btn_bookLesson.getVisibility() == View.VISIBLE)
                        btn_bookLesson.setVisibility(View.GONE);
                }

                //Toast.makeText(mInflater.getContext(), "Course ID: "+course.getIDCourse()+", Title : "+course.getTitle(), Toast.LENGTH_SHORT).show();

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // Auto-generated method stub for course spinner
            }
        };

        AdapterView.OnItemSelectedListener onItemSeletctedTeacherSpinner = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTeacher = (Teachers) parent.getSelectedItem();
                //Toast.makeText(mInflater.getContext(), "Teacher ID: "+teacher.getIDTeacher()+", Teacher : "+teacher.getFullName(), Toast.LENGTH_SHORT).show();
                if(selectedTeacher.getIDTeacher() != -1){
                    User loggedUser =  networkViewModel.getRegisteredUser().getValue();
                    if(loggedUser != null && loggedUser.getAccount() != null && loggedUser.getSurname() != null && loggedUser.getName() != null)
                        btn_bookLesson.setVisibility(View.VISIBLE);
                }else{
                    if(btn_bookLesson.getVisibility() == View.VISIBLE)
                        btn_bookLesson.setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // Auto-generated method stub for teacher spinner
            }
        };

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            // Auto-generated method stub for viewHoler
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            // Auto-generated method stub for viewHoler
        }
    }

    public void setData(List<FreeRepetitions> newData) {
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

        private final List<FreeRepetitions> oldPosts, newPosts;

        public PostDiffCallback(List<FreeRepetitions> oldPosts, List<FreeRepetitions> newPosts) {
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
            return ((oldPosts.get(oldItemPosition).getDay() == newPosts.get(newItemPosition).getDay()) && (oldPosts.get(oldItemPosition).getStartTime() == newPosts.get(newItemPosition).getStartTime()));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }
}