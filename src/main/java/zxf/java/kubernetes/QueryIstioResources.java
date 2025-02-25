package zxf.java.kubernetes;

import io.fabric8.istio.api.api.networking.v1alpha3.DestinationRule;
import io.fabric8.istio.api.api.networking.v1alpha3.VirtualService;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

public class QueryIstioResources {
    private static final String DEFAULT_NAMESPACE = "default";
    private static final String ARRAY_JOINING = ", ";

    public static void main(String[] args) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            System.out.println("==== VirtualServices ====");
            client.resources(VirtualService.class)
                    .inNamespace(DEFAULT_NAMESPACE)
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
                    .inNamespace(DEFAULT_NAMESPACE)
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
                    .inNamespace(DEFAULT_NAMESPACE)
                    .withName(gatewayName)
                    .getOptional()
                    .ifPresentOrElse(
                            gateway -> System.out.println("Found gateway: " + gateway.getMetadata().getName()),
                            () -> System.out.println("Gateway not found: " + gatewayName)
                    );
        }
    }
}
