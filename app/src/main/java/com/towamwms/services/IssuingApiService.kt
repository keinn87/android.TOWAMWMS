package com.towamwms.services

import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.entities.Issuing
import com.towamwms.entities.Picking
import com.towamwms.entities.User
import com.towamwms.services.interfaces.Insertable
import com.towamwms.services.internal.Pages
import java.util.*

class IssuingApiService(rootUrl: String) : AbstractApiService(rootUrl), Insertable<Issuing> {

    override fun insertRecord(user: User, entity: Issuing): Issuing? {
        val tuple = Tuple3(UUID.randomUUID(), user, entity)
        return request<Issuing?>(Pages.INSERT_ISSUING, tuple).value
    }
}