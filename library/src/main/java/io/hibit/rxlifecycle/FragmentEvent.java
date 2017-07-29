package io.hibit.rxlifecycle;

/**
 * Lifecycle events that can be emitted by Fragments.
 */
public enum FragmentEvent {
    ATTACH,
    CREATE,
    CREATE_VIEW,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY_VIEW,
    DESTROY,
    DETACH
}
