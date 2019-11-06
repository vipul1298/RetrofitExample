package android.example.retrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    JsonPlaceHolderApi jsonPlaceHolderApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.text);

        //Force gson to put null value for the particular key
        Gson gson = new GsonBuilder().serializeNulls().create();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request newRequest = originalRequest.newBuilder()
                                .header("Interceptor-Header","xyz")
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
         getPosts();
//           getComments();
//        createPost();
//        updatePost();
//        deletePost();
    }

    private void deletePost() {
        Call<Void> call =jsonPlaceHolderApi.deletePost(5);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                    textView.setText("Code: "+response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                 textView.setText(t.getMessage());
            }
        });
    }

    private void updatePost() {
        Post post = new Post(12,null,"New Text");
        Call<Post> call = jsonPlaceHolderApi.putPost(5,post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    textView.setText("Code: "+response.code());
                    return;
                }
                Post posts=response.body();
                String content="";
                content+="Code: "+response.code()+"\n";
                content+="ID: "+posts.getId()+"\n";
                content+="User Id: "+posts.getUserId()+"\n";
                content+="Title: "+posts.getTitle()+"\n";
                content+="Text: "+posts.getText()+"\n\n";

                textView.setText(content);

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }

    private void getPosts(){
        Map<String,String> parameters = new HashMap<>();
        parameters.put("userId","1");
        parameters.put("_sort","id");
        parameters.put("_order","desc");
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts(parameters);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(!response.isSuccessful()){
                    textView.setText("Code:"+response.code());
                    return;
                }
                List<Post> posts = response.body();
                for(Post post: posts){
                    String content="";
                    content+="ID: "+post.getId()+"\n";
                    content+="User Id: "+post.getUserId()+"\n";
                    content+="Title: "+post.getTitle()+"\n";
                    content+="Text: "+post.getText()+"\n\n";

                    textView.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }
    private void getComments(){
        Call<List<Comment>> call =jsonPlaceHolderApi.getComments();
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(!response.isSuccessful()){
                    textView.setText("Code:"+response.code());
                    return;
                }
                List<Comment> comments =response.body();
                for(Comment comment:comments){
                    String content="";
                    content+="ID: "+comment.getId()+"\n";
                    content+="Post Id: "+comment.getPostId()+"\n";
                    content+="Name: "+comment.getName()+"\n";
                    content+="Email: "+comment.getEmail()+"\n";
                    content+="Text: "+comment.getText()+"\n\n";

                    textView.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
               textView.setText(t.getMessage());
            }
        });
    }
    private void createPost(){
        Post post = new Post(23,"New title","New text");

         Map<String,String> fields =new HashMap<>();
         fields.put("userId","25");
         fields.put("title","New title");

        Call<Post> call = jsonPlaceHolderApi.createPost(fields);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
              if(!response.isSuccessful()){
                  textView.setText("Code:"+response.code());
                  return;
              }
              Post post1 = response.body();

                String content="";
                content+="Code: "+response.code()+"\n";
                content+="ID: "+post1.getId()+"\n";
                content+="User Id: "+post1.getUserId()+"\n";
                content+="Title: "+post1.getTitle()+"\n";
                content+="Text: "+post1.getText()+"\n\n";

                textView.setText(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                   textView.setText(t.getMessage());
            }
        });
    }

}
