package com.example.stockmarketapp.presentation.company_listings

sealed class CompanyListEvent {
    data object Refresh: CompanyListEvent()
    data class OnSearchQueryChange(val query: String): CompanyListEvent()
}