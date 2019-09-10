package com.pedrenrique.githubapp.features.common.adapter

import com.pedrenrique.githubapp.features.common.adapter.factory.TypesFactory

abstract class ModelHolder {
    abstract fun type(typesFactory: TypesFactory): Int

    open fun areItemTheSame(newItem: ModelHolder?) =
        this == newItem

    open fun areContentTheSame(newItem: ModelHolder?) =
        this == newItem
}