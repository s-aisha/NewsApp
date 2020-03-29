package newapp.com.newsapp.M;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import newapp.com.newsapp.R;
import newapp.com.newsapp.V.MainActivity;
import newapp.com.newsapp.V.WebViewFragment;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>  {


    private List<NewsPost.Datum> listitem;
    private Context ctx;
    private MainActivity newsScreen;


    public NewsAdapter(Context ctx, List<NewsPost.Datum> listitem, MainActivity newsScreen) {
        this.listitem = listitem;
        this.ctx = ctx;
        this.newsScreen = newsScreen;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.newspost,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final NewsPost.Datum newsList = listitem.get(position);

        Glide.with(ctx).load(newsList.urlToImage).into(holder.newsimage);

        holder.newsTitle.setText(newsList.title);

        holder.desc.setText(newsList.description);

        holder.publishdate.setText(newsList.publishedAt);

        holder.author.setText(newsList.author);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoWebView(newsList.url);
            }
        });

    }

    private void gotoWebView(String url) {

            Bundle bundle = new Bundle();
            bundle.putString("url",url);
            Fragment fragment = new WebViewFragment();
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = newsScreen.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.news,fragment).addToBackStack(null);
            fragmentTransaction.commit();
    }


    @Override
    public int getItemCount() {
        return listitem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView newsTitle,desc,publishdate,author;
        public ImageView newsimage;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            newsTitle = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.description);
            publishdate = itemView.findViewById(R.id.publishdate);
            author = itemView.findViewById(R.id.authorename);
            newsimage = itemView.findViewById(R.id.newsimage);
            cardView = itemView.findViewById(R.id.newsitem);

        }
    }


}
