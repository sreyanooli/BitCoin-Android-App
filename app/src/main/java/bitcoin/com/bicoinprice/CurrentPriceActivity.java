package bitcoin.com.bicoinprice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrentPriceActivity extends AppCompatActivity {


    String url = "https://api.coindesk.com/v1/bpi/currentprice.json";

    OkHttpClient mOkHttpClient;

    SwipeRefreshLayout swipe;
    TextView tvprice;
    TextView tvprice1;
    TextView tvprice2;
    ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentprice);
        tvprice = findViewById(R.id.tvprice);
        tvprice2 = findViewById(R.id.tvprice2);
        tvprice1 = findViewById(R.id.tvprice1);
        progressBar = findViewById(R.id.progressBar1);
        swipe = findViewById(R.id.swipe);
        mOkHttpClient = new OkHttpClient();
        refreshData();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                refreshData();
            }
        });
    }

    private void refreshData() {

        Request mRequest = new Request.Builder().url(url).build();
        progressBar.setVisibility(View.VISIBLE);
        mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        swipe.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CurrentPriceActivity.this, getString(R.string.someerror),
                                Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                parseData(response.body().string());

            }
        });


    }

    private void parseData(final String string) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    swipe.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(string);

                    JSONObject jsonObject1 = jsonObject.getJSONObject("bpi").
                            getJSONObject("USD");
                    JSONObject Time = jsonObject.getJSONObject("time");


                    String rate = jsonObject1.getString("rate");
                    String time = Time.getString("updated");
                    String time1 = Time.getString("updatedISO");
                    String time2 = Time.getString("updateduk");

                    tvprice.setText(jsonObject1.getString("code") + " PRICE : " +
                            Html.fromHtml(jsonObject1.getString("symbol")) + rate + "\n   Time:  "+ time);

                    JSONObject jsonObject2 = jsonObject.getJSONObject("bpi").
                            getJSONObject("GBP");


                    String rate2 = jsonObject2.getString("rate");

                    tvprice1.setText(jsonObject2.getString("code") + " PRICE : " +
                            Html.fromHtml(jsonObject2.getString("symbol")) + rate2 + "\n   Time:  "+ time1 );

                    JSONObject jsonObject3 = jsonObject.getJSONObject("bpi").
                            getJSONObject("EUR");


                    String rate3 = jsonObject3.getString("rate");

                    tvprice2.setText(jsonObject3.getString("code") + " PRICE : " +
                            Html.fromHtml(jsonObject3.getString("symbol")) + rate3 + "\n   Time:  "+ time2);
                } catch (Exception e) {


                }


            }
        });


    }
}
