package com.rmnivnv.cryptomoon.ui.main

import com.rmnivnv.cryptomoon.model.rxbus.CoinsLoadingEvent
import com.rmnivnv.cryptomoon.model.rxbus.OnDeleteCoinsMenuItemClickedEvent
import com.rmnivnv.cryptomoon.model.rxbus.RxBus
import com.rmnivnv.cryptomoon.model.MultiSelector
import com.rmnivnv.cryptomoon.model.PageController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by rmnivnv on 06/07/2017.
 */
class MainPresenter @Inject constructor(private val view: IMain.View,
                                        private val multiSelector: MultiSelector,
                                        private val pageController: PageController) : IMain.Presenter {
    private val disposable = CompositeDisposable()

    override fun onCreate() {
        setObservers()
    }

    private fun setObservers() {
        disposable.add(RxBus.listen(CoinsLoadingEvent::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.setCoinsLoadingVisibility(it.isLoading)
                })
        disposable.add(multiSelector.getSelectorObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.setMenuIconsVisibility(it)
                })
    }

    override fun onDestroy() {
        disposable.clear()
    }

    override fun onAddCoinClicked() {
        view.startAddCoinActivity()
    }

    override fun onSettingsClicked() {
        //TODO implement on settings clicked
        view.showToast("There will be SETTINGS")
    }

    override fun onDeleteClicked() {
        view.setMenuIconsVisibility(false)
        RxBus.publish(OnDeleteCoinsMenuItemClickedEvent())
    }

    override fun onPageSelected(position: Int) {
        pageController.pageSelected(position)
    }
}