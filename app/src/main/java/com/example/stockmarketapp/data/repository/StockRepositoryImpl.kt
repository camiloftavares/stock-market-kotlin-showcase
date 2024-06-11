package com.example.stockmarketapp.data.repository

import com.example.stockmarketapp.data.csv.CSVParser
import com.example.stockmarketapp.data.csv.CompanyListParser
import com.example.stockmarketapp.data.local.StockDao
import com.example.stockmarketapp.data.local.StockDatabase
import com.example.stockmarketapp.data.mapper.toEntity
import com.example.stockmarketapp.data.mapper.toModel
import com.example.stockmarketapp.data.remote.StockApi
import com.example.stockmarketapp.domain.model.CompanyInfo
import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.domain.model.IntraDayInfo
import com.example.stockmarketapp.domain.repository.StockRepository
import com.example.stockmarketapp.util.Resource
import com.opencsv.CSVReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntraDayInfo>
): StockRepository {

    private val dao = db.dao

    override fun getCompanyList(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localList = dao.searchCompanyList(query)
            emit(Resource.Success(
                data = localList.map {
                    it.toModel()
                }
            ))

            val isDbEmpty = localList.isEmpty() && query.isBlank()
            val shouldLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteList = try {
                val response = api.getListings()
                companyListParser.parse(response.byteStream())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data"))
                null
            } catch (e: IOException) {
                emit(Resource.Error(message = "Couldn't load data"))
                null
            }
            remoteList?.let { companyList ->
                dao.clearCompanyList()
                dao.insertCompanyList(
                    companyList.map {
                        it.toEntity()
                    }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyList("")
                        .map { it.toModel() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntraDayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol = symbol)
            val results = intradayInfoParser.parse(response.byteStream())
            Resource.Success(results)
        } catch (e: IOException) {
            Resource.Error(message = "Couldn't load intraday info")
        } catch (e: HttpException) {
            Resource.Error(message = "Couldn't load intraday info")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol = symbol)
            Resource.Success(result.toModel())
        } catch (e: IOException) {
            Resource.Error(message = "Couldn't load intraday info")
        } catch (e: HttpException) {
            Resource.Error(message = "Couldn't load intraday info")
        }
    }
}