package cn.ymex.updater;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;


/**
 * Created by ymexc on 2017/11/27.
 * About: api
 */

public interface ApiService {
    @GET("/")
    Observable<ResultVersion> checkVersion();
}
