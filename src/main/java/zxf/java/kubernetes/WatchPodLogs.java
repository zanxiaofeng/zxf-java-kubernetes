package zxf.java.kubernetes;

import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.LogWatch;

public class WatchPodLogs {
    private static final String DEFAULT_NAMESPACE = "default";

    public static void main(String[] args) {
        watchLog("zxf-java-memory-app-6cb76bb7db-2856k");
    }

    private static void watchLog(String pod) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            try (LogWatch logWatch = client.pods()
                    .inNamespace(DEFAULT_NAMESPACE)
                    .withName(pod)
                    .tailingLines(100)
                    .withPrettyOutput()
                    .watchLog(System.out)) {
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch (KubernetesClientException | InterruptedException e) {
            System.err.println("日志监听异常: " + e.getMessage());
        }
    }
}
