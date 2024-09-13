### aks cluster setup

```
1. create a aks cluster and a container registry
2. run below command build a docker image and build it on aks

command

```
#### locl command
```azure
az aks get-credentials --resource-group=matt_learn --name=m01akscluster
az acr login --name m01aksclusterregistry
    
mvn clean package -DskipTests
docker build -t m01aksclusterregistry.azurecr.io/event-webhook .
docker push m01aksclusterregistry.azurecr.io/event-webhook

```

### cloud shell azure cli command(service deployment)
```azure
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Pod
metadata:
  name: event-webhook
  namespace: default
  labels:
    app: event-webhook
spec:
  containers:
    - image: m01aksclusterregistry.azurecr.io/event-webhook
      name: event-webhook
EOF
```

### expose external endpoint
```azure
kubectl expose pod event-webhook --type=LoadBalancer --port=80 --target-port=8080
```

### create apim to proxy the aks service api
```azure
becaus it need a https url, other ways may implement same function
```