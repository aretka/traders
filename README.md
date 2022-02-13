# Trading simultor app

Table of contents
1. [ About this app. ](#desc)
2. [ Main concepts of the app. ](#main_concepts)
3. [ Features to be implemented ](#future_features)
4. [ Technologies used ](#technologies)
5. [ How to install ](#installation)

<a name="desc"></a>
## About this app
This is a trading app which allows you to simulate crypto trading with fake money.
The main idea of the app is to permit customer learn trading crypto.
Crytpo trading is extremely dangerous for new retail investors and most of them come to this area with lack of knowledge how volatile this market is.
Therefore, this trading app provides functionality to simulate trading(deposit USD, buying/sell crypto) and gain a sense of how volatile crypto market is.</br></br>
This version of the app consists of most valuable functionalities/features. 
New features are being developed and the app is being improved utilizing the cutting edge technologies.

<a name="main_concepts"></a>
## Main concepts of the app
App consists of three main screens: **watch list**, **profile** and **single crypto** screen. The app contains one activity and 

### Watch list screen
Watchlist is the main launching screen comprised of top 30 crypto list elements and each of the element consists of slug, symbol, price, and 24h precent and price change.</br>
**Main functionality points:**
  * Crypto list is represented in a recyclerView 
  * This list is fetched from binance api(only 1 request is performed).
  * Loader is shown while prices are being fetched
  * The prices are being updated utilizing websocket connection to binance server.
  * Pull-to-refresh is implemented. It refetches all crypto list


### Single crypto screen
This is some desc

### Profile screen
This is some desc

<a name="future_features"></a>
## Future features
- First
- Second

<a name="technologies"></a>
## Technologies used
* AndroidStudio kotlin
* Flipper
* Hilt DI
* Glide
* RecyclerView
* Navigation
* MVVM architecture
