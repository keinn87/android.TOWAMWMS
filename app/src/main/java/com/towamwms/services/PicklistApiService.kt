package com.towamwms.services

import com.globalsion.network.models.Tuple2
import com.towamwms.entities.Part
import com.towamwms.entities.User
import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.entities.Picklist
import com.towamwms.services.interfaces.Fetchable
import com.towamwms.services.internal.Pages

@Suppress("unused")
class PicklistApiService(rootUrl: String) : AbstractApiService(rootUrl), Fetchable<Picklist> {
    companion object {
        const val FILTER_ID = "id"
        const val FILTER_DOCUMENT_NO = "documentNo"
        const val FILTER_MFG_NO = "mfgNo"
    }

    override fun getRecords(user: User, filters: HashMap<String, Any?>?, predicates: HashMap<String, Any?>?) : Array<Picklist> {
        val tuple = Tuple3(user, convertHashMap(filters), convertHashMap(predicates))
        return request<Array<Picklist>>(Pages.GET_PICKLISTS, tuple).value!!
    }
    fun getRecordByPartBarcode(user: User, partBarcode: String) : Array<Picklist> {
        val tuple = Tuple2(user, partBarcode)
        return request<Array<Picklist>>(Pages.GET_PICKLIST_BY_PART_BARCODE, tuple).value!!
    }
}