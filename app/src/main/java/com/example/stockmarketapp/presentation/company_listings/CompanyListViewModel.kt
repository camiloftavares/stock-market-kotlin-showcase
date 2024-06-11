package com.example.stockmarketapp.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarketapp.domain.repository.StockRepository
import com.example.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListViewModel @Inject constructor (
    private val stockRepository: StockRepository
) : ViewModel() {
    var state by mutableStateOf(CompanyListState())

    private var searchJob: Job? = null

    init {
        getCompanyList()
    }

    fun onEvent(event: CompanyListEvent) {
        when (event) {
            is CompanyListEvent.Refresh -> {
                getCompanyList(fetchFromRemote = true)
            }
            is CompanyListEvent.OnSearchQueryChange -> {
                state = state.copy(
                    searchQuery = event.query
                )
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getCompanyList()
                }
            }
        }
    }

    private fun getCompanyList(
        query: String = state.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            stockRepository.getCompanyList(fetchFromRemote, query)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { list ->
                                state = state.copy(
                                    companies = list
                                )
                            }
                        }

                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}