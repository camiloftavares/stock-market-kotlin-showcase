package com.example.stockmarketapp.data.mapper

import com.example.stockmarketapp.data.remote.dto.IntraDayInfoDto
import com.example.stockmarketapp.domain.model.IntraDayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun IntraDayInfoDto.toModel(): IntraDayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)

    return IntraDayInfo(
        date = localDateTime,
        close = close
    )
}