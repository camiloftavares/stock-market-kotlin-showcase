package com.example.stockmarketapp.data.mapper

import com.example.stockmarketapp.data.local.CompanyListingEntity
import com.example.stockmarketapp.data.remote.dto.CompanyInfoDto
import com.example.stockmarketapp.domain.model.CompanyInfo
import com.example.stockmarketapp.domain.model.CompanyListing

fun CompanyListingEntity.toModel(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyListing.toEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyInfoDto.toModel() : CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: "",
    )
}