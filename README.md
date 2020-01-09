# Aspiration AI Internship


## Welcome
http://www.aspiration.ai/machine-learning/internship/

Investment Bankers, CA's, Hedge Fund / Portfolio Managers, Forex traders, Commodities Analysts. 

These have been historically considered to be among the most coveted professions of all time. Yet, if one fails to keep
up with the demands of the day, one would find one's skills to be obsolete in this era of data analysis.

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


## Module 1

CSV Data Pipeline
- Read input CSV file
- Cast string dates to datetime objects
- Compute additional feature columns
- Filter rows: by value + timestamp offset (last N days)
- Calculate row statistics: maximum, minimum and mean
- Calculate incremental statistics: Daily Percentage Change 
- Calculate GroupBy(month) VWAP = sum(price*volume)/sum(volume)
- Write output CSV file 

#### Python
- [./python3/module_1/main.py](./python3/module_1/main.py) 
- [./python3/module_1/StockAnalysis.py](./python3/module_1/StockAnalysis.py)
```
cd python3
source venv/bin/activate
python3 ./module_1/main.py
```

#### Typescript
- [./typescript/module_1/index.ts](./typescript/module_1/index.ts)
- [./typescript/module_1/StockAnalysis.ts](./typescript/module_1/StockAnalysis.ts)
```
cd typescript
yarn start:module_1
```

#### Scala
- [./scala/src/main/scala/module_1/Main.scala](./scala/src/main/scala/module_1/Main.scala)
- [./scala/src/main/scala/module_1/StockAnalysis.scala](./scala/src/main/scala/module_1/StockAnalysis.scala)
- [./scala/src/main/scala/module_1/StockPrice.scala](./scala/src/main/scala/module_1/StockPrice.scala)
- [./scala/src/main/scala/module_1/StockPriceCSV.scala](./scala/src/main/scala/module_1/StockPriceCSV.scala)
- [./scala/src/main/scala/module_1/Utils.scala](./scala/src/main/scala/module_1/Utils.scala)
```
cd scala
sbt "runMain module_1.StockAnalysis"
```


## Module 2
- [./python3/module_2/module_2.ipynb](./python3/module_2/module_2.ipynb)

Data Visualization 
  - Discrete Series Plot
  - Line Charts
  - Pie Charts
  - Bar Charts
  - Histograms
  - Correlation Matrix

Calculations
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
 

## Module 3

- [./python3/module_3/module_3.ipynb](./python3/module_3/module_3.ipynb)

Fundamental Analysis using Linear Regression
- Split / Score / Predict 
- Correlation Coefficient
- Polynomial Features


#### Python
```
cd python3
source venv/bin/activate
jupyter lab module_3/module_3.ipynb
```


## Module 4

- [./python3/module_4/module_4.ipynb](./python3/module_4/module_4.ipynb)

Trade Call Prediction using Classification
- Classification Comparison
- Random Forest


#### Python
```
cd python3
source venv/bin/activate
jupyter lab module_4/module_4.ipynb
```


## Module 5

- [./python3/module_5/module_5.ipynb](./python3/module_5/module_5.ipynb)

Modern Portfolio Theory
- Annualized Volatility and Returns
- Covariance Matrix
- Monty-Carlo Simulation
- Scikit-Optimize


#### Python
```
cd python3
source venv/bin/activate
jupyter lab module_5/module_5.ipynb
```


## Module 6

- [./python3/module_6/module_6.ipynb](./python3/module_6/module_6.ipynb)

Clustering for Diversification Analysis 
- K-means clustering
- Elbow curve method

#### Python
```
cd python3
source venv/bin/activate
jupyter lab module_6/module_6.ipynb
```