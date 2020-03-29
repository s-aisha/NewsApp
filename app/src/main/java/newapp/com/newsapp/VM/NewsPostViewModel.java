package newapp.com.newsapp.VM;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;
import newapp.com.newsapp.M.NewsPost;
import newapp.com.newsapp.M.RetroClient;
import newapp.com.newsapp.M.RetroInterface;
import newapp.com.newsapp.M.RetrofitResponseListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsPostViewModel extends ViewModel {


    private Context application;
    private RetroInterface apiInterface;
    private String country,preCountry = "1";
    private NewsPost newsList;
    private String category, preCategory = "1";

    public NewsPostViewModel(Context application, String country, String  category){
        this.application = application;
        this.country = country;
        this.category = category;

    }

    private RetroInterface getApiInterface(){
        if(apiInterface == null){
            apiInterface = RetroClient.getClient().create(RetroInterface.class);
            return apiInterface;
        }
        else return apiInterface;
    }

    public void getNewsList(final RetrofitResponseListener retrofitResponseListener) {

        if(newsList == null | !(preCountry.equals(country)) | !(preCategory.equals(category))){

            preCountry = country;
            preCategory = category;

            Call<NewsPost> call = getApiInterface().getNewsList(country,category,"67c6296f19c2443d861fd6a878dd3548");
            call.enqueue(new Callback<NewsPost>() {
                @Override
                public void onResponse(Call<NewsPost> call, Response<NewsPost> response) {

                    if(response.isSuccessful()){
                        newsList = response.body();
                        retrofitResponseListener.onSuccess(newsList);
                    }
                    else {

                        retrofitResponseListener.onFailure();
                    }
                }

                @Override
                public void onFailure(Call<NewsPost> call, Throwable t) {
                    call.cancel();
                    Toast.makeText(application,""+t,Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {
            retrofitResponseListener.onSuccess(newsList);
        }
    }


}
