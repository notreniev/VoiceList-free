package br.com.evknz.vol.free;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class PlayStoreActivity extends Activity {

    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_store);

        wv = (WebView) findViewById(R.id.wv_content);
        wv.setWebChromeClient(new WebChromeClient() {

        });

        //wv.setWebChromeClient(new MyWebChromeClient(wv, url));
        wv.setWebViewClient(new MyWebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);

        wv.loadUrl("http://play.google.com/store/apps/details?id=br.com.fiftytwoweeks");
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
            } else if (url.startsWith("http:") || url.startsWith("https:")) {
                if (url.endsWith(".pdf")) {
                    Log.d("ITEM", url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }

            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
            wv.goBack();
            return super.onKeyDown(keyCode, event);
        } else {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
