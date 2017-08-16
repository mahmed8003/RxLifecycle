# RxLifecycle
This library handles disposing of Observer on different lifecycle events.
# Usage
Extend Activity from RxActivity
```java
public class MainActivity extends RxActivity {
```
or Fragment from RxFragment
```java
public class MainFragment extends RxFragment {
```
Create a Disposable
```java
Disposable d = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, value.toString() + " Seconds");
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
```
Now if you want to dispose Disposable "d" on a specific event - e.g., onResume
```java
this.dispose(d, ActivityEvent.RESUME);
```
or if you want to dispose Disposable "d" on a opposing lifecycle event - e.g., if subscribing during START, it will terminate on STOP
```java
this.dispose(d);
```
# Installation
```script
maven { url "https://jitpack.io" }
```
and:
```script
dependencies {
    compile 'com.github.mahmed8003:RxLifecycle:0.0.3'
}
```