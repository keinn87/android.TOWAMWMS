package com.towamwms.services

import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.entities.JobSummaryView
import com.towamwms.entities.User
import com.towamwms.services.interfaces.Fetchable
import com.towamwms.services.internal.Pages

@Suppress("unused")
class JobSummaryViewApiService(rootUrl: String) : AbstractApiService(rootUrl), Fetchable<JobSummaryView> {
    companion object {
        const val FILTER_MFG_NO = "mfgNo"
    }

    override fun getRecords(user: User, filters: HashMap<String, Any?>?, predicates: HashMap<String, Any?>?) : Array<JobSummaryView> {
        val tuple = Tuple3(user, convertHashMap(filters), convertHashMap(predicates))
        return request<Array<JobSummaryView>>(Pages.GET_JOB_SUMMARY_VIEW, tuple).value!!
    }
}