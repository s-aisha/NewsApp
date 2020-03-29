package newapp.com.newsapp.M;



public interface RetrofitResponseListener {

    void onFailure();

    void onSuccess(NewsPost body);
}
