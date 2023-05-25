package com.towamwms.models

import android.os.Bundle
import com.globalsion.models.ObjectHolder
import com.towamwms.entities.User
import com.towamwms.enums.EnumReport
import com.globalsion.utils.GsonUtil
import com.google.gson.reflect.TypeToken

@Suppress("CanBePrimaryConstructorProperty")
class PrintParam(user: User, report: EnumReport) {
    companion object {
        const val BUNDLE_USER = "__PRINT_PARAM_USER__"
        const val BUNDLE_REPORT = "__PRINT_PARAM_REPORT__"
        const val BUNDLE_PRINTER = "__PRINT_PARAM_PRINTER__"
        const val BUNDLE_PARAMETERS = "__PRINT_PARAM_PARAMETERS__"

        fun fromBundle(bundle: Bundle) : PrintParam {
            val token = TypeToken.getParameterized(LinkedHashMap::class.java, String::class.java, ObjectHolder::class.java)
            val gson = GsonUtil.gson
            val user = bundle.getParcelable(BUNDLE_USER) as User
            val report = EnumReport.valueOf(bundle.getInt(BUNDLE_REPORT, -1))
            val printer = bundle.getString(BUNDLE_PRINTER)

            val tempParameters: HashMap<String, ObjectHolder> =
                    gson.fromJson(bundle.getString(BUNDLE_PARAMETERS), token.type)

            val param = PrintParam(user, report)
            param.printer = printer
            tempParameters.forEach { param.parameters[it.key] = it.value.value }
            return param
        }
    }

    var user: User = user
    var report: EnumReport = report
    var printer: String? = null
    var parameters: HashMap<String, Any?> = LinkedHashMap()

    fun toBundle(bundle: Bundle) : Bundle {
        val gson = GsonUtil.gson
        val tempParameters = LinkedHashMap<String, ObjectHolder>()

        parameters.forEach { tempParameters[it.key] = ObjectHolder(it.value) }

        bundle.putParcelable(BUNDLE_USER, user)
        bundle.putInt(BUNDLE_REPORT, report.value)
        bundle.putString(BUNDLE_PRINTER, printer)
        bundle.putString(BUNDLE_PARAMETERS, gson.toJson(tempParameters))

        return bundle
    }
}