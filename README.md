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
The main idea of the app is to permit customer to learn trading crypto.
Crypto trading is extremely dangerous for new retail investors and most of them come to this area with lack of knowledge how volatile this market is.
Therefore, this trading app provides functionality to simulate trading(deposit USD, buying/sell crypto) and gain a sense of how volatile crypto market is.</br></br>
This version of the app consists of the main and most valuable functionalities/features.
New features are being developed and the app is being improved utilizing the cutting-edge technologies.

<a name="main_concepts"></a>
## 2. Main parts of the app
It is single activity and multiple fragment app consisting of three main screens: **watch list**, **profile** and **crypto details** screen.

### 2.1. Watch list screen
Watchlist is the main launching screen comprised of top 30 crypto list elements and each of the element consists of slug, symbol, price, and 24h precent and price change.</br>
**Main functionality points:**
  * Crypto list is represented in a recyclerView
  * Prices are fetched from binance api(only 1 request is performed), while icons are loaded from *cryptologos.cc*.
  * Loader is shown while prices are being fetched
  * The prices are being updated utilizing websocket connection to binance server.
  * Pull-to-refresh is implemented. It refetches all crypto list.
  * There are 2 sorting options: by name, by price change. 
  * One filter option - extract favourite list
  * Sort and filter preferences are stored in datastore and retreived when app is relaunched.
</br></br>

 List sort and filtration |        Pull to refresh   |  Loader
