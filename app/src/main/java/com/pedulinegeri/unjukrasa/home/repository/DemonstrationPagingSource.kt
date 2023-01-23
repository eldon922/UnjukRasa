package com.pedulinegeri.unjukrasa.home.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.pedulinegeri.unjukrasa.demonstration.Demonstration
import kotlinx.coroutines.tasks.await

abstract class DemonstrationPagingSource : PagingSource<QuerySnapshot, Demonstration>() {

    protected abstract val collectionRef: Query

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Demonstration> {
        return try {
            // Step 1
            val currentPage = params.key ?: collectionRef
                .get()
                .await()

            // Step 2
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            // Step 3
            val nextPage = collectionRef.startAfter(lastDocumentSnapshot)
                .get()
                .await()

            val data = currentPage.toObjects(Demonstration::class.java)
            for (i in 0 until data.size) {
                data[i].id = currentPage.documents[i].id
            }

            // Step 4
            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Demonstration>): QuerySnapshot? {
        return null
    }
}