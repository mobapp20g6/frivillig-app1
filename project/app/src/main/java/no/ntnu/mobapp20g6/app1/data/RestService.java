package no.ntnu.mobapp20g6.app1.data;

import no.ntnu.mobapp20g6.app1.data.api.AuthApi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestService {
    public static String DOMAIN = "http://10.0.2.2:8080/appsrv-1.0/";

    private static Retrofit retrofit;

    private AuthApi authService;

    public RestService() {


        authService = retrofit.create(AuthApi.class);
    }

    public static Retrofit getRetrofitClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(DOMAIN)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
