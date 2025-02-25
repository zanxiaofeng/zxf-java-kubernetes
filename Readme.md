# MiniKube
#### commands
- minikube start --driver=docker
- minikube status
- minikube dashboard
- minikube stop
- minikube delete
#### config files
- ~/.minikube
- ~/.kube

# Istio
#### 验证安装
- kubectl get pods -n istio-system
#### 为默认命名空间启用自动Sidecar注入
- kubectl label namespace default istio-injection=enabled
#### 卸载Istio
- ./bin/istioctl manifest generate --set profile=demo | kubectl delete -f -

# kubernetes config
- Using In-Cluster Configuration
- Using a Kubeconfig File
- Using Customised Configuration(Token-Based Authentication, Username and Password)