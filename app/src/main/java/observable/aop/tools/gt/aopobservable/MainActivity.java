package observable.aop.tools.gt.aopobservable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    User user = new User();
    user.addObserver(new Observer() {
      @Override
      public void update(Observable observable, Object o) {
        Log.e("aop", observable + " changed by " + o);
      }
    });
    user.mName = "lalala";
    Log.e("aop", "after changed");
  }
}
