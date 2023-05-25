package com.globalsion.network.models

open class Tuple3<T1, T2, T3>(item1: T1, item2: T2, var item3: T3) : Tuple2<T1, T2>(item1, item2)
