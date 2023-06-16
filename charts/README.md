# Sample Helm Charts

This provides the following:

- a simple service deployment which leverages the image built with gradle
- a wiremock helm chart taken from [wiremock source](https://github.com/wiremock/helm-charts/tree/master)

## Helpful Commands

### Remove a helm installation

```shell
helm list --short --filter wiremock | xargs helm uninstall
```

### Install with random name

```shell
helm install ./wiremock --generate-name
```