package com.towamwms.services

import com.towamwms.entities.User
import com.globalsion.network.models.Tuple2
import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.services.interfaces.Fetchable
import com.towamwms.services.internal.Pages
import com.towamwms.utils.HashUtil

class UserApiService(url: String) : AbstractApiService(url), Fetchable<User> {
    companion object {
        const val FILTER_ID = "id"
        const val FILTER_USERNAME = "username"
        const val FILTER_PASSWORD_HASH = "passwordHash"

    }

    @Throws(Exception::class)
    fun   login(username: String, password: String): User? {
        val passwordHash = HashUtil.hash(password)
        val tuple = Tuple2(username, passwordHash)
        return request<User?>(Pages.LOGIN, tuple).value
    }

    override fun getRecords(user: User, filters: HashMap<String, Any?>?, predicates: HashMap<String, Any?>?) : Array<User> {
        val tuple = Tuple3(user, convertHashMap(filters), convertHashMap(predicates))
        return request<Array<User>>(Pages.GET_USERS, tuple).value!!
    }
}