package ru.mrlargha.stemobile.data

open class Result<T> {
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=" + this.data.toString() + "]"
            is Error -> "Error[exception=" + this.errorString + "]"
            else -> "Undefined type!"
        }
    }

    // Success sub-class
    class Success<T>(val data: T) : Result<Any?>()

    // Error sub-class
    class Error(val errorString: String) : Result<Any?>()
}