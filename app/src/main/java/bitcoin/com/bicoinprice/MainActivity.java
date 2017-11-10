package bitcoin.com.bicoinprice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvprice;
    TextView tvrecent;
    TextView tvnews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvprice=findViewById(R.id.tvprice);
        tvrecent=findViewById(R.id.tvrecent);
        tvnews=findViewById(R.id.tvnews);

        tvprice.setOnClickListener(this);
        tvrecent.setOnClickListener(this);
        tvnews.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {


        switch (view.getId())
        {

            case R.id.tvprice:

                Intent intent=new Intent(MainActivity.this,CurrentPriceActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  startActivity(intent);

                break;

            case R.id.tvrecent:
                Intent intent1=new Intent(MainActivity.this,RecentActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;

            case R.id.tvnews:
                Intent intent2=new Intent(MainActivity.this,News.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                break;



        }
    }
}
