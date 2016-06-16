package code.support.demo.net;

import code.support.demo.bean.Domin;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 网络请求的接口
 * Created by Design on 2016/4/1.
 */
public interface HttpInterface {

    // 使用 RxJava 的方法,返回一个 Observable
    @GET("/google/name")
    Observable<Domin> getUrl(@Path("name") String name);
}
