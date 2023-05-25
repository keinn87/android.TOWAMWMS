package com.towamwms.services

import com.towamwms.entities.Bin
import com.towamwms.entities.User
import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.services.interfaces.Fetchable
import com.towamwms.services.internal.Pages

@Suppress("unused")
class BinApiService(rootUrl: String) : AbstractApiService(rootUrl), Fetchable<Bin> {
    companion object {
        const val FILTER_ID = "id"
        const val FILTER_CODE = "code"
        const val FILTER_TYPE = "type"
    }

    override fun getRecords(user: User, filters: HashMap<String, Any?>?, predicates: HashMap<String, Any?>?): Array<Bin> {
        val tuple = Tuple3(user, convertHashMap(filters), convertHashMap(predicates))
        return request<Array<Bin>>(Pages.GET_BINS, tuple).value!!
    }
}