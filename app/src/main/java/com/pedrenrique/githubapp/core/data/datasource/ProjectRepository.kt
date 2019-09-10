package com.pedrenrique.githubapp.core.data.datasource

import com.pedrenrique.githubapp.core.data.PaginatedData
import com.pedrenrique.githubapp.core.data.Repository
import com.pedrenrique.githubapp.core.exceptions.EmptyResultException
import com.pedrenrique.githubapp.core.exceptions.NoMoreResultException
import com.pedrenrique.githubapp.core.net.services.GithubService

interface ProjectRepository {

    suspend fun load(page: Int = 1): PaginatedData<Repository>

    class Impl(private val service: GithubService) : ProjectRepository {

        private var data = PaginatedData<Repository>(0, listOf())
        private var isLoadPending = false

        override suspend fun load(page: Int): PaginatedData<Repository> =
            requestingPage(page) {
                val response = service.list(
                    mapOf(
                        "q" to "language:Java",
                        "sort" to "stars",
                        "page" to page.toString()
                    )
                ).await()

                if (response.items.isEmpty()) {
                    if (page == 1) throw EmptyResultException()
                    else throw NoMoreResultException()
                }

                data = PaginatedData(page, data.content + response.items)
                data
            }

        private suspend fun <T> requestingPage(page: Int, request: suspend () -> T) =
            try {
                if (page <= 0) error("Page must be greater than zero")
                if (page <= data.page) error("Page must be greater than last page requested (${data.page})")
                if (isLoadPending) error("This page load is in progress")
                isLoadPending = true
                request()
            } finally {
                isLoadPending = false
            }
    }
}