#将当前 shell 的 Docker 客户端指向 Minikube 的 Docker 守护进程(minikube使用了单独的docker env)
eval $(minikube docker-env)
docker build -t zxf-springboot-camunda-arch-app:latest ./ -f ./Dockerfile-arch-app
docker build -t zxf-springboot-camunda-arch-rest:latest . -f ./Dockerfile-arch-rest

kubectl apply -f zxf-springboot-camunda-arch-deployment.yaml