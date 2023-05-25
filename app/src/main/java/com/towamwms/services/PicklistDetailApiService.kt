package com.towamwms.services

import com.towamwms.entities.Part
import com.towamwms.entities.User
import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.entities.Picklist
import com.towamwms.entities.PicklistDetail
import com.towamwms.services.interfaces.Fetchable
import com.towamwms.services.internal.Pages

@Suppress("unused")
class PicklistDetailApiService(rootUrl: String) : AbstractApiService(rootUrl), Fetchable<PicklistDetail> {
    companion object {
        const val FILTER_ID = "id"
        const val FILTER_PICKLIST_ID = "picklist.id"
        const val FILTER_PICKLIST_DOCUMENT_NO = "picklist.documentNo"
        const val FILTER_PICKLIST_MFG_NO = "picklist.mfgNo"
        const val FILTER_PART_BARCODE = "part.barcode"
        const val FILTER_PART_STATUS = "part.status"
    }

    override fun getRecords(user: User, filters: HashMap<String, Any?>?, predicates: HashMap<String, Any?>?) : Array<PicklistDetail> {
        val tuple = Tuple3(user, convertHashMap(filters), convertHashMap(predicates))
        return request<Array<PicklistDetail>>(Pages.GET_PICKLIST_DETAILS, tuple).value!!
    }
}