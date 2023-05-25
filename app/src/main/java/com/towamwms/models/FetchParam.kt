package com.towamwms.models

import android.os.Bundle
import com.globalsion.models.ObjectHolder
import com.towamwms.entities.User
import com.globalsion.utils.GsonUtil
import com.google.gson.reflect.TypeToken

@Suppress("CanBePrimaryConstructorProperty", "unused")
class FetchParam(user: User) {
    companion object {
        const val BUNDLE_USER = "__FETCH_PARAM_USER__"
        const val BUNDLE_FILTERS = "__FETCH_PARAM_FILTERS__"
        const val BUNDLE_PREDICATES = "__FETCH_PARAM_PREDICATES__"

        fun fromBundle(bundle: Bundle) : FetchParam {
            val token = TypeToken.getParameterized(LinkedHashMap::class.java, String::class.java, ObjectHolder::class.java)
            val gson = GsonUtil.gson
            val user = bundle.getParcelable(BUNDLE_USER) as User

            val tempFilters: HashMap<String, ObjectHolder> =
                    gson.fromJson(bundle.getString(BUNDLE_FILTERS), token.type)
            val tempPredicates: HashMap<String, ObjectHolder> =
                    gson.fromJson(bundle.getString(BUNDLE_PREDICATES), token.type)

            val param = FetchParam(user)
            tempFilters.forEach { param.filters[it.key] = it.value.value }
            tempPredicates.forEach { param.predicates[it.key] = it.value.value }

            return param
        }
    }

    var user: User = user
    var filters: HashMap<String, Any?> = LinkedHashMap()
    var predicates: HashMap<String, Any?> = LinkedHashMap()

    fun toBundle(bundle: Bundle) : Bundle {
        val gson = GsonUtil.gson
        val tempFilters = LinkedHashMap<String, ObjectHolder>()
        val tempPredicates = LinkedHashMap<String, ObjectHolder>()

        filters.forEach { tempFilters[it.key] = ObjectHolder(it.value) }
        predicates.forEach { tempPredicates[it.key] = ObjectHolder(it.value) }

        bundle.putParcelable(BUNDLE_USER, user)
        bundle.putString(BUNDLE_FILTERS, gson.toJson(tempFilters))
        bundle.putString(BUNDLE_PREDICATES, gson.toJson(tempPredicates))

        return bundle
    }
}