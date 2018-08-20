package com.fintech.sst.helper;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {
    /**
     * 参考网址: http://hanhailong.com/2015/10/09/RxBus%E2%80%94%E9%80%9A%E8%BF%87RxJava%E6%9D%A5%E6%9B%BF%E6%8D%A2EventBus/
     *          http://www.loongwind.com/archives/264.html
     *          https://theseyears.gitbooks.io/android-architecture-journey/content/rxbus.html
     */
    private static volatile RxBus mDefaultInstance;

    private RxBus() {
    }

    public static RxBus getDefault() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    private final Subject<Object> _bus = PublishSubject.create().toSerialized();

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return _bus;
    }
    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     * @param eventType 事件类型
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return _bus.ofType(eventType);
    }

//    /**
//     * 提供了一个新的事件,根据code进行分发
//     * @param code 事件code
//     * @param o
//     */
//    public void post(int code, Object o){
//        _bus.onNext(new RxEvent(code,o));
//
//    }
//
//
//    /**
//     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
//     * 对于注册了code为0，class为voidMessage的观察者，那么就接收不到code为0之外的voidMessage。
//     * @param code 事件code
//     * @param eventType 事件类型
//     * @param <T>
//     * @return
//     */
//    public <T> Observable<T> toObservable(final int code, final Class<T> eventType) {
//        return _bus.ofType(RxEvent.class)
//                .filter(new Predicate<RxEvent>() {
//                    @Override
//                    public boolean test(RxEvent rxEvent) throws Exception {
//                        return rxEvent.getCode() == code && eventType.isInstance(rxEvent.getObject());
//                    }
//                })
//                .map(new Function<RxEvent, Object>() {
//                    @Override
//                    public Object apply(RxEvent rxEvent) throws Exception {
//                        return rxEvent.getObject();
//                    }
//                })
//                .cast(eventType);
//    }
    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return _bus.hasObservers();
    }


}
