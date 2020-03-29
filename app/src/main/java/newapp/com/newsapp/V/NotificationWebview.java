package newapp.com.newsapp.V;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import newapp.com.newsapp.R;


public class NotificationWebview extends AppCompatActivity {

    private ProgressBar progressBar;
    private WebView webView;
    private static  String url = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        url = getIntent().getExtras().getString("url");


        progressBar = (ProgressBar) findViewById(R.id.qiblaprog);

        webView = (WebView) findViewById(R.id.webViewinActivity);

        webView.setWebViewClient(new MyClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().getLoadsImagesAutomatically();
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                progressBar.setProgress(newProgress);

                if (newProgress == 100) {

                    progressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }

            }
        });

        webView.loadUrl(url);

    }

    private class MyClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }
    }
}
