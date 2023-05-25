package com.globalsion.models

import com.globalsion.network.annotations.SerializedTypeName

@SerializedTypeName(["com.globalsion.data.structure.PredicateParams"])
class PredicateParams(vararg objects: Any?) {
    var objects: Array<Any?> = arrayOf(objects)
}
