package bitcoin.com.bicoinprice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Srinath on 27/10/17.
 */

public class RecentActivity extends AppCompatActivity {

    //start=2013-09-01&end=2013-09-05
    //  String url = "https://api.coindesk.com/v1/bpi/historical/close.json?";
    String url = "https://api.coindesk.com/v1/bpi/currentprice.json";

    OkHttpClient mOkHttpClient;

    RecyclerView rv;

    List<PriceModel> list;
    ProgressBar progressBar;
    SharedPreferences mSharedPreferences;

    TextView tvprice;
    TextView tvdis;
    SwipeRefreshLayout swipeRefreshLayout;

    double price1, price2;

    Realm realm;

    Adapter adapter;
    Request mRequest;
    OrderedRealmCollection<PriceModel> orderedRealmCollection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);

        swipeRefreshLayout = findViewById(R.id.swipe);
        realm = Realm.getDefaultInstance();
        rv = findViewById(R.id.rv);
        tvdis = findViewById(R.id.tvdis);
        mSharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        Date date = new Date();
        progressBar = findViewById(R.id.progressBar1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = simpleDateFormat.format(yesterday(-1));
        String date2 = simpleDateFormat.format(yesterday(-2));
        mOkHttpClient = new OkHttpClient();
        String url1 = url + "start=" + date2 + "&end=" + date1;
        //  String url="url+\"start=\"+date1+\"&end=\"+date2";
        Log.e("data", url1);


        orderedRealmCollection = realm.where(PriceModel.class).findAll();
        adapter = new Adapter(orderedRealmCollection, true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        mRequest = new Request.Builder().url(url).build();


        refreshData();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                refreshData();
            }
        });

    }

    private void refreshData() {
        mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RecentActivity.this, getString(R.string.someerror),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                // parseData(response.body().string());
                parseData2(response.body().string());

            }
        });
    }

    private Date yesterday(int range) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, range);
        return cal.getTime();
    }

    private void parseData2(final String string) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(string);


                    JSONObject jsonObjecttime = jsonObject.getJSONObject("time");
                    tvdis.setText(jsonObject.getString("disclaimer"));
                    String time = jsonObjecttime.getString("updatedISO");


                    JSONObject jsonObject1 = jsonObject.getJSONObject("bpi").
                            getJSONObject("USD");


                    String rate = jsonObject1.getString("rate");

                    //  tvprice.setText(jsonObject1.getString("code") + " PRICE : " +
                    //        Html.fromHtml(jsonObject1.getString("symbol")) + rate);


                    JSONObject jsonObject2 = jsonObject.getJSONObject("bpi").
                            getJSONObject("GBP");


                    String rate2 = jsonObject2.getString("rate");

                    // tvprice1.setText(jsonObject2.getString("code") + " PRICE : " +
                    //       Html.fromHtml(jsonObject2.getString("symbol")) + rate2);

                    JSONObject jsonObject3 = jsonObject.getJSONObject("bpi").
                            getJSONObject("EUR");


                    String rate3 = jsonObject3.getString("rate");

                    //  tvprice2.setText(jsonObject3.getString("code") + " PRICE : " +
                    //        Html.fromHtml(jsonObject3.getString("symbol")) + rate3);

                    if(!realm.isInTransaction()) {

                        realm.beginTransaction();
                        //2017-10-27T06:29:00+00:00
                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

                        PriceModel priceModel = realm.createObject(PriceModel.class, time);
                        priceModel.setDate(simpleDateFormat.parse(time));
                        priceModel.setPrice(rate);
                        realm.copyToRealmOrUpdate(priceModel);
                        realm.commitTransaction();
                        adapter.notifyDataSetChanged();



                        Date date=new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        cal.add(Calendar.MINUTE, -5);
                        RealmResults<PriceModel> realmResults =  realm.where(PriceModel.class).
                                greaterThan("date",cal.getTime()).findAll();

                        if(realmResults!=null && realmResults.size()>0)
                        {

                            for(PriceModel p:realmResults)
                            {

                                Log.e("data",p.getDate1());
                            }


                        }

                    }


                } catch (Exception e) {


                    Log.e("err", e.toString());
                }


            }
        });


    }


    @Override
    protected void onDestroy() {
        try {
            realm.close();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    private void parseData(final String string) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {

                    progressBar.setVisibility(View.GONE);
                    Log.e("data", string + "");
                    mSharedPreferences.edit().putString("data", string).apply();
                    JSONObject jsonObject = new JSONObject(string);


                    String[] data = jsonObject.getString("bpi").split(",");

                    for (int i = 0; i < data.length; i++) {

                        switch (i) {

                            case 0:
                                tvprice.append(data[i].substring(1) + "\n");
                                break;
                            case 1:
                                tvprice.append(data[i].substring(0, data[i].length() - 1) + "\n");
                                break;
                        }
                    }

                    //  tvdis.setText(jsonObject.getString("disclaimer"));

                    // String rate= jsonObject.getJSONObject("bpi").getJSONObject("USD").getString("rate");


                } catch (Exception e) {


                }


            }
        });


    }


    public class Adapter extends RealmRecyclerViewAdapter<PriceModel, Adapter.Vh> {


        public Adapter(@Nullable OrderedRealmCollection<PriceModel> data, boolean autoUpdate) {
            super(data, autoUpdate);
        }

        @Override
        public Vh onCreateViewHolder(ViewGroup parent, int viewType) {

            return new Vh(getLayoutInflater().inflate(R.layout.inflate, null));
        }

        @Override
        public void onBindViewHolder(Vh holder, int position) {


            try {
                holder.tvprice.setText("USD $" + getData().get(position).getPrice());
                holder.tvdate.setText(getData().get(position).getDate() + "");
            } catch (Exception e) {

            }

        }

        public class Vh extends RecyclerView.ViewHolder {

            TextView tvprice;
            TextView tvdate;

            public Vh(View itemView) {
                super(itemView);
                tvprice = itemView.findViewById(R.id.tvprice);
                tvdate = itemView.findViewById(R.id.tvdate);
            }
        }

    }


}
