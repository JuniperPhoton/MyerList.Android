package com.juniperphoton.myerlistandroid.api.response;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetOrderResponse extends CommonResponse {

    @SerializedName("OrderList")
    private ArrayList<ListOrder> listOrder;

    public String getOrder() {
        if (listOrder != null && listOrder.size() > 0) {
            return listOrder.get(0).getOrder();
        }
        return null;
    }

    private class ListOrder {
        @SerializedName("list_order")
        private String order;

        public String getOrder() {
            return order;
        }
    }
}
