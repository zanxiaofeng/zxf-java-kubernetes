package zxf.java.kubernetes;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.Config;

import java.util.Optional;
import java.util.stream.Collectors;

public class QueryKubernetesResources {
    private static final String DEFAULT_NAMESPACE = "default";
    private static final String ARRAY_JOINING = ", ";

    public static void main(String[] args) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            System.out.println("******************==============================================================================================================================================******************\n\n");
            Config config = client.getConfiguration();

            System.out.println("Cluster Server URL: " + config.getMasterUrl());
            System.out.println("Cluster CA Certificate: " + config.getCaCertData());
            System.out.println("Cluster Namespace: " + config.getNamespace());
            System.out.println("Current Context: " + config.getCurrentContext());
            System.out.println("Context Cluster: " + config.getCurrentContext().getContext().getCluster());
            System.out.println("Context User: " + config.getCurrentContext().getContext().getUser());
            System.out.println("Context Namespace: " + config.getCurrentContext().getContext().getNamespace());


            System.out.println("******************==============================================================================================================================================******************\n");
            NodeList nodes = client.nodes().list();
            nodes.getItems().forEach(node -> System.out.println(nodeInfo(node) + "\n"));

            System.out.println("******************==============================================================================================================================================******************\n");
            ResourceQuotaList resourceQuotas = client.resourceQuotas().list();
            resourceQuotas.getItems().forEach(resourceQuota -> System.out.println(resourceQuota.toString() + "\n"));

            System.out.println("******************==============================================================================================================================================******************\n");
            PodList pods = client.pods().inNamespace(DEFAULT_NAMESPACE).withLabel("app", "zxf-java-memory-app").list();
            pods.getItems().forEach(pod -> System.out.println(podInfo(pod) + "\n"));

            System.out.println("******************==============================================================================================================================================******************\n");
            DeploymentList deployments = client.apps().deployments().inNamespace(DEFAULT_NAMESPACE).list();
            deployments.getItems().forEach(deployment -> System.out.println(deployment.toString() + "\n"));

            System.out.println("******************==============================================================================================================================================******************\n");
            ServiceList services = client.services().inNamespace(DEFAULT_NAMESPACE).list();
            services.getItems().forEach(svc -> System.out.println(serviceInfo(svc) + "\n"));

