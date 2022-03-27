package dev.mediamod.utils

fun <T : Any> Iterable<T?>.firstNotNullOrNull(): T? {
    return filterNotNull().firstOrNull()
}