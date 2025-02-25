package zxf.java.kubernetes.token;
import io.fabric8.kubernetes.client.OIDCTokenProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.FormBody;

import java.io.IOException;

public class CustomOIDCTokenProvider implements OIDCTokenProvider {

    private String refreshToken;
    private String clientId;
    private String clientSecret;
    private String oidcIssuerUrl;

    public CustomOIDCTokenProvider(String refreshToken, String clientId, String clientSecret, String oidcIssuerUrl) {
        this.refreshToken = refreshToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.oidcIssuerUrl = oidcIssuerUrl;
    }

    @Override
    public String getToken() {
        // 使用 Refresh Token 获取新的 ID Token
        OkHttpClient httpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        Request request = new Request.Builder()
                .url(oidcIssuerUrl + "/oauth2/v1/token")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                // 解析响应，获取新的 ID Token
                return parseIdTokenFromResponse(responseBody);
            } else {
                throw new RuntimeException("Failed to refresh token: " + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to refresh token", e);
        }
    }

    private String parseIdTokenFromResponse(String responseBody) {
        // 解析 JSON 响应，提取 ID Token
        // 例如，使用 Jackson 或 Gson 解析
        // 假设响应格式为：{"id_token": "xxx", "access_token": "xxx", ...}
        return "parsed-id-token";
    }
}
