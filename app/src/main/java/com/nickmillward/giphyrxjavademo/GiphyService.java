package com.nickmillward.giphyrxjavademo;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by nmillward on 8/10/16.
 */
public interface GiphyService {

    String SERVICE_ENDPOINT = "http://api.giphy.com/v1/gifs/";

    @GET("random?api_key=dc6zaTOxFJmzC&tag=funny&rating=g")
    Observable<Gif> getRandomGif(String randomGif);
}
