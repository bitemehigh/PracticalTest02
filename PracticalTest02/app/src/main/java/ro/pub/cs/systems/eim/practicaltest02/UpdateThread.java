package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class UpdateThread extends Thread {
    private boolean isRunning;

    private ServerThread serverThread;

    public UpdateThread(ServerThread serverThreads) {
        isRunning = true;
        this.serverThread = serverThreads;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                String url_usd = "https://api.coindesk.com/v1/bpi/currentprice/USD.json";
                String url_eur = "https://api.coindesk.com/v1/bpi/currentprice/EUR.json";

                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";

                HttpGet httpGetUsd = new HttpGet(url_eur);
                HttpResponse httpGetResponse = httpClient.execute(httpGetUsd);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }

                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else
                    Log.i(Constants.TAG, pageSourceCode);

                JSONObject content = new JSONObject(pageSourceCode);

                JSONObject bpi = content.getJSONObject("bpi");
                JSONObject currency_info = bpi.getJSONObject("USD");
                String rate_float = currency_info.getString("rate_float");

                Double currencyInformationUSD = Double.parseDouble(rate_float);

                currency_info = bpi.getJSONObject("EUR");
                rate_float = currency_info.getString("rate_float");

                Double currencyInformationEUR = Double.parseDouble(rate_float);

                serverThread.setData("EUR", currencyInformationEUR);
                serverThread.setData("USD", currencyInformationUSD);

                sleep(60000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
