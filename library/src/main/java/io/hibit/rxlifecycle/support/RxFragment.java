package io.hibit.rxlifecycle.support;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.hibit.rxlifecycle.FragmentEvent;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class RxFragment extends Fragment {

    private final Map<FragmentEvent, CompositeDisposable> disposables = new ConcurrentHashMap<>(8);
    private FragmentEvent lastEvent = FragmentEvent.ATTACH;


    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);
        this.onEvent(FragmentEvent.ATTACH);
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.onEvent(FragmentEvent.CREATE);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onEvent(FragmentEvent.CREATE_VIEW);
    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        this.onEvent(FragmentEvent.START);
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        this.onEvent(FragmentEvent.RESUME);
    }

    @Override
    @CallSuper
    public void onPause() {
        this.onEvent(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    public void onStop() {
        this.onEvent(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        this.onEvent(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    @CallSuper
    public void onDestroy() {
        this.onEvent(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    @CallSuper
    public void onDetach() {
        this.onEvent(FragmentEvent.DETACH);
        super.onDetach();
    }


    /**
     * Dispose a subscriber on given event
     *
     * @param disposable
     * @param event
     */
    public final void dispose(Disposable disposable, FragmentEvent event) {
        addDisposable(disposable, event);
    }

    /**
     * Dispose a subscriber on alternative event
     *
     * @param disposable
     */
    public final void dispose(Disposable disposable) {
        final FragmentEvent lastEvent = this.lastEvent;
        FragmentEvent event = null;
        switch (lastEvent) {
            case ATTACH:
                event = FragmentEvent.DETACH;
                break;

            case CREATE:
                event = FragmentEvent.DESTROY;
                break;

            case CREATE_VIEW:
                event = FragmentEvent.DESTROY_VIEW;
                break;

            case START:
                event = FragmentEvent.STOP;
                break;

            case RESUME:
                event = FragmentEvent.PAUSE;
                break;

            case PAUSE:
                event = FragmentEvent.STOP;
                break;

            case STOP:
                event = FragmentEvent.DESTROY_VIEW;
                break;

            case DESTROY_VIEW:
                event = FragmentEvent.DESTROY;
                break;

            case DESTROY:
                event = FragmentEvent.DETACH;
                break;

            default:
                throw new UnsupportedOperationException(String.format("Event can't be disposed at eventType : %s", lastEvent.name()));
        }

        this.dispose(disposable, event);
    }

    private void addDisposable(Disposable disposable, FragmentEvent event) {
        CompositeDisposable d = getDisposable(event);
        d.add(disposable);
    }

    private void onEvent(FragmentEvent event) {
        this.lastEvent = event;
        this.disposeAndRemove(event);
    }

    private void disposeAndRemove(FragmentEvent event) {
        if (disposables.containsKey(event)) {
            CompositeDisposable disposable = disposables.get(event);
            disposable.dispose();
            disposable.clear();
            disposables.remove(event);
        }
    }

    private CompositeDisposable getDisposable(FragmentEvent event) {
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
