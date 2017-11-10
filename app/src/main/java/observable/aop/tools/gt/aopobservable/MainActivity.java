package observable.aop.tools.gt.aopobservable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

import observable.aop.tools.gt.aspect.Dispatchers;
import observable.aop.tools.gt.aspect.IgnoredInvoker;

public class MainActivity extends Activity {

  // 避免被释放
  private User mLooperUser = new User();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLooperUser.addObserver(new Observer() {
      @Override
      public void update(Observable observable, Object o) {
        Log.e("aop", observable + " changed by " + o);
      }
    });
    // 直接修改，AOP通知
    mLooperUser.mName = "looper";
    Log.e("aop", "after changed");
    final User threadUser = new User();
    threadUser.addObserver(new Observer() {
      @Override
      public void update(Observable observable, Object o) {
        Log.e("aop", observable + " changed by " + o);
      }
    });
    new Thread() {
      @Override
      // 必须是方法最近的annotation
      @IgnoredInvoker
      public void run() {
        super.run();
        // 不会AOP
        threadUser.mName = "thread";
        threadUser.mGender = 1;
        Log.e("aop", "after changed");
        // 手动触发
        Dispatchers.get().notifyDataChanged(threadUser, MainActivity.this);
      }
    }.start();
  }
}
