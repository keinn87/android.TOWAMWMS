package com.towamwms.services

import com.towamwms.entities.Transferring
import com.towamwms.entities.User
import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.services.interfaces.Insertable
import com.towamwms.services.internal.Pages
import java.util.*

@Suppress("unused")
class TransferringApiService(rootUrl: String) : AbstractApiService(rootUrl), Insertable<Transferring> {

    override fun insertRecord(user: User, entity: Transferring): Transferring? {
        val tuple = Tuple3(UUID.randomUUID(), user, entity)
        return request<Transferring?>(Pages.INSERT_TRANSFERRING, tuple).value
    }
}