package com.musala.weatherApp.data.mapper

/**
 * Model mappers to map dataEntity into domainEntity.
 *
 * @param <Data> the data model
 * @param <Domain> the domain model
 */
interface Mapper<in Data, out Domain> {
    fun Data.map(): Domain
}
