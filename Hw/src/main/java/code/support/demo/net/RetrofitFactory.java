package code.support.demo.net;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitFactory 工厂类
 */
public class RetrofitFactory {
    private RetrofitFactory() {
    }

    /**
     * @param clazz   目标接口 class
     * @param baseUrl BaseUrl
     * @return 返回目标接口的具体实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T createService(Class<?> clazz, String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
        return (T) retrofit.create(clazz);
    }

    public static class Builder {
        private String   baseUrl;
        private Class<?> clazz;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder concreteClass(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        /**
         * @return 返回目标接口的具体实例
         */
        @SuppressWarnings("unchecked")
        public <T> T build() {
            Retrofit retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build();
            return (T) retrofit.create(clazz);
        }
    }
}
