# Trading simultor app

Table of contents
1. [ About this app. ](#desc)
2. [ Main concepts of the app. ](#main_concepts)
3. [ Features to be implemented ](#future_features)
4. [ Technologies used ](#technologies)
5. [ How to install ](#installation)

<a name="desc"></a>
## 1. About this app
This is a trading app which allows you to simulate crypto trading with fake money.
The main idea of the app is to permit customer learn trading crypto.
Crytpo trading is extremely dangerous for new retail investors and most of them come to this area with lack of knowledge how volatile this market is.
Therefore, this trading app provides functionality to simulate trading(deposit USD, buying/sell crypto) and gain a sense of how volatile crypto market is.</br></br>
This version of the app consists of the main and most valuable functionalities/features. 
New features are being developed and the app is being improved utilizing the cutting edge technologies.

<a name="main_concepts"></a>
## 2. Main concepts of the app
It is single activity and multiple fragment app consisting of three main screens: **watch list**, **profile** and **single crypto** screen.

### 2.1. Watch list screen
Watchlist is the main launching screen comprised of top 30 crypto list elements and each of the element consists of slug, symbol, price, and 24h precent and price change.</br>
**Main functionality points:**
  * Crypto list is represented in a recyclerView 
  * Prices are fetched from binance api(only 1 request is performed), while icons are loaded from *cryptologos.cc*.
  * Loader is shown while prices are being fetched
  * The prices are being updated utilizing websocket connection to binance server.
  * Pull-to-refresh is implemented. It refetches all crypto list.
</br></br>

 Whole crypto list            |        Pull to refresh      |  Loader 
:------------------------:|:------------------------:|:---------------------:
![allList](https://user-images.githubusercontent.com/57877668/153886934-1aafd94f-a442-4d88-ae26-1b3480754b54.gif) | ![pullToRefresh](https://user-images.githubusercontent.com/57877668/153887154-5d91ea31-7872-4e7b-9965-9e6ea1011da1.gif) | ![loader](https://user-images.githubusercontent.com/57877668/153886814-99b9d39d-c8cf-4583-a4e9-62ab6015799e.gif)

### 2.2. Single crypto screen
Single crypto screen represents 3 tabs: crypto chart, price statistics(last 24h data, ROI etc) and project description.</br>

#### 2.2.1 Chart screen tab
This tab is the main single crypto screen which shows the recent crypto price info, candle chart and buy,sell buttons.</br>
**Main functionality points:**
* Header info(latest price, and price change) is collected from websocket.
* Price chart is a custom view where crypto chart data is transfered to candle chart(no libraries used).
* Price chart data is fetched from messari.io api and passed to this view. It contains 4 options of date range(last 1/3/6/12 months).
* Buy buttom opens dialog in order to execute crypto purchase.
* Sell buttom opens dialog in order to execute crypto sell.
* Input price is validated and customer is notified if input is invalid by showing message below input field.

</br></br>
 1 month(daily candle)  |  3 months(daily candle) |  6 months(weekly candle) | 12 months(weekly candle) 
:-----------------------:|:-----------------------:|:------------------------:|:-----------------------:
<img src="https://user-images.githubusercontent.com/57877668/153913533-8ef36a19-97d0-466b-91d9-d93f0f1dfd0e.png" width="220"/> | <img src="https://user-images.githubusercontent.com/57877668/153913569-2aca0494-6d5c-4856-a0a7-96384091c326.png" width="220"/> | <img src="https://user-images.githubusercontent.com/57877668/153913591-18110e02-8b2c-4eb6-b53b-1c274b0d79cd.png" width="220"/> | <img src="https://user-images.githubusercontent.com/57877668/153913630-ef04ac6c-7597-4217-89a0-cb6fb9120a73.png" width="220"/>

#### 2.2.2 Price statistics tab
#### 2.2.3 Project description tab

### 2.3. Profile screen
This is some desc

<a name="future_features"></a>
## 3. Future features
- First
- Second

<a name="technologies"></a>
## Technologies used
* Tools used
  * AndroidStudio kotlin
  * Flipper<br/>
* Android specific
  * Hilt DI
  * Glide
  * RecyclerView
  * Navigation
  * MVVM architecture
  * Shared preferences
  * Room

## How to install?
The app has not been released to google play yet, but it will be released in near future.</br></br>
The only way to to launch this app is to clone this repo and build project using android studio.
