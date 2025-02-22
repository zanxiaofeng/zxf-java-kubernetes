#将当前 shell 的 Docker 客户端指向 Minikube 的 Docker 守护进程(minikube使用了单独的docker env)
eval $(minikube docker-env)
docker build -t zxf-java-memory-app:latest ./

kubectl apply -f zxf-java-memory-deployment.yaml