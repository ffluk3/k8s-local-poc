# Local Development POC

This repo uses the following to provide a Kubernetes local developer experience.

## Technologies used

- [Wiremock](https://wiremock.org/)
- [Gradle](https://gradle.org/)
  - [Spring Boot](https://spring.io/projects/spring-boot) Java Application
- [Jib](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin) - fast and clean docker image builds
- [Minikube](https://minikube.sigs.k8s.io/docs/start/) - local Kubernetes done easy!
- [Helm](https://helm.sh/)

> NOTE: [k9s](https://k9scli.io/) is highly recommended for managing the cluster as you are iterating


## Getting Started

### Step 0: Set up minikube

```shell
# Sets up a minikube instance locally
minikube start

# enable local registry for minikube
minikube addons enable registry
```

### Step 1: Build application docker image (useful for running everything in the cluster)

```shell
# In the first terminal, stand up redirection to the minikube instance for registry mocking
docker run --rm -it --network=host alpine ash -c "apk add socat && socat TCP-LISTEN:5000,reuseaddr,fork TCP:$(minikube ip):5000"

# In a second terminal, build/package/push the image
./gradlew jibDockerBuild 
docker push localhost:5000/hello:1.0.0
```

### Step 2: Install the helm charts

```shell
helm install sample-svc ./charts/sample-svc
helm install wiremock ./charts/wiremock
```

### Step 3: Verify the application 

```shell
# In a new terminal, port-forward the sample service

export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=sample-svc,app.kubernetes.io/instance=sample-svc" -o jsonpath="{.items[0].metadata.name}")
export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
kubectl --namespace default port-forward $POD_NAME 8080:$CONTAINER_PORT
```

You can now view the service running at `localhost:8080`!

### Step 4: Update wiremock configurations

```shell
# First, port forward wiremock to 9021
export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=wiremock,app.kubernetes.io/instance=wiremock" -o jsonpath="{.items[0].metadata.name}")
export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
kubectl --namespace default port-forward $POD_NAME 9021:$CONTAINER_PORT
```

You can then view bindings at http://localhost:9021/__admin/webapp

```shell
# 1. Download the latest binary (~101 MB):
os_specific_file=telepresence-darwin-arm64 # telepresence-darwin-amd64 for Intel Macs
sudo curl -fL https://app.getambassador.io/download/tel2oss/releases/download/v2.14.0/$os_specific_file -o /usr/local/bin/telepresence

# 2. Make the binary executable:
sudo chmod a+x /usr/local/bin/telepresence

# 3. Install into your local cluster (check that you are in minikube via (kubectl config current-context)
telepresence helm install

# 4. Connect to telepresence in the target cluster
sudo telepresence connect

# 5. Forward traffic from local spring boot (TODO: leverage environment file)
telepresence intercept sample-svc --port 8080:80 --env-file /tmp/hello.env

# 6. Confirm the service by curling it. Bonus: use port forwarding to mess with wiremock
curl http://sample-svc
```