package com.university.unicornslayer.lab3;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ProxyMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_menu);
    }

    class ProxyDocLoader extends AsyncTask<Void, Void, Document> {
        static final String url = "http://spys.one/en/http-proxy-list/";

        @Override
        protected Document doInBackground(Void... voids) {
            try {
                String userAgent = getString(R.string.user_agent);
                return Jsoup.connect(url).userAgent(userAgent).validateTLSCertificates(false).get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
