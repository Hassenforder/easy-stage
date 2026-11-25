package fr.uha.hassenforder.easy.stage.model

enum class SeatType {
    NO,
    PATH,               // path in a chamber
    BROKEN,             // broken place cannot be used
    WHEELCHAIR,         // wheelchair place
    VIP_CHAIR,          // vip chair
    PREMIUM_CHAIR,      // premium chair
    REGULAR_CHAIR,      // regular chair
    ;

    fun isAChair() : Boolean {
        return this == WHEELCHAIR || this == VIP_CHAIR || this == PREMIUM_CHAIR || this == REGULAR_CHAIR
    }
}
