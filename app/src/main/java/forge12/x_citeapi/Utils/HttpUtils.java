package forge12.x_citeapi.Utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtils {
    /**
     * The Base URL is used to ensure all posts are only going to this url.
     */
    //private static final String BASE_URL = "http://localhost/www.x-cite-group.de/rest/";

    //private static final String BASE_URL = "http://192.168.178.26/www.x-cite-group.de/rest/v2/";
    //private static final String BASE_URL = "http://www.x-cite.de/api/rest/v2/";
     private static final String BASE_URL = "http://192.168.1.112/API/rest/v2/";
    /**
     * The Client responsible to call the URL
     */
    private static AsyncHttpClient client = new AsyncHttpClient();
    /**
     * The Security Token to access the API
     */
    private static String API_TOKEN = "i2AedoCGYblCgVPRqPtb";

    /**
     * Retrieve Data from an given URL.
     *
     * @param url             String
     * @param params          RequestParams
     * @param responseHandler AsyncHttpResponseHandler
     */
    public static void get(String url, RequestParams params, String token, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "bearer=\"" + token + "\"");
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, String token, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "bearer=\"" + token + "\"");
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url, RequestParams params, String token, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "bearer=\"" + token + "\"");
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Converts the relative url to the absolute url
     *
     * @param relativeUrl String
     * @return String
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
