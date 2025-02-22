package zxf.java.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

public class WatchPodEvents {
    public static void main(String[] args) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            System.out.println("开始监听 Pod 变化...");

            client.pods().inAnyNamespace().watch(new Watcher<Pod>() {
                @Override
                public void eventReceived(Action action, Pod pod) {
                    String podName = pod.getMetadata().getName();
                    String namespace = pod.getMetadata().getNamespace();
                    System.out.printf("事件: %s, Pod: %s, 命名空间: %s%n", action, podName, namespace);

                    switch (action) {
                        case ADDED:
                            System.out.println("Pod 已创建: " + podName);
                            break;
                        case MODIFIED:
                            System.out.println("Pod 已更新: " + podName);
                            break;
                        case DELETED:
                            System.out.println("Pod 已删除: " + podName);
                            break;
                        case ERROR:
                            System.out.println("Pod 事件错误: " + podName);
                            break;
                        default:
                            System.out.println("未知事件类型: " + action);
                    }
                }

                @Override
                public void onClose(WatcherException cause) {
                    if (cause != null) {
                        System.err.println("监听器关闭，原因: " + cause.getMessage());
                    } else {
                        System.out.println("监听器正常关闭");
                    }
                }
            });

            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
