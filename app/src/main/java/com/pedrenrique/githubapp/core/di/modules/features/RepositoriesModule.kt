package com.pedrenrique.githubapp.core.di.modules.features

import com.pedrenrique.githubapp.core.data.datasource.ProjectRepository
import com.pedrenrique.githubapp.core.domain.LoadRepositories
import com.pedrenrique.githubapp.features.repositories.RepositoriesFragment
import com.pedrenrique.githubapp.features.repositories.RepositoriesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoriesModule = module {
    scope(named<RepositoriesFragment>()) {
        viewModel {
            RepositoriesViewModel(get())
        }

        factory {
            LoadRepositories(get())
        }

        factory {
            LoadRepositories(get())
        }

        factory<ProjectRepository> {
            ProjectRepository.Impl(get())
        }
    }
}