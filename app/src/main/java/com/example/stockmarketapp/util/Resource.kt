package com.example.stockmarketapp.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String): Resource<T>(message = message)
    class Loading<T>(val isLoading: Boolean = true): Resource<T>()
}