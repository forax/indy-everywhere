package fr.umlv.indyeverywhere.test;

public class IndyEveryWhereTest {
  private int field;
  
  public void method(String s) {
    System.out.println("method called with " + s);
  }
  
  public static void main(String[] args) {
    var test = new IndyEveryWhereTest();
    test.field = 42;
    test.method("foo");
  }
}
