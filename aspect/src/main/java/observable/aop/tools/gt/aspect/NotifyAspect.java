package observable.aop.tools.gt.aspect;

import android.util.Log;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Observable;

@Aspect
public class NotifyAspect {
  private static final ThreadLocal<Dispatchers.Dispatcher> sDispatcher = new ThreadLocal<>();

  @Pointcut("target(java.util.Observable+) " +
      "&& !within(@observable.aop.tools.gt.aspect.IgnoredInvoker *)")
  public void observableClass() {
  }

  @Pointcut("observableClass() && set(* *)")
  public void observableField() {
  }

  @After("observableField() && target(me) && this(invoker)")
  public void onSet(Object me, Object invoker) {
    Log.e("aop", "observed by aop");
    if (sDispatcher.get() == null) {
      sDispatcher.set(Dispatchers.get());
    }
    sDispatcher.get().notifyDataChanged((Observable) me, invoker);
  }
}
