package com.rmnivnv.cryptomoon.model

import com.rmnivnv.cryptomoon.model.db.CMDatabase
import com.rmnivnv.cryptomoon.utils.doubleFromString
import io.reactivex.schedulers.Schedulers

/**
 * Created by rmnivnv on 09/09/2017.
 */
class HoldingsHandler(db: CMDatabase) {

    init {
        db.displayCoinsDao().getAllCoins()
                .subscribeOn(Schedulers.io())
                .subscribe({ displayCoins = it })
    }

    private var displayCoins: List<DisplayCoin> = arrayListOf()

    fun getTotalChangePercent(holdings: List<HoldingData>): Double {
        val oldValue = getTotalValueWithTradePrice(holdings)
        var newValue = 0.0

        holdings.forEach {
            val quantity = it.quantity
            val fromList = displayCoins.filter { (from) -> it.from == from }
            fromList.forEach {
                if (it.to == USD) {
                    newValue += quantity * doubleFromString(it.PRICE.substring(2))
                } else {
                    //todo calculate if not USD
                }
            }
        }
        return calculateChangePercent(oldValue, newValue)
    }

    private fun calculateChangePercent(value1: Double, value2: Double) =
            if (value1 > value1) (value2 - value1) / value2 * 100
            else (value2 - value1) / value1 * 100

    private fun getTotalValueWithTradePrice(holdings: List<HoldingData>): Double {
        val sums: ArrayList<Double> = arrayListOf()
        holdings.forEach { sums.add(it.quantity * it.price) }
        return sums.sum()
    }

    fun getTotalValueWithCurrentPrice(holdings: List<HoldingData>): Double {
        val sums: ArrayList<Double> = arrayListOf()
        holdings.forEach { (from, _, quantity) ->
            val currentPrice = displayCoins.find { it.from == from }?.PRICE
            if (currentPrice != null) {
                sums.add(quantity * doubleFromString(currentPrice.substring(2)))
            }
        }
        return sums.sum()
    }

    fun getTotalValueWithCurrentPriceByHoldingData(holdingData: HoldingData): Double {
        val currentPrice = displayCoins.find { it.from == holdingData.from && it.to == holdingData.to }?.PRICE
        if (currentPrice != null) {
            return doubleFromString(currentPrice) * holdingData.quantity
        }
        return 0.0
    }



}