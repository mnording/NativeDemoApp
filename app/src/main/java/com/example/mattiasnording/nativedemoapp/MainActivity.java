package com.example.mattiasnording.nativedemoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.klarna.checkout.KlarnaCheckout;
import com.klarna.checkout.SignalListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    KlarnaCheckout mKlarnaCheckout;
    SignalListener klarnaListener;
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WebView.setWebContentsDebuggingEnabled(true);
        super.onCreate(savedInstanceState);
        mKlarnaCheckout = new KlarnaCheckout(this);
        setContentView(R.layout.activity_main);
        initKlarnaCheckout();
    }
    protected void initKlarnaCheckout() {
        klarnaListener = new SignalListener() {
            @Override
            public void onSignal(String eventName, JSONObject jsonObject) {
                Log.d("MainActivity",eventName);
                if (eventName.equals("complete")) {
                    try {
                        String url = jsonObject.getString("uri");
                        loadThankYou(url);

                    } catch (JSONException e) {
                        Log.e(e.getMessage(), e.toString());
                    }
                }
            }
        };
        getCheckoutFromUrl();
    }
    public void loadThankYou(String url) {
        Intent intent = new Intent(getApplicationContext(), KCOThankYouActivity.class);
        intent.putExtra(EXTRA_MESSAGE, url);
        MainActivity.this.startActivity(intent);
        return;
    }

    protected void getCheckoutFromUrl()
    {
        Ion.with(getApplicationContext())
                .load("http://aftersales-test-web1.internal.machines/mnording/inappBackend/index.php/snippet")
                //.load("http://mnording.com/klarnaKCO/")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        createKCOfromSnippet(result);
                    }
                });

    }
    protected void createKCOfromSnippet(String snippet)
    {
        mKlarnaCheckout.setSnippet(snippet);
        mKlarnaCheckout.setSignalListener(klarnaListener);
        View view = mKlarnaCheckout.getView();
        ViewGroup placeholder = ((ViewGroup) findViewById(R.id.kcoView));
        placeholder.addView(view);
    }

}
