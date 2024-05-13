
// Creacion de la clase de token, con su token, la linea en la que se encuentra y el lexema con su constructor
public class Token {
  int token;
  int line;
  String lexema;

  Token(int token, int line, String lexema) {
    this.token = token;
    this.line = line;
    this.lexema = lexema;
  }
}
