# Scala

## [Module 1](MODULE_1.md)

CSV Data Pipeline
- Read input CSV file
- Cast string dates to datetime objects
- Compute additional feature columns
- Filter rows: by value + timestamp offset (last N days)
- Calculate row statistics: maximum, minimum and mean
- Calculate incremental statistics: Daily Percentage Change 
- Calculate GroupBy(month) VWAP = sum(price*volume)/sum(volume)
- Write output CSV file 

### Install
```
sbt clean
sbt compile
sbt run
sbt test

sbt 'show discoveredMainClasses'
```

### Module 1
```
sbt "runMain module_1.StockAnalysis"
```