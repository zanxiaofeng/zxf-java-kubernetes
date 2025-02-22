package zxf.java.kubernetes;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.*;

public class QueryKubernetesResources {
    private static final String DEFAULT_NAMESPACE = "default";

    public static void main(String[] args) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            NodeList nodes = client.nodes().list();
            nodes.getItems().forEach(node -> System.out.println("Node: " + node.getMetadata().getName()));

            PodList pods = client.pods().inNamespace(DEFAULT_NAMESPACE).list();
            pods.getItems().forEach(pod -> System.out.println("Pod: " + pod.getMetadata().getName()));

            ServiceList services = client.services().inNamespace(DEFAULT_NAMESPACE).list();
            services.getItems().forEach(svc -> System.out.println("Service: " + svc.getMetadata().getName()));

            EndpointsList endpoints = client.endpoints().inNamespace(DEFAULT_NAMESPACE).list();
            endpoints.getItems().forEach(ep -> System.out.println("Endpoint: " + ep.getMetadata().getName()));
        }
    }
}
