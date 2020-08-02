package ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.self.JournalListActivity;
import com.example.self.OpenActivity;
import com.example.self.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Journal;

import static androidx.core.content.ContextCompat.startActivity;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Journal> journalList;


    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_row,viewGroup,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        final Journal journal=journalList.get(position);
        final String imageUrl;

        holder.title.setText(journal.getTitle());
        holder.thought.setText(journal.getThought());
        holder.name.setText(journal.getUserName());
        imageUrl=journal.getImageUrl();

        // 1 hour ago ....
        // source:  https://medium.com/@shaktisinh/time-a-go-in-android-8bad8b171f87
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal
                .getTimeAdded()
                .getSeconds() *1000);

        holder.dateAdded.setText(timeAgo);
        //using picasso for image to set up
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.images3)
                .fit()
                .into(holder.image);

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // share image
                //Toast.makeText(ctx,"share is invoked ",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        "Hey there is a good initiative taken by DFS, Check it out by given Url : "+journal.getImageUrl());
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        "Thought : "+journal.getThought());
                context.startActivity(intent);
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(imageUrl));
                context.startActivity(intent);


            }
        });

        //hello


    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title,thought,dateAdded,name,newtext;
        public ImageView image;
        public ImageButton shareButton;

        String userId,username;

        public ViewHolder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            context=ctx;
            title=itemView.findViewById(R.id.journal_title_list);
            thought=itemView.findViewById(R.id.journal_thought_list);
            dateAdded=itemView.findViewById(R.id.journal_timestamp_list);
            image=itemView.findViewById(R.id.journal_image_list);
            name=itemView.findViewById(R.id.journal_row_username);
            shareButton=itemView.findViewById(R.id.journal_row_sharebutton);
            newtext=itemView.findViewById(R.id.new_text);



        }
    }
}
