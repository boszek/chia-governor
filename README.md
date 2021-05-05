# Chia Governor

Simple java application to govern a chia cryptocurrency mining.

Working on both linux (tested Ubuntu) and Windows.

The idea is to control running processes so that:

- No process will go beyond drive free space
- Can set limit per drive
- Only one Chia Phase 1 can be running simultanously on one drive

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
