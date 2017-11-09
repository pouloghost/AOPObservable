package observable.aop.tools.gt.aspect;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

public class Dispatchers {
  public static Dispatcher get() {
    if (Looper.myLooper() == null) {
      return new DirectDispatcher();
    } else {
      return new LooperDispatcher(Looper.myLooper());
    }
  }

  public interface Dispatcher {
    void notifyDataChanged(Observable observable, Object invoker);
  }

  private static Method sSetChanged = null;

  static {
    try {
      sSetChanged = Observable.class.getMethod("setChanged");
      sSetChanged.setAccessible(true);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  public static class LooperDispatcher implements Dispatcher {
    private static final int sDISPATCH_ALL = 233;
    private final Set<WeakReference<Pair<Observable, Object>>> mChangedObservables = new HashSet<>();
    private Handler mHandler;

    public LooperDispatcher(Looper looper) {
      mHandler = new Handler(looper) {
        @Override
        public void handleMessage(Message msg) {
          if (msg.what == sDISPATCH_ALL) {
            for (WeakReference<Pair<Observable, Object>> pack : mChangedObservables) {
              Pair<Observable, Object> pair = pack.get();
              if (pair != null) {
                pair.first.notifyObservers(pair.second);
              }
            }
          }
          super.handleMessage(msg);
        }
      };
    }

    @Override
    public void notifyDataChanged(Observable observable, Object invoker) {
      boolean changed = false;
      if (sSetChanged != null) {
        try {
          sSetChanged.invoke(observable);
          changed = true;
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
      if (changed) {
        mChangedObservables.add(new WeakReference<>(new Pair<>(observable, invoker)));
        mHandler.removeMessages(sDISPATCH_ALL);
        mHandler.sendEmptyMessage(sDISPATCH_ALL);
      }
    }
  }

  public static class DirectDispatcher implements Dispatcher {

    @Override
    public void notifyDataChanged(Observable observable, Object invoker) {
      observable.notifyObservers(invoker);
    }
  }
}
