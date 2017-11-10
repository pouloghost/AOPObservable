package observable.aop.tools.gt.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Observable;

@Aspect
public class NotifyAspect {

  @Pointcut("target(java.util.Observable+) " +
      "&& !within(@observable.aop.tools.gt.aspect.IgnoredInvoker *)")
  public void observableClass() {
  }

  @Pointcut("withincode(@observable.aop.tools.gt.aspect.IgnoredInvoker * *(..))")
  public void ignoredMethod() {
  }

  @Pointcut("observableClass() && !ignoredMethod() && set(* *)")
  public void observableField() {
  }

  @After("observableField() && target(me) && this(invoker)")
  public void onSet(Object me, Object invoker) {
    Dispatchers.get().notifyDataChanged((Observable) me, invoker);
  }
}
