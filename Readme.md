# MiniKube
#### Start/Stop/Status
- minikube start --driver=docker
- minikube status
- minikube stop
- minikube delete
#### Config
- ~/.minikube
- ~/.kube

# Build image to minikube docker env
- eval $(minikube docker-env)  #将当前 shell 的 Docker 客户端指向 Minikube 的 Docker 守护进程
- docker build -t zxf-java-memory-app:latest ../zxf-java-memory

# Deployment
kubectl apply -f zxf-java-momory-deployment.yaml