package com.abdok.geminichat.UI.Home;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abdok.geminichat.Local.CacheDatabase;
import com.abdok.geminichat.Models.ModelCache;
import com.abdok.geminichat.Models.ModelChat;
import com.abdok.geminichat.Utils.Consts;
import com.abdok.geminichat.Utils.SharedModel;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends AndroidViewModel {

    private CacheDatabase cacheDatabase;
    public MutableLiveData<String> confirmationState = new MutableLiveData<>();

    public LiveData<String> getConfirmationState() {
        return confirmationState;
    }

    public HomeViewModel(@NonNull Application application) {
        super(application);
        cacheDatabase = CacheDatabase.getInstance(application);
    }




    public void getMessage(ModelChat uModel  , Executor listeningExecutor){


        GenerativeModel gm = new GenerativeModel(Consts.GEMINI_PRO,Consts.GEMINI_TOKEN);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(uModel.getMessage())
                .build();


        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {

                ModelChat gModel = new ModelChat(Consts.GEMINI , result.getText(),null);
                SharedModel.activeChat.add(gModel);
                confirmationState.setValue("S");

            }

            @Override
            public void onFailure(Throwable t) {
                confirmationState.setValue("FAILED");
            }
        }, listeningExecutor);
    }

    public void getImageMessage(ModelChat uModel , Executor listeningExecutor , Bitmap bitmap){


        GenerativeModel gm = new GenerativeModel(Consts.GEMINI_FLASH,Consts.GEMINI_TOKEN);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);


        Content content =
                new Content.Builder()
                        .addText(uModel.getMessage())
                        .addImage(bitmap)
                        .build();


        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {

                        ModelChat gModel = new ModelChat(Consts.GEMINI , result.getText(),null);
                        SharedModel.activeChat.add(gModel);
                        confirmationState.setValue("S");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        confirmationState.setValue(t.getLocalizedMessage());
                        Log.e("'TAG'", "onFailure: "+t.getLocalizedMessage() );
                    }
                },
                listeningExecutor);
    }

    public void insertCache(ModelCache modelCache) {

                cacheDatabase.cacheDao().insertCache(modelCache)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        ;
    }


    public void getLastitem(ModelCache model) {

        cacheDatabase.cacheDao().getLastItem()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ModelCache>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ModelCache modelCache) {
                        model.setId(modelCache.getId());
                        updateCache(model);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
    public void updateCache(ModelCache modelCache) {

        cacheDatabase.cacheDao().updateCache(modelCache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                })
        ;
    }


}