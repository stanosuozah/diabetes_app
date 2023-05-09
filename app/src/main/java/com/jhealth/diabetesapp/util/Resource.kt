package com.tutorial.messageme.data.utils
sealed class Resource<T>(val data: T? = null, val msg: String? = null) {
    class Successful<T>(data: T?) : Resource<T>(data = data)
    class Failure<T>(msg: String?) : Resource<T>(msg = msg)
    class Loading<T>() : Resource<T>()
}