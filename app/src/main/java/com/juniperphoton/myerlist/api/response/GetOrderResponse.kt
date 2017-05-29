package com.juniperphoton.myerlist.api.response

import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class GetOrderResponse : CommonResponse() {
    @SerializedName("OrderList")
    private val listOrder: ArrayList<ListOrder>? = null

    val order: String?
        get() {
            if (listOrder != null && listOrder.size > 0) {
                return listOrder[0].order
            }
            return null
        }

    private class ListOrder {
        @SerializedName("list_order")
        val order: String? = null
    }
}