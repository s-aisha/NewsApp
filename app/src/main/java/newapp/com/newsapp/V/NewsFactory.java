package newapp.com.newsapp.V;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import newapp.com.newsapp.VM.NewsPostViewModel;


public class NewsFactory implements ViewModelProvider.Factory {


    private Context application;
    private String country;
    private String category;

    public NewsFactory(Context application,String country, String category){
        this.application =  application;
        this.country = country;
        this.category = category;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsPostViewModel(application, country, category);
    }
}
