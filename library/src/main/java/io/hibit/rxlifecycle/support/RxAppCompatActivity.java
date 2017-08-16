package io.hibit.rxlifecycle.support;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.hibit.rxlifecycle.ActivityEvent;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 *
 */
public class RxAppCompatActivity extends AppCompatActivity {

    private final Map<ActivityEvent, CompositeDisposable> disposables = new ConcurrentHashMap<>(8);
    private ActivityEvent lastEvent = ActivityEvent.CREATE;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.onEvent(ActivityEvent.CREATE);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        this.onEvent(ActivityEvent.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        this.onEvent(ActivityEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        this.onEvent(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        this.onEvent(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        this.onEvent(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    /**
     * Dispose a subscriber on given event
     *
     * @param disposable
     * @param event
     */
    public final void dispose(Disposable disposable, ActivityEvent event) {
        addDisposable(disposable, event);
    }

    /**
     * Dispose a subscriber on alternative event
     *
     * @param disposable
     */
    public final void dispose(Disposable disposable) {
        final ActivityEvent lastEvent = this.lastEvent;
        ActivityEvent event = null;
        switch (lastEvent) {
            case CREATE:
                event = ActivityEvent.DESTROY;
                break;

            case START:
                event = ActivityEvent.STOP;
                break;

            case RESUME:
                event = ActivityEvent.PAUSE;
                break;

            case PAUSE:
                event = ActivityEvent.STOP;
                break;

            case STOP:
                event = ActivityEvent.DESTROY;
                break;

            default:
                throw new UnsupportedOperationException(String.format("Event can't be disposed at eventType : %s", lastEvent.name()));
        }

        this.dispose(disposable, event);
    }


    private void addDisposable(Disposable disposable, ActivityEvent event) {
        CompositeDisposable d = getDisposable(event);
        d.add(disposable);
    }

    private void onEvent(ActivityEvent event) {
        this.lastEvent = event;
        this.disposeAndRemove(event);
    }

    private void disposeAndRemove(ActivityEvent event) {
        if (disposables.containsKey(event)) {
            CompositeDisposable disposable = disposables.get(event);
            disposable.dispose();
            disposable.clear();
            disposables.remove(event);
        }
    }

    private CompositeDisposable getDisposable(ActivityEvent event) {
        CompositeDisposable disposable = null;
        if (disposables.containsKey(event)) {
            disposable = disposables.get(event);
        } else {
            disposable = new CompositeDisposable();
            disposables.put(event, disposable);
        }

        return disposable;
    }
}
