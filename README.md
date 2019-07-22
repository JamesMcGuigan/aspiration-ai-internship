# Aspiration AI Internship


## Welcome
[http://www.aspiration.ai/machine-learning/internship/]

Investment Bankers . CA's . Hedge Fund / Portfolio Managers . Forex traders . Commodities Analysts. These have been historically considered to be among the most coveted professions of all time. Yet, if one fails to keep up with the demands of the day, one would find one's skills to be obsolete in this era of data analysis.

Data Science has inarguably been the hottest domain of the decade, asserting its need in every single sphere of corporate life. It was not long agowhen we discovered the massive potential of incorporating ML/AI in the financial world. Now, the very idea of the two being disjointed sounds strange.

Data Science has been incremental in providing powerful insights ( which people didn't even know existed ) and helped massively increase the efficiency, helping everyone from a scalp trader to a long term debt investor. Accurate predictions, unbiased analysis, powerful tools that run through millions of rows of data in the blink of an eye have transformed the industry in ways we could've never imagined.

The following program is designed to both test your knowledge and to give you the feel and experience of a real world financial world - data science problem. 


## Setup

#### Python
```
cd python3
./requirements.sh
source venv/bin/activate
./clean.sh
./main.sh
```

#### Typescript
```
cd typescript
yarn
yarn clean
yarn start
```

#### Scala
```
cd scala
sbt clean
sbt compile
sbt run
```


## [Module 1](MODULE_1.md)

- Read input CSV file
- Cast string dates to datetime objects
- Compute additional feature columns
- Filter rows: by value + timestamp offset (last N days)
- Calculate row statistics: maximum, minimum and mean
- Calculate incremental statistics: Daily Percentage Change 
- Calculate GroupBy(month) VWAP = sum(price*volume)/sum(volume)
- Write output CSV file 

#### Python
```
cd python3
source venv/bin/activate
python3 ./module_1/main.py
```

#### Typescript
```
cd typescript
yarn start:module_1
```

#### Scala
```
cd scala
sbt "runMain module_1.StockAnalysis"
```


## [Module 2](MODULE_2.md)

- Data Visualization 
  - Discrete Series Plot
  - Line Charts
  - Pie Charts
  - Bar Charts
  - Histograms
  - Correlation Matrix

- Calculations
  - Rolling Volatility
  - Beta vs Index
  - Simple Moving Average
  - Bollinger Bands
  

#### Python
```
cd python3
source venv/bin/activate
jupyter lab module_2/module_2.ipynb
```
 
