package zxf.java.kubernetes;

import io.fabric8.kubernetes.api.model.AuthProviderConfig;
import io.fabric8.kubernetes.api.model.AuthProviderConfigBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;

public class ConfigFactory {
    public static Config inCluster(String file) {
        return new ConfigBuilder().build();
    }

    public static Config fromKubeconfig(String file) {
        return Config.fromKubeconfig(file);
    }

    public static Config customisedWithOauth() {

        AuthProviderConfig authProviderConfig = new AuthProviderConfigBuilder().withAdditionalProperties()
        authProviderConfig.

                Config config = new ConfigBuilder()
                .withMasterUrl("https://<kubernetes-api-server>") // Kubernetes API Server 地址
                .withOauthToken("<ID-Token>") // 使用 ID Token 进行认证
                .withAuthProvider()
                .
                .withOidcTokenProvider(new CustomOIDCTokenProvider()) // 自定义 OIDC Token 提供者
                .build();

    }
}
