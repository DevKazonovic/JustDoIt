package com.devkazonovic.projects.justdoit.domain.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}