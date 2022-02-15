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
Single crypto screen represents 3 tabs: crypto chart, price statistics(last 24h data, ROI etc) and project description.</br></br>
<img src="https://user-images.githubusercontent.com/57877668/154049320-c46bd625-87d6-439b-b52b-f8b69df64dc7.gif" width="300"/></br>

#### 2.2.1 Chart screen tab
This tab is the main single crypto screen which shows the recent crypto price info, candle chart and buy,sell buttons.</br>
**Main functionality points:**
* Header info(latest price, and price change) is collected from websocket.
* Price chart is a custom view where crypto chart data is transfered to candle chart(no libraries used).
* Price chart data is fetched from messari.io api and passed to this view. It contains 4 options of date range(last 1/3/6/12 months).
* Buy buttom opens dialog in order to execute crypto purchase. This dialog shows the current crypto price, USD balance, input field 
* Sell buttom opens dialog in order to execute crypto sell.
* Input price is validated and customer is notified if input is invalid by showing message below input field.
</br></br>

 1 month(daily candle)  |  3 months(daily candle) |  6 months(weekly candle) | 12 months(weekly candle) 
:-----------------------:|:-----------------------:|:------------------------:|:-----------------------:
<img src="https://user-images.githubusercontent.com/57877668/153913533-8ef36a19-97d0-466b-91d9-d93f0f1dfd0e.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/153913569-2aca0494-6d5c-4856-a0a7-96384091c326.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/153913591-18110e02-8b2c-4eb6-b53b-1c274b0d79cd.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/153913630-ef04ac6c-7597-4217-89a0-cb6fb9120a73.png" width="230"/>
</br>

 Buy dialog  |  Sell dialog  
:-----------------------:|:-----------------------:
<img src="https://user-images.githubusercontent.com/57877668/154036156-aba46310-50a9-4aad-8095-506693668a7c.png" width="250"/> | <img src="https://user-images.githubusercontent.com/57877668/154036539-9d0b5f9b-863c-41e5-92bc-112914ce551e.png" width="250"/>
</br>

 Empty message  |  Insufficient balance message | Price too low message | Valid input(no message)
:-----------------------:|:-----------------------:|:-----------------------:|:-----------------------:
<img src="https://user-images.githubusercontent.com/57877668/154037457-84c31867-3365-41a6-8cbc-2e407f5ec281.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/154037965-be4dc8bb-acb5-4850-91ce-5c9090a63ec9.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/154037554-44fe6e3b-9a32-446f-8308-f35c8b662425.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/154038127-30289039-1a88-46ab-ac38-bcced0c723b3.png" width="230"/>

#### 2.2.2 Price statistics tab
Price statistics tab represents essential statistics points such as market dominance, 1h/24h volume, ATH data, ROI and others.
**Main functionality points:**
* Header of this screen comprises of immutable data. Icon, latest price stats(latest price, 24h/change), market dominance, market cap, 1h/24h volume.
* Content area consists of 4 expandable cards: last 1h/24h data(high, low, open, close, volume), ATH data(price, date, days since, perecent down), ROI data(return on investment).
</br></br>.

<img src="https://user-images.githubusercontent.com/57877668/154047257-43509813-7805-4cd3-afeb-fadf4f93c43b.gif" width="300"/>

#### 2.2.3 Project description tab
Description tab simply shows Project info and pre-history of the coin. Data comes from messari api.</br>
<img src="https://user-images.githubusercontent.com/57877668/154049367-e833d09d-a0d2-4a0a-ab7c-47ad98ecebc2.gif" width="300"/>

### 2.3. Profile screen
Profile screen contains 2 tabs portfolio and transaction history tabs.</br>
<img src="https://user-images.githubusercontent.com/57877668/154051857-c0dda0fb-1bbb-4901-988c-a1a0baa63af7.gif" width="300"/>

#### 2.3.1 Portfolio tab
Portfolio tab comprises of 3 parts: total balance and chart, button area(), crypto list.</br>
**Main functionality points:**
* Crypto portfolio the data comes from room local databse and prices from binance api.
* On viewModel init chart data is calculated by making requests from binance to get live prices and calculates crypto balances in usd.
* Maximum crypto elements in chart are 6. If exceeded, 5 are shown and the rest is labelled as others.
* If no crypto is present, chart is empty and message of *"Deposit to begin trading"* insead of recycler view.
* Deposit button opens dialog where customer is allowed to deposit US dollars.
* Reset balance button simply resets all data by erasing all portfolio data.
* Cryto list below button area is created using recyclerView. It contains couple viewHolders: header and crypto element.

</br></br>

 Whole screen  | Empty crypto list  |  Deposit usecase | Balance reset 
:-------------:|:------------------:|:----------------:|:------------:
<img src="https://user-images.githubusercontent.com/57877668/154063406-dedc3636-eff1-4e9a-aa73-a60d13718af2.gif" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/154078787-0ab73d6a-4143-4e63-a0de-4c9f1b0f44b6.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/154078133-948cfe9e-eec2-4a94-b151-365aa4bf8c57.gif" width="230"> | <img src="https://user-images.githubusercontent.com/57877668/154078155-4808d26d-ef84-449b-b0d4-5327790d343d.gif" width="230">

</br></br>
**Chart different cases**
Empty chart  | Item count <= 5  |  Item count > 5
:-------------:|:------------------:|:----------------:
<img src="https://user-images.githubusercontent.com/57877668/154079215-d33e7e74-b650-4900-9476-b769efbb14fa.png" width="300"/> | <img src="https://user-images.githubusercontent.com/57877668/154079382-5aa73e73-45ca-45b6-9ba5-ec52fd58ae24.png" width="300"/> | <img src="https://user-images.githubusercontent.com/57877668/154079541-d2ea4983-b12b-4c50-954f-0f81ac08a7eb.png" width="300"> 

#### 2.3.2 Transaction tab
This tab shows a list of executed transactions stored in room database.</br>
**Main functionality points:**
* Whole screen is a single recyclerView consisting 3 different viewHolders: header, deposit and purchase/sell holders.
* On init the list is fetched from local database and inserted into recyclerView.
* Message of *"No transactions yet"* is displayed if list is empty.
* Header contains title and erase button which erases all list.
* Each element contains symbol, slug, icon, date, amount, amount in usd, last price.
</br></br>

Whole transaction page  | OnEraseAllTransactions
:----------------------:|:------------------:
<img src="https://user-images.githubusercontent.com/57877668/154086157-49462d25-7582-4f30-8916-213b563bf060.gif" width="300"/> | <img src="https://user-images.githubusercontent.com/57877668/154086204-f872f4b2-0460-4077-83f1-7a5f35cf7970.gif" width="300"/> 

<a name="future_features"></a>
## 3. Future features
- Main watchlist sorting by name, by price change.
- Registration with google account.
- Implementing favourites list.
- Confirmation dialgos such as confirm purchase, confirm deposit etc.
- Sort portfolio list
- Balance change over time screen to track how your crypto list changed over time.

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
