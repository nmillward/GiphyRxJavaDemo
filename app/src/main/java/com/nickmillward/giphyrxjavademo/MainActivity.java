package com.nickmillward.giphyrxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
    @BindView(R.id.progressBar) ProgressBar progressBar;

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
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(Gif gif) {
                                progressBar.setVisibility(View.VISIBLE);

                                /**
                                 * Glide request without '.into()' for Giphy still thumbnail
                                 * Add BlurTransformation to the thumbnail§
                                 */
                                DrawableRequestBuilder<String> thumbnailRequest = Glide
                                        .with(MainActivity.this)
                                        .load(gif.getData().getFixed_height_small_still_url())
                                        .bitmapTransform(new jp.wasabeef.glide.transformations.BlurTransformation(MainActivity.this, 25));

                                /**
                                 * Pass thumbnailRequest as the thumbnail parameter
                                 * Once resource has loaded successfully, hide progressBar
                                 */
                                Glide.with(MainActivity.this)
                                        .load(gif.getData().getFixed_height_downsampled_url())
                                        .thumbnail(thumbnailRequest)
                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                progressBar.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(iv_gif);

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
