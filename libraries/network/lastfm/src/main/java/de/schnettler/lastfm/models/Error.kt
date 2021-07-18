package de.schnettler.lastfm.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: Int,
    val message: String
) {
    fun asError() = when (error) {
        2 -> Errors.SERVICE
        3 -> Errors.METHOD
        4 -> Errors.AUTHENTICATION
        5 -> Errors.FORMAT
        6 -> Errors.PARAMETERS
        7 -> Errors.RESOURCE
        8 -> Errors.OPERATION
        9 -> Errors.SESSION
        10 -> Errors.API_KEY
        11 -> Errors.OFFLINE
        12 -> Errors.SUB_ONLY
        13 -> Errors.SIGNATURE
        14 -> Errors.TOKEN
        15 -> Errors.STREAMING
        16 -> Errors.UNAVAILABLE
        17 -> Errors.LOGIN
        18 -> Errors.TRIAL
        19 -> Errors.UNKNOWN2
        20 -> Errors.CONTENT
        21 -> Errors.MEMBERS
        22 -> Errors.FANS
        23 -> Errors.NEIGHBOURS
        24 -> Errors.PEAK
        25 -> Errors.RADIO
        26 -> Errors.SUSPENDED
        27 -> Errors.DEPRECATED
        29 -> Errors.RATE_LIMIT
        else -> Errors.UNKNOWN
    }
}

enum class Errors(private val num: Long, val title: String, val description: String) {
    UNKNOWN(1, "Unknown Error", "This error does not exist"),
    SERVICE(2, "Invalid service", "This service does not exist"),
    METHOD(3, "Invalid Method", "No method with that name in this package"),
    AUTHENTICATION(4, "Authentication Failed", "You do not have permissions to access the service"),
    FORMAT(5, "Invalid format", "This service doesn't exist in that format"),
    PARAMETERS(6, "Invalid parameters", "Your request is missing a required parameter"),
    RESOURCE(7, "Invalid resource", "Invalid resource specified"),
    OPERATION(8, "Operation failed", "Most likely the backend service failed. Please try again."),
    SESSION(9, "Invalid session key", "Please re-authenticate"),
    API_KEY(10, "Invalid API key", "You must be granted a valid key by last.fm"),
    OFFLINE(11, "Service Offline", "This service is temporarily offline. Try again later."),
    SUB_ONLY(12, "Subscribers Only", "This station is only available to paid last.fm subscribers"),
    SIGNATURE(13, "Invalid signature", "Invalid method signature supplied"),
    TOKEN(14, "Unauthorized Token", "This token has not been authorized"),
    STREAMING(15, "Streaming not possible", "This item is not available for streaming"),
    UNAVAILABLE(
        16,
        "Service unavailable",
        "The service is temporarily unavailable, please try again."
    ),
    LOGIN(17, "Login", "User requires to be logged in"),
    TRIAL(18, "Trial Expired", "This user has no free radio plays left. Subscription required."),
    UNKNOWN2(19, "Unknown Error", "This error does not exist"),

    // RADIO
    CONTENT(20, "Not Enough Content", "There is not enough content to play this station"),
    MEMBERS(21, "Not Enough Members", "This group does not have enough members for radio"),
    FANS(22, "Not Enough Fans", "This artist does not have enough fans for for radio"),
    NEIGHBOURS(23, "Not Enough Neighbours", "There are not enough neighbours for radio"),
    PEAK(24, "No Peak Radio", "This user is not allowed to listen to radio during peak usage"),
    RADIO(25, "Radio Not Found", "Radio station not found"),

    SUSPENDED(
        26,
        "API Key Suspended",
        "This application is not allowed to make requests to the web services"
    ),
    DEPRECATED(27, "Deprecated", "This type of request is no longer supported"),
    RATE_LIMIT(
        29,
        "Rate Limit Exceded",
        "Your IP has made too many requests in a short period, exceeding our API guidelines"
    );

    override fun toString(): String = "[$num] $name: $title - $description"

    fun isRecoverable() = this == OFFLINE || this == UNAVAILABLE
}