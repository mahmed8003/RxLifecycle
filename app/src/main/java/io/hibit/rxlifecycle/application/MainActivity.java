package io.hibit.rxlifecycle.application;

import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.hibit.rxlifecycle.ActivityEvent;
import io.hibit.rxlifecycle.RxActivity;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends RxActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        disposeOnResume();
        disposeOnDestroy();
    }


    private void disposeOnResume() {
        Disposable d = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "Timer A:" + value.toString() + " Seconds");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

        /*
         * Dispose observer on ActivityEvent.RESUME
         */
        dispose(d, ActivityEvent.RESUME);
    }

    private void disposeOnDestroy() {
        Disposable d = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "Timer B:" + value.toString() + " Seconds");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

        /*
         * Dispose observer
         */
        dispose(d);
    }


}
