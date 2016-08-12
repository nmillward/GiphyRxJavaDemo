package com.nickmillward.giphyrxjavademo.Model;

/**
 * Created by nmillward on 8/12/16.
 */
public class Data {

    private String image_url;
    private String fixed_height_downsampled_url;

    public String getRandomGif() {
        return image_url;
    }

    public String getFixed_height_downsampled_url() {
        return fixed_height_downsampled_url;
    }
}
