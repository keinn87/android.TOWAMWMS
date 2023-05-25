package com.towamwms.models

import android.os.Bundle
import com.towamwms.entities.User
import com.towamwms.entities.bases.TransactionEntity

@Suppress("CanBePrimaryConstructorProperty", "unused")
class InsertParam<T : TransactionEntity>(user: User, entity: T) {
    companion object {
        const val BUNDLE_USER = "__INSERT_PARAM_USER__"
        const val BUNDLE_ENTITY = "__INSERT_PARAM_ENTITY__"

        fun <T: TransactionEntity> fromBundle(bundle: Bundle): InsertParam<T> {
            val user = bundle.getParcelable(BUNDLE_USER) as User
            val entity = bundle.getParcelable(BUNDLE_ENTITY) as T

            return InsertParam(user, entity)
        }
    }

    var user: User = user
    var entity: T = entity

    fun toBundle(bundle: Bundle) : Bundle {
        bundle.putParcelable(BUNDLE_USER, user)
        bundle.putParcelable(BUNDLE_ENTITY, entity)

        return bundle
    }
}