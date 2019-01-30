package fr.umlv.indyeverywhere.test;

public class IndyEveryWhereTest {
  private int field;
  
  public void method(String s) {
    System.out.println("method called with " + s);
  }
  
  public int fun() {
    return 3;
  }
  public long __fun__() {
    return 3L;
  }
  
  public static void main(String[] args) {
    var test = new IndyEveryWhereTest();
    test.field = 42;
    test.method("foo");
    
    // test patching
    System.out.println(test.fun());
    System.out.println(test.__fun__());
  }
}
