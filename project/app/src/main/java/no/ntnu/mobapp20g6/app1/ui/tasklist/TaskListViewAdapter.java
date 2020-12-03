package no.ntnu.mobapp20g6.app1.ui.tasklist;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import no.ntnu.mobapp20g6.app1.R;
import no.ntnu.mobapp20g6.app1.data.RestService;
import no.ntnu.mobapp20g6.app1.data.api.PictureApi;
import no.ntnu.mobapp20g6.app1.data.model.Task;

/**
 * @author TrymV
 */
public class TaskListViewAdapter extends RecyclerView.Adapter<TaskListViewAdapter.ViewHolder> {
    private List<Task> taskList;
    private final RecyclerViewClickListener listener;
    private final Picasso picasso;

    public TaskListViewAdapter(List<Task> taskList, Picasso picasso, RecyclerViewClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
        this.picasso = picasso;
    }

    @NonNull
    @Override
    public TaskListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowElement = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(rowElement);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TaskListViewAdapter.ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.participantCount.setText(task.getParticipantCount() + " / " + task.getParticipantLimit());
        if(task.getPicture() != null && !task.getPicture().getId().isEmpty()) {
            System.out.println("Loading picture for task: " + task.getTitle());
            picasso.load(RestService.DOMAIN + PictureApi.PREFIX + "getimage?name=" + task.getPicture().getId() + "&width=" + "480").into(holder.imageView);
        } else {
            holder.imageView.setImageResource(android.R.drawable.ic_menu_crop);
        }
        holder.bind(task, listener);
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(taskList != null) {
            return taskList.size();
        } else {
            return 0;
        }
    }

    public interface RecyclerViewClickListener {
        void onClickTask(Task t);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView participantCount;
        ImageView imageView;

        public ViewHolder(@NonNull View taskView) {
            super(taskView);
            title = taskView.findViewById(R.id.task_item_title);
            participantCount = taskView.findViewById(R.id.task_item_participant_count);
            imageView = taskView.findViewById(R.id.task_item_picture);
        }

        public void bind(final  Task task, final RecyclerViewClickListener listener) {
            itemView.setOnClickListener(view -> listener.onClickTask(task));
        }
    }
}