:------------------------:|:------------------------:|:---------------------:
![allList](https://user-images.githubusercontent.com/57877668/166565606-a8022b71-2661-4bce-8548-d10ba61ce344.gif) | ![pullToRefresh](https://user-images.githubusercontent.com/57877668/166565597-837a43ee-ba26-49b0-92e5-291f1ba74f1f.gif) | ![loader](https://user-images.githubusercontent.com/57877668/166565467-9ed97da0-2b27-449d-a64a-1ffb1dcce390.gif)

### 2.2. Single crypto screen
Single crypto screen represents 3 tabs: crypto chart, price statistics(last 24h data, ROI etc) and project description. There is a favourite toggle button on the top which saves chose crypto to room or removes it.</br></br>

<img src="https://user-images.githubusercontent.com/57877668/166567064-a4d0f46f-f4f4-4db3-a082-66eada23f800.gif" width="300"/></br>

#### 2.2.1.Chart screen tab
This tab is the main single crypto screen which shows the recent crypto price info, candle chart and buy,sell buttons.</br>
**Main functionality points:**
* Header info(latest price, and price change) is collected from websocket.
* Price chart is a custom view where crypto chart data is transfered to candle chart(no libraries used).
* Candle chart listens to long presses and show price, date, prace change of that candle if pressed for at least 0.25s.
* Price chart data is fetched from messari.io api and passed to this view. It contains 4 options of date range(last 1/3/6/12 months).
* Buy buttom opens dialog in order to execute crypto purchase. This dialog shows the current crypto price, USD balance, input field
* Sell buttom opens dialog in order to execute crypto sell.
* Input price is validated and customer is notified if input is invalid by showing message below input field.
* Transaction is executed only when confirmed clicked on confirmation dialog.
</br></br>

 1 month(daily candle)  |  3 months(daily candle) |  6 months(weekly candle) | 12 months(weekly candle)
:-----------------------:|:-----------------------:|:------------------------:|:-----------------------:
<img src="https://user-images.githubusercontent.com/57877668/170682983-1863ca43-7604-430c-a6a7-6cb6669d0698.gif" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/170682699-f8f18723-dcfc-46ca-9382-3424361d194e.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/170682745-c4369fd5-2ec6-45ae-b6b4-389c607f8227.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/170682616-e2795345-1de7-4c24-98e2-76a8e1783a6e.png" width="230"/>
</br>

 Buy dialog  |  Sell dialog  
:-----------------------:|:-----------------------:
<img src="https://user-images.githubusercontent.com/57877668/166566374-7b4cde2c-147e-4473-81a9-3663294b1ff7.gif" width="300"/> | <img src="https://user-images.githubusercontent.com/57877668/166566369-c1ec0d83-9e77-4ea0-8f79-7d337dec48fc.gif" width="300"/>
</br>

 Empty message  |  Insufficient balance message | Price too low message | Valid input(no message)
:-----------------------:|:-----------------------:|:-----------------------:|:-----------------------:
<img src="https://user-images.githubusercontent.com/57877668/154037457-84c31867-3365-41a6-8cbc-2e407f5ec281.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/154037965-be4dc8bb-acb5-4850-91ce-5c9090a63ec9.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/154037554-44fe6e3b-9a32-446f-8308-f35c8b662425.png" width="230"/> | <img src="https://user-images.githubusercontent.com/57877668/154038127-30289039-1a88-46ab-ac38-bcced0c723b3.png" width="230"/>

#### 2.2.2.Price statistics tab
Price statistics tab represents essential statistics points such as market dominance, 1h/24h volume, ATH data, ROI and others.</br>
**Main functionality points:**
* Header of this screen comprises of immutable data. Icon, latest price stats(latest price, 24h/change), market dominance, market cap, 1h/24h volume.
* Content area consists of 4 expandable cards: last 1h/24h data(high, low, open, close, volume), ATH data(price, date, days since, perecent down), ROI data(return on investment).
</br></br>.

<img src="https://user-images.githubusercontent.com/57877668/154047257-43509813-7805-4cd3-afeb-fadf4f93c43b.gif" width="300"/>

#### 2.2.3.Project description tab
Description tab simply shows Project info and pre-history of the coin. Data comes from messari api.</br>
<img src="https://user-images.githubusercontent.com/57877668/154049367-e833d09d-a0d2-4a0a-ab7c-47ad98ecebc2.gif" width="300"/>

### 2.3. Profile screen
Profile screen contains 2 tabs portfolio and transaction history tabs.</br>
<img src="https://user-images.githubusercontent.com/57877668/154051857-c0dda0fb-1bbb-4901-988c-a1a0baa63af7.gif" width="300"/>

#### 2.3.1.Portfolio tab
Portfolio tab comprises of 3 parts: total balance and chart, button area(), crypto list.</br>
**Main functionality points:**
* Crypto portfolio the data comes from room local databse and prices from binance api.
* On viewModel init chart data is calculated by making requests from binance to get live prices and calculates crypto balances in usd.
* Maximum crypto elements in chart are 6. If exceeded, 5 are shown and the rest is labelled as others.
* If no crypto is present, chart is empty and message of *"Deposit to begin trading"* insead of recycler view.
* Deposit button opens dialog where customer is allowed to deposit US dollars.
* Reset balance button simply resets all data by erasing all portfolio data.
* Cryto list below button area is created using recyclerView. It contains couple viewHolders: header and crypto element.
* Confrimation dialog are shown on deposit or reset balance click.
* Transaction is executed only dialogs are confirmed. 

</br></br>

 Whole screen  | Empty crypto list  |  Deposit&reset balance usecase |
:-------------:|:------------------:|:----------------:|
<img src="https://user-images.githubusercontent.com/57877668/154063406-dedc3636-eff1-4e9a-aa73-a60d13718af2.gif" width="260"/> | <img src="https://user-images.githubusercontent.com/57877668/154078787-0ab73d6a-4143-4e63-a0de-4c9f1b0f44b6.png" width="260"/> | <img src="https://user-images.githubusercontent.com/57877668/166567689-fefe1228-c67f-499e-a854-42606b76b3a9.gif" width="260">

</br></br>
**Chart different cases**
Empty chart  | Item count <= 5  |  Item count > 5
:-------------:|:------------------:|:----------------:
<img src="https://user-images.githubusercontent.com/57877668/154079215-d33e7e74-b650-4900-9476-b769efbb14fa.png" width="300"/> | <img src="https://user-images.githubusercontent.com/57877668/154079382-5aa73e73-45ca-45b6-9ba5-ec52fd58ae24.png" width="300"/> | <img src="https://user-images.githubusercontent.com/57877668/154079541-d2ea4983-b12b-4c50-954f-0f81ac08a7eb.png" width="300">

#### 2.3.2.Transaction tab
This tab shows a list of executed transactions stored in room database.</br>
**Main functionality points:**
* Whole screen is a single recyclerView consisting 3 different viewHolders: header, deposit and purchase/sell holders.
* On init the list is fetched from local database and inserted into recyclerView.
* Message of *"No transactions yet"* is displayed if list is empty.
* Header contains title and erase button which erases all list. It contains confirmation dialog in case of missclick.
* Each element contains symbol, slug, icon, date, amount, amount in usd, last price.
</br></br>

Whole transaction page  | OnEraseAllTransactions
:----------------------:|:------------------:
<img src="https://user-images.githubusercontent.com/57877668/166568315-63a81511-3223-46ea-aec9-0e7b0068e607.gif" width="300"/> | <img src="https://user-images.githubusercontent.com/57877668/166567761-af20d1a9-22d0-41ea-b575-aad3f17584ae.gif" width="300"/>

<a name="future_features"></a>
## 3. Future features
- :heavy_check_mark: Main watchlist sorting by name, by price change.
- Registration with google account.
- :heavy_check_mark: Implementing favourites list.
- :heavy_check_mark: Confirmation dialgos such as confirm purchase, confirm deposit etc.
- :heavy_check_mark: Sort portfolio list
- Balance change over time screen to track how your crypto list changed over time.

<a name="technologies"></a>
## 4. Technologies used
* IDE's, app debuggers
  * Android Studio - it was chosen as development environment since it is the official integrated development environment (IDE) for Google's Android operating system. Kotlin was chosen as a language(google preferred langugage is kotlin instead of java since 2019).
  * Flipper - it has been used as a platform for debugging Android app, check network requests, observe room database, shared-preferences etc. <br/>
* Android specific
  * Hilt DI - a technique widely used in OOP for better reusability, ease of refactoring and ease of testing has been utilized with hilt.
  * Glide - fast and efficient open source media management and image loading framework for Android has been applied for icon fetching and catching.
  * RecyclerView - effiecient way of displaying large sets of data. Single and multi-type viewHolder recycler views has been utilized in the project.
  * Navigation - bottom multistack navigation was accomplished in this app by creating couple graphs and implementing in a parent nav_graph.
  * MVVM architecture - a pattern that suggests separating the data presentation logic(Views or UI) from the core business logic part of the application.
  * <img src="https://user-images.githubusercontent.com/57877668/154479959-ee7aec97-f36c-41c6-ad8d-987947f39aa5.png" width="500" />
  * Shared preferences - key-value pairs has been used in the early version of the app but now they are removed and replaced with room.
  * Room - a local database designated to store data on your phone has been used for saving crypto porftolio and transaction history.
  * Retrofit with OKHTTP
  * Websocket - a persistent connection between a client and server which provides full-duplex communication channels over a single TCP connection. It was uesd for live crypto data fetching.
  * Data store was utilized to store filter and sort preferences.

<a name="installation"></a>
## 5. How to install?
The app has not been released to google play yet, but it will be released in near future.</br></br>
The only way to to launch this app is to clone this repo and build project using Android studio or from
<a href="https://developer.android.com/studio/build/building-cmdline">CMD</a>.
