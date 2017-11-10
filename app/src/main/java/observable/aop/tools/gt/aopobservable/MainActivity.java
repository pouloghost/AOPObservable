package observable.aop.tools.gt.aopobservable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

import observable.aop.tools.gt.aspect.Dispatchers;
import observable.aop.tools.gt.aspect.IgnoredInvoker;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final User user = new User();
    user.addObserver(new Observer() {
      @Override
      public void update(Observable observable, Object o) {
        Log.e("aop", observable + " changed by " + o);
      }
    });
    new Thread() {
      @Override
      @IgnoredInvoker
      public void run() {
        super.run();
        user.mName = "lalala";
        user.mGender = 1;
        Log.e("aop", "after changed");
        Dispatchers.get().notifyDataChanged(user, MainActivity.this);
      }
    }.start();
  }
}
