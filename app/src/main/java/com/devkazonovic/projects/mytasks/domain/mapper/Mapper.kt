package com.devkazonovic.projects.mytasks.domain.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}