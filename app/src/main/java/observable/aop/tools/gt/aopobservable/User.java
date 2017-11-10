package observable.aop.tools.gt.aopobservable;

import java.util.Observable;

import observable.aop.tools.gt.aspect.IgnoreField;

public class User extends Observable {
  @IgnoreField
  public String mName;
  public int mGender;

  @Override
  public String toString() {
    return "name : " + mName + " gender : " + mGender;
  }
}
