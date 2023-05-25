package com.towamwms.services

import com.globalsion.network.models.Tuple3
import com.globalsion.network.services.AbstractApiService
import com.towamwms.entities.Picking
import com.towamwms.entities.User
import com.towamwms.services.interfaces.Insertable
import com.towamwms.services.internal.Pages
import java.util.*

class PickingApiService(rootUrl: String) : AbstractApiService(rootUrl), Insertable<Picking> {

    override fun insertRecord(user: User, entity: Picking): Picking? {
        val tuple = Tuple3(UUID.randomUUID(), user, entity)
        return request<Picking?>(Pages.INSERT_PICKING, tuple).value
    }
}