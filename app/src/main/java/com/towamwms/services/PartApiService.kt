package com.towamwms.services

import com.towamwms.entities.Part
import com.towamwms.entities.User
import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.services.interfaces.Fetchable
import com.towamwms.services.internal.Pages

@Suppress("unused")
class PartApiService(rootUrl: String) : AbstractApiService(rootUrl), Fetchable<Part> {
    companion object {
        const val FILTER_ID = "id"
        const val FILTER_BARCODE = "barcode"
        const val FILTER_DELETED = "deleted"
        const val FILTER_MFGNO = "mfgNo"
    }

    override fun getRecords(user: User, filters: HashMap<String, Any?>?, predicates: HashMap<String, Any?>?) : Array<Part> {
        val tuple = Tuple3(user, convertHashMap(filters), convertHashMap(predicates))
        return request<Array<Part>>(Pages.GET_PARTS, tuple).value!!
    }
}