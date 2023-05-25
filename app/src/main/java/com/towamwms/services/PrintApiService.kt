package com.towamwms.services

import com.towamwms.entities.User
import com.towamwms.enums.EnumReport
import com.towamwms.models.PrintParam
import com.globalsion.network.models.Tuple5
import com.globalsion.network.services.AbstractApiService
import com.towamwms.services.internal.Pages
import java.util.*
import kotlin.collections.HashMap

class PrintApiService(rootUrl: String) : AbstractApiService(rootUrl) {
    fun print(user: User, printer: String?, report: EnumReport, parameters: HashMap<String, Any?>?) : Boolean {
        val tuple = Tuple5(UUID.randomUUID(), user, printer, report, convertHashMap(parameters))
        return request<Boolean>(Pages.PRINT, tuple).value!!
    }

    fun print(param: PrintParam) : Boolean {
        var parameters = param.parameters as HashMap?

        if (parameters!!.isEmpty()) {
            parameters = null
        }

        return print(param.user, param.printer, param.report, parameters)
    }
}