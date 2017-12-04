package cn.ymex.updater;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryName;


/**
 * Created by ymexc on 2017/11/27.
 * About: api
 */

public interface ApiService {
    @GET("/{app}")
    Observable<ResponseBody> checkVersion(@Path("app") int app, @Query("channel") String channel);
}
