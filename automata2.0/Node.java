public class Node {
  int token;
  int line;
  String lexema;
  Node next = null;

  Node(int token, int line, String lexema) {
    this.token = token;
    this.line = line;
    this.lexema = lexema;
  }
}
