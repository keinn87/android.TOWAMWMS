package com.towamwms.services

import com.towamwms.entities.Receiving
import com.towamwms.entities.User
import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.services.interfaces.Insertable
import com.towamwms.services.internal.Pages
import java.util.*

@Suppress("unused")
class ReceivingApiService(rootUrl: String) : AbstractApiService(rootUrl), Insertable<Receiving> {

    override fun insertRecord(user: User, entity: Receiving): Receiving? {
        val tuple = Tuple3(UUID.randomUUID(), user, entity)
        return request<Receiving?>(Pages.INSERT_RECEIVING, tuple).value
    }
}