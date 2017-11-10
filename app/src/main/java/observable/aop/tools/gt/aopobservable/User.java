package observable.aop.tools.gt.aopobservable;

import java.util.Observable;

public class User extends Observable {
  public String mName;
  public int mGender;

  @Override
  public String toString() {
    return "name : " + mName + " gender : " + mGender;
  }
}
