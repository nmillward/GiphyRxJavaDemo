package com.nickmillward.giphyrxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nickmillward.giphyrxjavademo.Model.Gif;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv_gif) ImageView iv_gif;
    @BindView(R.id.btn_new_gif) Button btn_new_gif;
    @BindView(R.id.tv_gif) TextView tv_gif;

    private CompositeSubscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        subscription = new CompositeSubscription();

        btn_new_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GiphyService service = ServiceFactory.createRetrofitService(GiphyService.class, GiphyService.SERVICE_ENDPOINT);
                service.getRandomGif()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Gif>() {
                            @Override
                            public void onCompleted() {
                                Log.v("MAIN ACTIVITY", "onCompleted reached");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.v("MAIN ACTIVITY", "onError reached " + e);
                            }

                            @Override
                            public void onNext(Gif gif) {
                                Glide.with(MainActivity.this).load(gif.getData().getFixed_height_downsampled_url()).into(iv_gif);
                                tv_gif.setText("Giphy URL: " + gif.getData().getRandomGif());
                            }
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }
}
