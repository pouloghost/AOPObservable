package observable.aop.tools.gt.aspect;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

public class Dispatchers {
  public static boolean DO_LOG = true;
  private static final ThreadLocal<Dispatchers.Dispatcher> sDispatcher = new ThreadLocal<>();

  public static Dispatcher get() {
    if (sDispatcher.get() == null) {
      if (Looper.myLooper() == null) {
        sDispatcher.set(new DirectDispatcher());
      } else {
        sDispatcher.set(new LooperDispatcher(Looper.myLooper()));
      }
    }
    return sDispatcher.get();
  }

  public interface Dispatcher {
    void notifyDataChanged(Observable observable, Object invoker);
  }

  private static Method sSetChanged = null;

  static {
    try {
      sSetChanged = Observable.class.getDeclaredMethod("setChanged");
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
                log("real dispatch in looper" + pair.first + " by " + pair.second);
                pair.first.notifyObservers(pair.second);
              } else {
                log("empty pair");
              }
            }
          }
          super.handleMessage(msg);
        }
      };
    }

    @Override
    public void notifyDataChanged(Observable observable, Object invoker) {
      log("dispatch in looper " + observable);
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
        log("dispatched in looper " + observable);
      }
    }
  }

  public static class DirectDispatcher implements Dispatcher {

    @Override
    public void notifyDataChanged(Observable observable, Object invoker) {
      log("dispatch directly " + observable);
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
        observable.notifyObservers(invoker);
        log("dispatched directly " + observable);
      }
    }
  }

  private static final void log(String message) {
    if (!DO_LOG) {
      return;
    }
    Log.d("aop", message);
  }
}
