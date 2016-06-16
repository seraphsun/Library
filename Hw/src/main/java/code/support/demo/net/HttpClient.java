package code.support.demo.net;

import code.support.demo.bean.Domin;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * HTTP     http协议
 * DELETE   请求服务器删除指定的页面
 * GET      请求指定的页面信息，并返回实体主体
 * HEAD     类似于get请求，只不过返回的响应中没有具体的内容，用于获取报头
 * OPTIONS  允许客户端查看服务器的性能
 * PATCH    实体中包含一个表，表中说明与该URI所表示的原内容的区别
 * POST     向指定资源提交数据进行处理请求（例如提交表单或者上传文件）。数据被包含在请求体中。POST请求可能会导致新的资源的建立和/或已有资源的修改
 * PUT      从客户端向服务器传送的数据取代指定的文档的内容
 *
 * Header Headers 表示请求头的内容
 * FormUrlEncoded 表示请求体以哪种编码编辑
 * Multipart      表示请求体多部分组成，对参数的说明
 *
 * Body     当你想直接控制一个post/put的请求体 Body作为一个序列化的对象，其属性和属性值作为对应请求参数的key-value
 * Field    当参数是一对一或一对多的键值结构时，可以直接使用Field注解
 * FieldMap 以map的形式封装参数，通过ImmutableMap.of()将一个key，一个value分序写入
 * Part     作为一个文件上传，指定参数的编码规范
 * PartMap  是作为对文件的一个描述的map，它不可以是key或者value
 * Path     用于替换url中的某个字段
 * Query    指定的参数不会被转义
 * QueryMap 可以过滤json返回的某些数据
 *
 * Created by Design on 2016/4/1.
 */
public class HttpClient {

    public static final String BASE_URL = "www.google.com";
    private HttpInterface reqClient;

    // 在访问HttpClient时创建单例
    private static final HttpClient INSTANCE = new HttpClient();

    // Singleton模式
    private HttpClient() {
        // 获取联网请求的客户端
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // 添加 json 转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 添加 RxJava 适配器
                .build();

        reqClient = retrofit.create(HttpInterface.class);
    }

    // 获取单例
    public static HttpClient getInstance() {
        return INSTANCE;
    }

    /**
     * 以下为联网请求的通用方法区
     *
     * @param subscriber 由调用者传过来的观察者对象
     */
    public void getUrl(Subscriber<Domin> subscriber) {

        reqClient
                .getUrl("design")
                .subscribeOn(Schedulers.io()) // 分线程执行请求
                .unsubscribeOn(Schedulers.io()) // 分线程执行请求
                .observeOn(AndroidSchedulers.mainThread()) // 主线程处理界面
                .subscribe(subscriber);
    }
}
