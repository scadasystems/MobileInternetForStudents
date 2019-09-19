package com.example.mobileinternetforstudent.exampleJson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileinternetforstudent.MainActivity;
import com.example.mobileinternetforstudent.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JsonActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.post_recycler_view)
    RecyclerView postRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        ButterKnife.bind(this);

        // custom toolbar 사용
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // toolbar에 뒤로가기 버튼 생성

        // adater 연결
        final PostRecyclerView adapter = new PostRecyclerView();
        postRecyclerView.setAdapter(adapter);

        // Retrofit
        String baseUrl = "https://koreanjson.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Retrofit 인터페이스 연결
        KoreanJsonService service = retrofit.create(KoreanJsonService.class);
        service.listPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> posts = response.body();

                Log.d(TAG, "onResponse: " + posts);

                if (posts != null) {
                    adapter.setItems(posts);
                    adapter.notifyItemRangeInserted(0, posts.size());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });

    }

    /* RecyclerView Adapter */
    private static class PostRecyclerView extends RecyclerView.Adapter<PostRecyclerView.PostViewHolder> {

        private List<Post> mItems = new ArrayList<>();

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Post post = mItems.get(position);

            holder.titleTextView.setText(post.getTitle());
            holder.contentTextView.setText(post.getContent());
            holder.createTextView.setText(post.getCreatedAt());
            holder.updateTextView.setText(post.getUpdatedAt());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void setItems(List<Post> items) {
            mItems = items;
        }

        // item_post.xml 에 대해 ViewHolder
        public static class PostViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            TextView contentTextView;
            TextView createTextView;
            TextView updateTextView;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);
                // itemView의 id값은 item_post.xml 의 뷰들의 id 값이다.
                titleTextView = itemView.findViewById(R.id.title_text_view);
                contentTextView = itemView.findViewById(R.id.content_text_view);
                createTextView = itemView.findViewById(R.id.create_time_text_view);
                updateTextView = itemView.findViewById(R.id.updated_time_text_view);
            }
        }
    }

    /*  toolbar 뒤로가기 버튼 누를 시 메인으로 이동 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent_home = new Intent(this, MainActivity.class);
                startActivity(intent_home);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
