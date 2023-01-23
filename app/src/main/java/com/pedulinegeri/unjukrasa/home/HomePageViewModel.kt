package com.pedulinegeri.unjukrasa.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.pedulinegeri.unjukrasa.home.repository.MostRecentCreatedDemonstrationPagingSource
import com.pedulinegeri.unjukrasa.home.repository.MostUpvotedDemonstrationPagingSource
import com.pedulinegeri.unjukrasa.home.repository.TrendingDemonstrationPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val trendingDemonstrationPagingSource: TrendingDemonstrationPagingSource,
    private val mostUpvotedDemonstrationPagingSource: MostUpvotedDemonstrationPagingSource,
private val mostRecentCreatedDemonstrationPagingSource: MostRecentCreatedDemonstrationPagingSource) :
    ViewModel() {

    var nsvScrollPosition = 0
    var rvMostRecentCreatedDemonstrationLastHeight = 0

    val trendingDemonstrationFlow = Pager(PagingConfig(10, 5, true)) {
        trendingDemonstrationPagingSource
    }.flow.cachedIn(viewModelScope)

    val mostUpvotedDemonstrationFlow = Pager(PagingConfig(10, 5, true)) {
        mostUpvotedDemonstrationPagingSource
    }.flow.cachedIn(viewModelScope)

    val mostRecentCreatedDemonstrationFlow = Pager(PagingConfig(10, 5, true)) {
        mostRecentCreatedDemonstrationPagingSource
    }.flow.cachedIn(viewModelScope)
}