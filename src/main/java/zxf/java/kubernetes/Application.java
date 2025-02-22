package zxf.java.kubernetes;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.*;

public class Application {
    public static void main(String[] args) {
        // 1. 创建客户端（自动检测配置）
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {

            // 2. 获取 Pods
            PodList pods = client.pods().inNamespace("default").list();
            pods.getItems().forEach(pod ->
                    System.out.println("Pod: " + pod.getMetadata().getName()));

            // 3. 获取 Services
            ServiceList services = client.services().inNamespace("default").list();
            services.getItems().forEach(svc ->
                    System.out.println("Service: " + svc.getMetadata().getName()));

            // 4. 获取 Endpoints
            EndpointsList endpoints = client.endpoints().inNamespace("default").list();
            endpoints.getItems().forEach(ep ->
                    System.out.println("Endpoint: " + ep.getMetadata().getName()));


            client.pods()
                    .inNamespace(namespace)
                    .withName(podName)
                    .usingListener(new Watcher<Pod>() {
                        @Override
                        public void eventReceived(Action action, Pod pod) {
                            if (action == Action.MODIFIED) {
                                System.out.println("Pod状态变化: " + pod.getStatus().getPhase());
                            }
                        }

                        @Override
                        public void onClose(WatcherException cause) {
                            System.out.println("监听关闭");
                        }
                    })
                    .watchLog(log ->
                            System.out.println("[Custom] " + log)
                    );


            / 查询 VirtualService
            System.out.println("==== VirtualServices ====");
            client.resources(VirtualService.class)
                    .inNamespace(namespace)
                    .list()
                    .getItems()
                    .forEach(vs ->
                            System.out.printf("Name: %-20s Hosts: %s%n",
                                    vs.getMetadata().getName(),
                                    vs.getSpec().getHosts())
                    );

            // 查询 DestinationRule
            System.out.println(" ==== DestinationRules ====");
            client.resources(DestinationRule.class)
                    .inNamespace(namespace)
                    .list()
                    .getItems()
                    .forEach(dr ->
                            System.out.printf("Name: %-20s Host: %s%n",
                                    dr.getMetadata().getName(),
                                    dr.getSpec().getHost())
                    );

            // 查询指定名称的 Gateway
            String gatewayName = "my-gateway";
            System.out.println(" ==== Specific Gateway ====");
            client.resources(io.fabric8.istio.api.networking.v1beta1.Gateway.class)
                    .inNamespace(namespace)
                    .withName(gatewayName)
                    .getOptional()
                    .ifPresentOrElse(
                            gateway -> System.out.println("Found gateway: " + gateway.getMetadata().getName()),
                            () -> System.out.println("Gateway not found: " + gatewayName)
                    );
        }
    }
}
