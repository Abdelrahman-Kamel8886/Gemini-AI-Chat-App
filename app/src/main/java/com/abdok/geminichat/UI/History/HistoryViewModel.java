package com.abdok.geminichat.UI.History;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abdok.geminichat.Enums.ConfirmationState;
import com.abdok.geminichat.Local.CacheDatabase;
import com.abdok.geminichat.Models.ModelCache;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryViewModel extends AndroidViewModel {

    private CacheDatabase cacheDatabase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<ModelCache>> cachesLiveData = new MutableLiveData<>();

    private MutableLiveData<ConfirmationState> updateConfirmtion = new MutableLiveData<>();

    private MutableLiveData<ConfirmationState> deleteConfirmtion = new MutableLiveData<>();

    public LiveData<ConfirmationState> getUpdateConfirmtion() {
        return updateConfirmtion;
    }

    public LiveData<ConfirmationState> getDeleteConfirmtion() {
        return deleteConfirmtion;
    }

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        cacheDatabase = CacheDatabase.getInstance(application);
    }

    public LiveData<List<ModelCache>> getCachesLiveData() {
        return cachesLiveData;
    }



    public void fetchAllCaches() {

                cacheDatabase.cacheDao().getAllCaches()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<ModelCache>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<ModelCache> modelCaches) {
                                cachesLiveData.setValue(modelCaches);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("TAG", "onError: "+e.getLocalizedMessage() );
                            }
                        }
        );
    }

    public void deleteCache(ModelCache modelCache) {

        cacheDatabase.cacheDao().delete(modelCache)
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onComplete() {
                    deleteConfirmtion.setValue(ConfirmationState.SUCCESS);
                }

                @Override
                public void onError(Throwable e) {
                    deleteConfirmtion.setValue(ConfirmationState.FAILED);

                }
            });

    }

    public void updateTitleById(int cacheId, String title) {

                cacheDatabase.cacheDao().updateTitleById(cacheId, title)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                updateConfirmtion.setValue(ConfirmationState.SUCCESS);
                            }

                            @Override
                            public void onError(Throwable e) {
                                updateConfirmtion.setValue(ConfirmationState.FAILED);

                            }
                        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}