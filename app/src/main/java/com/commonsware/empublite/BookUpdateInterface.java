package com.commonsware.empublite;

import retrofit.http.GET;

/**
 * Created by ivan on 24/11/15.
 */
public interface BookUpdateInterface {
    @GET("/misc/empublite-update.json")
    BookUpdateInfo update();
}
