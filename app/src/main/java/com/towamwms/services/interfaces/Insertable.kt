package com.towamwms.services.interfaces

import com.towamwms.entities.User
import com.towamwms.entities.bases.Entity

interface Insertable<T : Entity> {
    fun insertRecord(user: User, entity: T) : T?
}