package com.towamwms.services.interfaces

import com.towamwms.entities.User
import com.towamwms.models.FetchParam

@Suppress("unused")
interface Fetchable<T> {
    fun getRecords(user: User, filters: HashMap<String, Any?>?, predicates: HashMap<String, Any?>?) : Array<T>

    fun getRecords(user: User, filters: HashMap<String, Any?>?) : Array<T> {
        return getRecords(user, filters, null)
    }

    fun getRecords(user: User) : Array<T> {
        return getRecords(user, null, null)
    }

    fun getRecords(param: FetchParam) : Array<T> {
        var filters = param.filters as HashMap?
        var predicates = param.predicates as HashMap?

        if (filters!!.isEmpty()) {
            filters = null
        }
        if (predicates!!.isEmpty()) {
            predicates = null
        }

        return getRecords(param.user, filters, predicates)
    }
}