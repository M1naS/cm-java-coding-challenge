# Currency Exchange Service
##### [Crewmeister Test Assignment - Java Backend Developer](https://github.com/crewmeister/java-coding-challenge)
###### 
A RESTful microservice that provides foreign exchange rates by consuming the Bundesbank API.
### Prerequisites
* Java 11
* Maven 3.x

### How to Run
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/M1naS/cm-java-coding-challenge.git](https://github.com/M1naS/cm-java-coding-challenge.git)
    cd java-coding-challenge
    ```
2.  **Build and Run:**
    ```bash
    mvn spring-boot:run
    ```
3.  **Verify:**
    The application will start on `http://localhost:8080` and will be ready after the `Cache warmed!` message.
---

### Technology Overview
- **Jackson:** was used for `json` parsing, also used it for `csv` *for convenience*
  - Used the ``jackson-module-afterburner`` to boost performance
- **Caffeine:** was used for caching, all the API use cache and the ``getExchangeRates`` are cached on startup
  - To control how many observations are cached on startup uncomment and set ``external.api.bundesbank.data-path-api-limit`` to any number, by default it will cache everything, it will take a few seconds and then ``Cache warmed!`` is shown which means everything is ready

### Test
- Run 
    ```bash
    mvn test
    ```
- **To Test the APIs, included are `.bru` files for [Bruno](https://www.usebruno.com/)**

### API Documentation

#### 1. Get All Currencies
#### `GET /api/v1/bundesbank/all/currencies`
Gets all currencies, returning their codes and names

**Query Params:**

| Name   | Type     | Value        |
|:-------|:---------|--------------|
| `lang` | `String` | `en` or `de` |

**Request Body:**
```json
{
  ...
    {
      "name": "United Arab Emirates dirham",
      "id": "AED"
    },
  ...
}
```
---
#### 2. Get Available Currencies
#### `GET /api/v1/bundesbank/available/currencies`
Gets available currencies, returning their codes

**Request Body:**
```json
{
  ...
    [
      "AUD",
      "BGN",
      "BRL",
      "CAD",
      ...
    ],
  ...
}
```
---
#### 3. Get Exchange Rates
#### `GET /api/v1/bundesbank/exchange-rates`
Gets all available exchange rates for all dates

**Request Body:**
```json
{
  [
  ...
    {
      "date": "2026-01-29",
      "rates": [
        {
          "code": "AUD",
          "rate": 1.6935
        },
        {
          "code": "BRL",
          "rate": 6.2011
        },
        {
          "code": "CAD",
          "rate": 1.6186
        },
        ...
    }
  ]
  ...
}
```
---
#### 4. Get Exchange Rate by Date
#### `GET /api/v1/bundesbank/exchange-rates`
Gets exchange rates by date

**Query Params:**

| Name   | Type   | Value      |
|:-------|:-------|------------|
| `date` | `Date` | `YYYY-MM-DD` |

**Request Body:**
```json
{
  ...
  {
      "date": "2026-01-29",
      "rates": [
        {
          "code": "AUD",
          "rate": 1.6935
        },
        {
          "code": "BRL",
          "rate": 6.2011
        },
        {
          "code": "CAD",
          "rate": 1.6186
        },
    ...
  }
}
```
---
#### 5. Get Converted Foreign Exchange Amount
#### `GET /api/v1/bundesbank/convert`
Gets converted amount calculated depending on date, currencyCode and amount 

**Query Params:**

| Name           | Type     | Value                               |
|:---------------|:---------|-------------------------------------|
| `date`         | `Date`   | `YYYY-MM-DD`                        |
| `currencyCode` | `String` | `USD` or `JPY` or any currency code |
| `amount`       | `Number` | `10`                                |

**Request Body:**
```json
{
  ...
    {
      "date": "2026-01-29",
      "currencyCode": "USD",
      "amount": 10,
      "converted": 8.36
    }
  ...
}
```

---
Data is fetched from [Bundesbank Daily Exchange Rates](https://www.bundesbank.de/dynamic/action/en/statistics/time-series-databases/time-series-databases/759784/759784?statisticType=BBK_ITS&listId=www_sdks_b01012_3&treeAnchor=WECHSELKURSE)