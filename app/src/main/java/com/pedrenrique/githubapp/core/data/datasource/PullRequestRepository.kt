package com.pedrenrique.githubapp.core.data.datasource

import com.pedrenrique.githubapp.core.data.PaginatedData
import com.pedrenrique.githubapp.core.data.PullRequest
import com.pedrenrique.githubapp.core.data.Repository
import com.pedrenrique.githubapp.core.exceptions.EmptyResultException
import com.pedrenrique.githubapp.core.exceptions.NoMoreResultException
import com.pedrenrique.githubapp.core.net.services.GithubService

interface PullRequestRepository {

    suspend fun load(repository: Repository, page: Int = 1): PaginatedData<PullRequest>

    class Impl(private val service: GithubService) : PullRequestRepository {
        private var data = PaginatedData<PullRequest>(0, listOf())
        private var isLoadPending = false

        override suspend fun load(repository: Repository, page: Int): PaginatedData<PullRequest> =
            requestingPage(page) {
                val response = service.listPR(
                    repository.owner.login,
                    repository.name,
                    page
                ).await()

                if (response.isEmpty()) {
                    if (page == 1) throw EmptyResultException()
                    else throw NoMoreResultException()
                }

                data = PaginatedData(page, data.content + response)
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