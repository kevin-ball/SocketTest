

public enum ServerMessageType {
    RequestNewGame,
    AckNewGameRequest,
    CancelGameRequest,
    StartNewGame,
    StartPlayerTurn,
    PlayerReady,
    DiceRoll,
    MovePlayerTo,
    PayMoneyTo,
    GetMoneyFrom,
    PickedCard,
    BuyProperty,
    MortgageProperty,
    UnmortgageProperty,
    BuyHouses,
    SellHouses,
    SellProperty,
    GoToJail,
    GetOutOfJail,
    OfferTrade,
    AuctionProperty,
    QuitGame,
    GoBankrupt,
    StatusPrint,
    Other;

    String[] strings = {

            "Request New Game",
            "Acknowledge New Game Request",
            "Cancel New Game Request",
            "Start New Game",
            "Start Player Turn",
            "Player Ready",
            "Player Rolled",
            "Move Player To",
            "Pay Money To",
            "GetMOneyFrom",
            "Picked Card",
            "Buy Property",
            "Mortgage Property",
            "Unmortgage Property",
            "Buy Houses",
            "Sell Houses",
            "Sell Property",
            "Go To Jail",
            "Get Out of Jail",
            "Offer Trade",
            "Auction Property",
            "Quit Game",
            "Go Bankrupt",
            "Status Print",
            "Other" };

    public String getName() {
        return strings[this.ordinal()];
    }
}
