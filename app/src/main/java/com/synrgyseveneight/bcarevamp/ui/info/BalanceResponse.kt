package com.synrgyseveneight.bcarevamp.ui.info

data class InfoSaldo(
    var code: Int,
    var `data`: Data,
    var message: String,
    var status: Boolean
) {
    data class Data(
        var balance: Int,
        var checkTime: String
    )
}