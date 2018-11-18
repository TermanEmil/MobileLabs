package com.university.unicornslayer.lab3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    private final static String siteUrl = "https://www.moldcell.md/rom/sendsms";
    private final static String siteBaseUrl = "https://www.moldcell.md/";

    private final static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

    private ProgressDialog mProgressDialog;
    private Document mWebDocument;

    private TextInputEditText mPhoneNb;
    private TextInputEditText mAuthor;
    private TextInputEditText mMessage;
    private TextInputEditText mCaptchaInput;
    private AppCompatImageView mCaptchaImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneNb = findViewById(R.id.phone_nb_input);
        mAuthor = findViewById(R.id.author);
        mMessage = findViewById(R.id.message_content_input);
        mCaptchaInput = findViewById(R.id.captcha_input);
        mCaptchaImgView = findViewById(R.id.captcha_img);

        try {
            setTrustAllCerts();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.setProperty("https.proxyHost", "85.30.48.222");
        System.setProperty("https.proxyPort", "30228");

        loadWebDocument();
    }

    public void onSendMsg(View view) {
        if (!validateInput()) {
            return;
        }

        Map<String, String> requestParams = new HashMap<>();

        // Remove the first 0.
        String phone = mPhoneNb.getText().toString();
        phone = phone.substring(1);
        requestParams.put("phone", phone);

        requestParams.put("name", mAuthor.getText().toString());
        requestParams.put("message", mMessage.getText().toString());

        requestParams.put("captcha_sid", mWebDocument.select(".captcha > input[name=captcha_sid]").val());
        requestParams.put("captcha_token", mWebDocument.select(".captcha > input[name=captcha_token]").val());
        requestParams.put("captcha_response", mCaptchaInput.getText().toString());

        requestParams.put("conditions", "1");
        requestParams.put("op", "");
        requestParams.put("form_build_id", mWebDocument.select(".websms-form > input[name=form_build_id]").val());
        requestParams.put("form_id", "websms_main_form");

        MsgSendRequest task = new MsgSendRequest();
        task.execute(requestParams);
    }

    private void loadWebDocument() {
        mProgressDialog = ProgressDialog.show(
                this,
                "",
                "Loading. Please wait...",
                true);

        WebDocLoader task = new WebDocLoader();
        task.execute(siteUrl);
    }

    private void loadCaptcha() {
        CaptchaLoaderData taskData = new CaptchaLoaderData();
        taskData.doc = mWebDocument;
        taskData.baseUrl = siteBaseUrl;

        CaptchaLoader task = new CaptchaLoader();
        task.execute(taskData);
    }

    private void setTrustAllCerts() throws Exception
    {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType ) { }
                    public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType ) { }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance( "SSL" );
            sc.init( null, trustAllCerts, new java.security.SecureRandom() );
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(
                    new HostnameVerifier() {
                        public boolean verify(String urlHostName, SSLSession session) {
                            return true;
                        }
                    });
        }
        catch ( Exception e ) {
            //We can not recover from this exception.
            e.printStackTrace();
        }
    }

    public void resetCaptcha(View view) {
        loadWebDocument();
    }

    private boolean validateInput() {
        String exception = "";

        String phoneNbException = validatePhoneNb();
        if (phoneNbException != null) {
            exception += "- Phone number: " + phoneNbException + "\n";
        }

        String authorException = validateAuthor();
        if (phoneNbException != null) {
            exception += "- Author: " + authorException + "\n";
        }

        String msgException = validateMsg();
        if (msgException != null) {
            exception += "- Message: " + msgException + "\n";
        }

        String captchaException = validateCaptcha();
        if (captchaException != null) {
            exception += "- Captcha: " + captchaException + "\n";
        }

        if (exception.length() == 0)
            return true;

        new QuickWarning(this, exception);
        return false;
    }

    private String validatePhoneNb() {
        String input = mPhoneNb.getText().toString();
        if (!isNumeric(input)) {
            return "Invalid characters";
        }

        if (input.length() != 9) {
            return "Length must be 9";
        }

        return null;
    }

    private String validateAuthor() {
        String input = mAuthor.getText().toString();

        if (input.length() > 9) {
            return "Can't be longer than 9";
        }

        return null;
    }

    private String validateMsg() {
        String input = mMessage.getText().toString();

        if (input.length() > 140) {
            return "Too long";
        }

        return null;
    }

    private String validateCaptcha() {
        String input = mCaptchaInput.getText().toString();

        if (input.length() == 0) {
            return "Can't be empty";
        }

        if (input.length() > 10) {
            return "Too long";
        }

        return null;
    }


    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    class WebDocLoader extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            try {
                return Jsoup.connect(urls[0]).userAgent(userAgent).validateTLSCertificates(false).get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            if (document == null) {
                new QuickWarning(MainActivity.this, "Failed to load Moldcell");
                return;
            }

            mWebDocument = document;
            loadCaptcha();
        }
    }

    class CaptchaLoaderData {
        Document doc;
        String baseUrl;
    }

    class CaptchaLoader extends AsyncTask<CaptchaLoaderData, Void, Bitmap> {

        static final String captchaTitle = "Imagine CAPTCHA";

        @Override
        protected Bitmap doInBackground(CaptchaLoaderData... captchaLoaderDatas) {
            Document document = captchaLoaderDatas[0].doc;

            Element captchaDiv = document.getElementsByClass("captcha").first();
            Element captchaImg = captchaDiv.selectFirst(String.format("img[title=%s]", captchaTitle));

            assert captchaImg != null;

            Connection connection = Jsoup
                    .connect(captchaImg.absUrl("src"))
                    .validateTLSCertificates(false)
                    .ignoreContentType(true);

            Connection.Response response;
            try {
                response = connection.execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return BitmapFactory.decodeStream(new ByteArrayInputStream(response.bodyAsBytes()));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            mCaptchaImgView.setImageBitmap(bitmap);

            if (mProgressDialog != null)
                mProgressDialog.cancel();
        }
    }

    class MsgSendRequest extends AsyncTask<Map<String, String>, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(
                    MainActivity.this,
                    "",
                    "Sending. Please wait...",
                    true);
        }

        @Override
        protected List<String> doInBackground(Map<String, String>... requestParams) {
            Map<String, String> params = requestParams[0];
            Connection connection = Jsoup.connect(siteUrl)
                    .data("phone", params.get("phone"))
                    .data("name", params.get("name"))
                    .data("message", params.get("message"))
                    .data("captcha_sid", params.get("captcha_sid"))
                    .data("captcha_token", params.get("captcha_token"))
                    .data("captcha_response", params.get("captcha_response"))
                    .data("conditions", params.get("conditions"))
                    .data("op", params.get("op"))
                    .data("form_build_id", params.get("form_build_id"))
                    .data("form_id", params.get("form_id"))
                    .userAgent(userAgent)
                    .referrer(siteUrl)
                    .followRedirects(true)
                    .validateTLSCertificates(false)
                    .method(Connection.Method.POST);

            Connection.Response response;
            try {
                response = connection.execute();
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<String>() {{ add("Failed to connect" ); }};
            }

            Document responseDoc;
            try {
                 responseDoc = response.parse();
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<String>() {{ add("Internal error" ); }};
            }

            final Element errorsDiv = responseDoc.select("div.messages.error").first();
            Elements errors = responseDoc.select("div.messages.error > ul > li");

            if (errors.size() == 0) {
                if (errorsDiv != null) {
                    return new ArrayList<String>() {{
                        add(errorsDiv.text().replace("Mesaj de eroare", ""));
                    }};
                }
                else if (response.url().getPath().endsWith("sent"))
                    return null;
                else
                    return new ArrayList<String>() {{ add("Unclear error" ); }};
            }

            ArrayList<String> errorMsgs = new ArrayList<>();
            for (Element error : errors) {
                errorMsgs.add(error.text());
            }

            return errorMsgs;
        }

        @Override
        protected void onPostExecute(List<String> errors) {
            super.onPostExecute(errors);

            if (mProgressDialog != null)
                mProgressDialog.cancel();

            String finalMsg;
            if (errors == null || errors.size() == 0) {
                finalMsg = "Msg sent";
            } else {
                finalMsg = "Failed to send msg:\n- " + concatStringsWSep(errors, "\n- ");
            }

            loadWebDocument();
            new QuickWarning(MainActivity.this, finalMsg);
        }

        String concatStringsWSep(Iterable<String> strings, String separator) {
            StringBuilder sb = new StringBuilder();
            String sep = "";
            for(String s: strings) {
                sb.append(sep).append(s);
                sep = separator;
            }
            return sb.toString();
        }
    }
}

class QuickWarning {
    public QuickWarning(Context context, String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(msg);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}