            System.out.println("******************==============================================================================================================================================******************\n");
            EndpointsList endpoints = client.endpoints().inNamespace(DEFAULT_NAMESPACE).list();
            endpoints.getItems().forEach(ep -> System.out.println(endpointsInfo(ep) + "\n"));
            System.out.println("******************==============================================================================================================================================******************\n\n");
        }
    }

    private static String nodeInfo(Node node) {
        return String.format("Node(name=%s, podCIDRs=%s, addresses=[%s], allocatable=%s, capacity=%s)", node.getMetadata().getName(), node.getSpec().getPodCIDRs(),
                node.getStatus().getAddresses().stream().map(QueryKubernetesResources::nodeAddressInfo).collect(Collectors.joining(ARRAY_JOINING)),
                node.getStatus().getAllocatable(), node.getStatus().getCapacity()
        );
    }

    private static String nodeAddressInfo(NodeAddress nodeAddress) {
        return String.format("(address=%s, type=%s)", nodeAddress.getAddress(), nodeAddress.getType());
    }

    private static String podInfo(Pod pod) {
        return String.format("Pod(name=%s/%s, owners=[%s], spec=(dnsPolicy=%s, nodeName=%s, priorityClass=%s,restartPolicy=%s, containers=[%s]), status=(podIPs=%s, hostIPs=%s, qosClass=%s, phase=%s, startTime=%s, containerStatuses=[%s]))", pod.getMetadata().getName(), pod.getMetadata().getNamespace(),
                pod.getMetadata().getOwnerReferences().stream().map(QueryKubernetesResources::ownerReferenceInfo).collect(Collectors.joining(ARRAY_JOINING)),
                pod.getSpec().getDnsPolicy(), pod.getSpec().getNodeName(), pod.getSpec().getPriorityClassName(), pod.getSpec().getRestartPolicy(),
                pod.getSpec().getContainers().stream().map(QueryKubernetesResources::containerInfo).collect(Collectors.joining(ARRAY_JOINING)),
                pod.getStatus().getPodIPs().stream().map(PodIP::getIp).collect(Collectors.joining(ARRAY_JOINING)),
                pod.getStatus().getHostIPs().stream().map(HostIP::getIp).collect(Collectors.joining(ARRAY_JOINING)),
                pod.getStatus().getQosClass(), pod.getStatus().getPhase(), pod.getStatus().getStartTime(),
                pod.getStatus().getContainerStatuses().stream().map(QueryKubernetesResources::containerStatusInfo).collect(Collectors.joining(ARRAY_JOINING))
        );
    }

    private static String containerInfo(Container container) {
        return String.format("(name=%s, image=%s, imagePullPolicy=%s, ports=%s, env=[%s])", container.getName(), container.getImage(), container.getImagePullPolicy(),
                container.getPorts().stream().map(QueryKubernetesResources::containerPortInfo).collect(Collectors.joining(ARRAY_JOINING)),
                container.getEnv().stream().map(QueryKubernetesResources::envVarInfo).collect(Collectors.joining(ARRAY_JOINING))
        );
    }

    private static String containerPortInfo(ContainerPort containerPort) {
        return String.format("(protocol=%s, containerPort=%d, hostIp/Port=%s/%d)", containerPort.getProtocol(),
                containerPort.getContainerPort(), containerPort.getHostIP(), containerPort.getHostPort()
        );
    }

    private static String envVarInfo(EnvVar envVar) {
        return String.format("(name=%s, value=%s, valueFrom=%s)", envVar.getName(), envVar.getValue(), envVar.getValueFrom());
    }

    private static String containerStatusInfo(ContainerStatus containerStatus) {
        return String.format("(image=%s, started=%s, running=%s, waiting=%s, terminated=%s)", containerStatus.getImage(), containerStatus.getStarted(), containerStatus.getState().getRunning().getStartedAt(),
                Optional.ofNullable(containerStatus.getState().getWaiting()).map(waiting -> waiting.getReason() + "/" + waiting.getMessage()).orElse("null"),
                Optional.ofNullable(containerStatus.getState().getTerminated()).map(terminated -> terminated.getReason() + "/" + terminated.getMessage()).orElse("null")
        );
    }

    private static String serviceInfo(Service service) {
        return String.format("Service(name=%s/%s, spec=(type=%s, clusterIPs=%s, externalIPs=%s, trafficPolicy=%s/%s, ports=[%s], loadBalancer=%s/%s, selector=%s))",
                service.getMetadata().getName(), service.getMetadata().getNamespace(), service.getSpec().getType(), service.getSpec().getClusterIPs(),
                service.getSpec().getExternalIPs(), service.getSpec().getInternalTrafficPolicy(), service.getSpec().getExternalTrafficPolicy(),
                service.getSpec().getPorts().stream().map(QueryKubernetesResources::servicePortInfo).collect(Collectors.joining(ARRAY_JOINING)),
                service.getSpec().getLoadBalancerIP(), service.getSpec().getLoadBalancerClass(), service.getSpec().getSelector());
    }

    private static String servicePortInfo(ServicePort servicePort) {
        return String.format("(protocol=%s, port=%d, nodePort=%d, targetPort=%s)", servicePort.getProtocol(), servicePort.getPort(), servicePort.getNodePort(), servicePort.getTargetPort().toString());
    }

    private static String endpointsInfo(Endpoints endpoints) {
        return String.format("Endpoints(name=%s/%s, subsets=[%s])", endpoints.getMetadata().getName(), endpoints.getMetadata().getNamespace(), endpoints.getSubsets().stream().map(QueryKubernetesResources::endpointSubsetInfo).collect(Collectors.joining(ARRAY_JOINING)));
    }

    private static String endpointSubsetInfo(EndpointSubset subset) {
        return String.format("(addresses=[%s], notReadyAddresses=[%s], ports=[%s])", subset.getAddresses().stream().map(QueryKubernetesResources::endpointAddressInfo).collect(Collectors.joining(ARRAY_JOINING)), subset.getNotReadyAddresses().stream().map(QueryKubernetesResources::endpointAddressInfo).collect(Collectors.joining(ARRAY_JOINING)), subset.getPorts().stream().map(QueryKubernetesResources::endpointPortInfo).collect(Collectors.joining(ARRAY_JOINING)));
    }

    private static String endpointAddressInfo(EndpointAddress endpointAddress) {
        return String.format("(ip=%s/, node=%s, ref=%s)", endpointAddress.getIp(), endpointAddress.getNodeName(), objectReferenceInfo(endpointAddress.getTargetRef()));
    }

    private static String endpointPortInfo(EndpointPort endpointPort) {
        return String.format("(port=%d/%s)", endpointPort.getPort(), endpointPort.getProtocol());
    }

    private static String objectReferenceInfo(ObjectReference objectReference) {
        if (objectReference == null) {
            return "null";
        }
        return String.format("(kind=%s, name=%s/%s)", objectReference.getKind(), objectReference.getName(), objectReference.getNamespace());
    }

    private static String ownerReferenceInfo(OwnerReference ownerReference) {
        if (ownerReference == null) {
            return "null";
        }
        return String.format("(kind=%s, name=%s)", ownerReference.getKind(), ownerReference.getName());
    }
}
