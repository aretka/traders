package com.example.traders.database

enum class FixedCryptoList(val slug: String, val logoUrl: String, val amountToRound: Int = 3, val priceToRound: Int = 2) {
    BTC("bitcoin", "https://cryptologos.cc/logos/bitcoin-btc-logo.png?v=014", 5),
    ETH("ethereum", "https://cryptologos.cc/logos/ethereum-eth-logo.png?v=014", 4),
    BNB("binance-coin", "https://cryptologos.cc/logos/binance-coin-bnb-logo.png?v=014"),
    SOL("solana", "https://cryptologos.cc/logos/solana-sol-logo.png?v=014"),
    ADA("cardano", "https://cryptologos.cc/logos/cardano-ada-logo.png?v=014",2,3),
    XRP("xrp", "https://cryptologos.cc/logos/xrp-xrp-logo.png?v=014",2,3),
    DOT("polkadot", "https://cryptologos.cc/logos/polkadot-new-dot-logo.png?v=014"),
    LUNA("terra", "https://cryptologos.cc/logos/terra-luna-luna-logo.png?v=014"),
    DOGE("dogecoin", "https://cryptologos.cc/logos/dogecoin-doge-logo.png?v=014", 2, 3),
    AVAX("avalanche", "https://cryptologos.cc/logos/avalanche-avax-logo.png?v=014"),
    MATIC("polygon", "https://cryptologos.cc/logos/polygon-matic-logo.png?v=014", 2,3),
    SHIB("shiba-inu", "https://cryptologos.cc/logos/shiba-inu-shib-logo.png?v=014", 0, 7),
    LINK("chainlink", "https://cryptologos.cc/logos/chainlink-link-logo.png?v=014"),
    NEAR("near-protocol", "https://cryptologos.cc/logos/near-protocol-near-logo.png?v=014"),
    LTC("litecoin", "https://cryptologos.cc/logos/litecoin-ltc-logo.png?v=014"),
    UNI("uniswap", "https://cryptologos.cc/logos/uniswap-uni-logo.png?v=014"),
    ATOM("cosmos", "https://cryptologos.cc/logos/cosmos-atom-logo.png?v=014"),
    ALGO("algorand", "https://cryptologos.cc/logos/algorand-algo-logo.png?v=014", 2, 3),
    FTM("fantom", "https://cryptologos.cc/logos/fantom-ftm-logo.png?v=014", 2, 3),
    BCH("bitcoin-cash", "https://cryptologos.cc/logos/bitcoin-cash-bch-logo.png?v=014"),
    TRX("tron", "https://cryptologos.cc/logos/tron-trx-logo.png?v=014", 2, 4),
    XLM("stellar", "https://cryptologos.cc/logos/stellar-xlm-logo.png?v=014", 2, 3),
    ICP("internet-computer", "https://cryptologos.cc/logos/internet-computer-icp-logo.png?v=014"),
    FTT("ftx-token", "https://cryptologos.cc/logos/ftx-token-ftt-logo.png?v=014"),
    MANA("decentraland", "https://cryptologos.cc/logos/decentraland-mana-logo.png?v=014", 2, 3),
    HBAR("hedera-hashgraph", "https://cryptologos.cc/logos/hedera-hbar-logo.png?v=014", 2, 3),
    VET("vechain", "https://cryptologos.cc/logos/vechain-vet-logo.png?v=014", 1, 4),
    SAND("thesandbox", "https://cryptologos.cc/logos/the-sandbox-sand-logo.png?v=014", 2, 3),
    ETC("ethereum-classic", "https://cryptologos.cc/logos/ethereum-classic-etc-logo.png?v=014"),
    USD("US-dollars", "https://cdn-icons.flaticon.com/png/512/1140/premium/1140418.png?token=exp=1644242774~hmac=df443bdc0ba0807b1d59e4cf02d8209b");

    companion object {
        fun getEnumName(value: String): FixedCryptoList? = values().find { it.slug == value }
    }
}

