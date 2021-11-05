package ru.sber.rdbms

class FaultyDataException(private val msg: String): Exception(msg)
