# Typescript

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

#### Install
```
yarn
yarn start
```

### Module 1
```
yarn start:module_1
```