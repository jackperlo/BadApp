package com.example.progettoium.ui.booked.bookedHistory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettoium.R;
import com.example.progettoium.data.BookedRepetitions;
import com.example.progettoium.utils.NetworkViewModel;

import java.util.List;
import java.util.Locale;

public class BookedHistoryCustomViewAdapter extends RecyclerView.Adapter<BookedHistoryCustomViewAdapter.ViewHolder> {
    private List<BookedRepetitions> mData;
    private LayoutInflater mIflater;
    private NetworkViewModel networkViewModel;
    public ProgressDialog progressDialog;

    public BookedHistoryCustomViewAdapter(Context context, List<BookedRepetitions> data, NetworkViewModel networkViewModel) {
        this.mIflater = LayoutInflater.from(context);
        this.mData = data;
        this.networkViewModel = networkViewModel;
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
            holder.lbl_Day.setText(course.getDay());

            holder.lbl_TeacherDisplayer.setText(String.valueOf(course.getTitle()));
            holder.lbl_CourseDisplayer.setText(String.valueOf(course.getSurname() + " " + course.getName()));

            if(networkViewModel.getOnState().equals("Active")) {
                holder.btn_sustained.setVisibility(View.VISIBLE);
                holder.btn_cancel.setVisibility(View.VISIBLE);
            } else {
                holder.btn_sustained.setVisibility(View.INVISIBLE);
                holder.btn_cancel.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
        TextView lbl_CourseStartTime;
        TextView lbl_CourseEndTime;
        TextView lbl_CourseDisplayer;
        TextView lbl_TeacherDisplayer;
        TextView lbl_Day;
        Button btn_cancel;
        Button btn_sustained;

        ViewHolder(View itemView) {
            super(itemView);
            lbl_CourseStartTime = itemView.findViewById(R.id.lbl_CourseStartTime);
            lbl_CourseEndTime = itemView.findViewById(R.id.lbl_CourseEndTime);
            lbl_CourseDisplayer = itemView.findViewById(R.id.lbl_CourseDisplayer);
            lbl_TeacherDisplayer = itemView.findViewById(R.id.lbl_TeacherDisplayer);
            lbl_Day = itemView.findViewById(R.id.lbl_Day);
            btn_cancel = itemView.findViewById(R.id.btn_cancel);

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Attention!")
                            .setMessage("Are you sure you want to cancel this booking?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog = new ProgressDialog(mIflater.getContext());
                                    progressDialog.setMessage("Connection...");
                                    progressDialog.show();
                                    BookedRepetitions choose = mData.get(getAdapterPosition());
                                    networkViewModel.changeRepetitionState("Cancelled", choose.getDay(), choose.getStartTime(), choose.getIdCourse(), choose.getIdTeacher());
                                }
                            })
                            .setNegativeButton("CANCEL", null)
                            .show();
                }
            });

            btn_sustained = itemView.findViewById(R.id.btn_susteined);
            btn_sustained.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Attention!")
                            .setMessage("Are you sure you want to mark this lesson as sustained?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog = new ProgressDialog(mIflater.getContext());
                                    progressDialog.setMessage("Connection...");
                                    progressDialog.show();
                                    BookedRepetitions choose = mData.get(getAdapterPosition());
                                    networkViewModel.changeRepetitionState("Done", choose.getDay(), choose.getStartTime(), choose.getIdCourse(), choose.getIdTeacher());
                                }
                            })
                            .setNegativeButton("CANCEL", null)
                            .show();
                }
            });
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
            return ((oldPosts.get(oldItemPosition).getDay() == newPosts.get(newItemPosition).getDay()) && (oldPosts.get(oldItemPosition).getStartTime() == newPosts.get(newItemPosition).getStartTime()) && (oldPosts.get(oldItemPosition).getTitle() == newPosts.get(newItemPosition).getTitle()) && (oldPosts.get(oldItemPosition).getSurname() == newPosts.get(newItemPosition).getSurname()) && (oldPosts.get(oldItemPosition).getName() == newPosts.get(newItemPosition).getName()));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }
}


