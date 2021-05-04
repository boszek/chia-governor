# Chia Governor
Simple java application to govern a chia cryptocurrency mining.

Currently, only supported on Windows.

## Configure
Example:
```yaml
chia:
  executable: C:/Users/XXX/AppData/Local/chia-blockchain
  memory: 3800
  logs: c:/Users/XXX/.chia/logs
  temps:
    - location: "H:/temp"
      limit: 4
    - location: "E:/temp"
      limit: 1
    - location: "F:/temp"
      limit: 1
  targets:
    - "J:/Chia Plots"
```
## Build
Use maven for building executable jar
`mvn package`

## Run
`java -jar governor-0.1.jar`