import java.io.File;
import java.io.RandomAccessFile;
import java.util.Stack;

public class Lexic {
  // Asignacion de variables globales par la clase
  String lexema = "";
  int state = 0, column, matrixValue, line = 1;
  int character = 0;
  Stack<Token> tokenStack = new Stack<Token>();

  // Direccion del archivo de texto que se analizara
  String filePath = new File("File.txt").getAbsolutePath();

  public void analizerMessage(String message) {
    System.out.println("---------------------------------");
    System.out.println(" Analizer -> " + message);
    System.out.println("---------------------------------");
  }

  // ----------------------------- MATRICES -----------------------------------------------
  public int[][] transitionMatrix = {
        //    _    c    d    +    -    *    /    <    >    (    )    |    .    ,    ;    "    '    =    oc  EOF  EOL   EB  TAB    
        //    0    1    2    3    4    5    6    7    8    9   10   11   12   13   14   15   16   17   18   19   20   21   22
  /* 0  */ {   1,   1,   2, 103, 104,   5,   7,  11,  12, 125, 126, 117, 118, 119, 120,  14, 121,  13, 502,   0,   0,   0,   0 },
  /* 1  */ {   1,   1,   1, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 },
  /* 2  */ { 501, 501,   2, 101, 101, 101, 101, 101, 101, 125, 126, 101,   3, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101 },
  /* 3  */ { 501, 501,   4, 501, 501, 501, 501, 501, 501, 125, 126, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501, 501 },
  /* 4  */ { 501,	501,	 4,	102, 102,	102, 102,	102, 102, 125, 126, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102 },
  /* 5  */ { 105,	105, 105,	105, 105,	  6, 105,	105, 105, 125, 126,	105, 105, 105, 105, 105, 105, 105, 105, 105, 105, 105, 105 },
  /* 6  */ { 107,	107, 107,	107, 107,	107, 107,	107, 107, 125, 126,	107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107, 107 },
  /* 7  */ { 106,	106, 106,	106, 106,	  9,	 8,	106, 106, 125, 126,	106, 106, 106, 106, 106, 106, 113, 106, 106, 106, 106, 106 },
  /* 8  */ {   8,	  8, 	 8,	  8,	 8,	  8,	 8,	  8,	 8,   8,   8,	  8,   8,	  8,	 8,	  8,	 8,	  8,	 8,	  8,	 0,	  8,	 8 },
  /* 9  */ {   9,	  9,	 9,	  9,	 9,	 10,	 9,	  9,	 9,   9,   9,	  9,   9,	  9,	 9,	  9,	 9,	  9,	 9, 503,	 9,	  9,	 9 },
  /* 10 */ {   9,	  9,	 9,	  9,	 9,	  9,	 0,	  9,	 9,   9,   9, 	9,   9,   9,	 9,	  9,	 9, 	9,	 9, 503,	 9,	  9,	 9 },
  /* 11 */ { 108,	108, 108,	108, 108,	108, 108,	108, 108, 125, 126,	108, 108, 108, 108, 108, 108, 109, 108, 108, 108, 108, 108 },
  /* 12 */ { 110, 110, 110, 110, 110, 110, 110, 110, 110, 125, 126, 110, 110, 110, 110, 110, 110, 111, 110, 110, 110, 110, 110 },
  /* 13 */ { 123, 123, 123, 123, 123, 123, 123, 123, 123, 125, 126, 123, 123, 123, 123, 123, 123, 112, 123, 123, 123, 123, 123 },
  /* 14 */ {  14,  14,	14,	 14,	14,	 14,	14,	 14,	14,  14,  14,	 14,  14,	 14,	14, 124,	14,	 14,	14, 504, 504,  14,	14 }
  };

  public String[][] reservedWordsMatrix = {
    //           0          1  
    /*  0 */ { "200", "program"   },
    /*  1 */ { "201", "character" },
    /*  2 */ { "202", "integer"   },
    /*  3 */ { "203", "real"      },
    /*  4 */ { "204", "boolean"   },
    /*  5 */ { "205", "begin"     },
    /*  6 */ { "206", "end"       },
    /*  7 */ { "207", "read"      },
    /*  8 */ { "208", "write"     }, 
    /*  9 */ { "209", "if"        },
    /* 10 */ { "210", "then"      },
    /* 11 */ { "211", "else"      }, 
    /* 12 */ { "212", "while"     },
    /* 13 */ { "213", "do"        },
    /* 14 */ { "214", "or"        },
    /* 15 */ { "216", "not"       },
    /* 16 */ { "217", "implicit"  },
    /* 17 */ { "218", "none"      },
    /* 18 */ { "219", "dimension" },
    /* 19 */ { "220", "print"     }
  };

  public String[][] errorMatrix = {
    //           0                 1  
    /*  0 */ { "500", "Se esperaba un caracter"  },
    /*  1 */ { "501", "Se esperaba un digito"    },
    /*  2 */ { "502", "Caracter no identificado" },
    /*  3 */ { "503", "Comentario abierto"       },
    /*  4 */ { "504", "Cadena abierta"           }
  };
  
  RandomAccessFile file = null;

  // Codigo principal donde se realiza todas las funciones del codigo
  public void runAnalisis() {
    try {
      // Se declara la manera en que se leera el archivo
      file = new RandomAccessFile(filePath, "r");

      // El siguiente while se utiliza para validar que aun se estan leyendo caracteres del documento
      while (character != -1) {
        // Al caracter se le va asignando el caracter conforme este va leyendo
        character = file.read();

        // A continuacion se valida el tipo de caracter que sea, si el un caracter o un numero, y en caso de no serlo este sera un simbolo
        // dependiento de lo que sea, sera la columna a la cual sera asignada.
        if (Character.isLetter((char)character)) {
          column = 1;
        } else if (Character.isDigit((char)character)) {
          column = 2;
        } else {
          switch ((char)character) {
            case '_':
              column = 0;
              break;
            case '+':
              column = 3;
              break;
            case '-':
              column = 4;
              break;
            case '*':
              column = 5;
              break;
            case '/':
              column = 6;
              break;
            case '<':
              column = 7;
              break;
            case '>':
              column = 8;
              break;
            case '(':
              column = 9;
              break;
            case ')':
              column = 10;
              break;
            case '|':
              column = 11;
              break;
            case '.':
              column = 12;
              break;
            case ',':
              column = 13;
              break;
            case ';':
              column = 14;
              break;
            case '"':
              column = 15;
              break;
            case '\'':
              column = 16;
              break;
            case '=':
              column = 17;
              break;
            case 9:
              column = 22;
              break;
            case 10:
              column = 20;
              line += 1;
              break;
            case 13:
              column = 19;
              break;
            case 32:
              column = 21;
              break;
            default:
              column = 18;
              break;
          }
        }

        // Se asigna el valor de la matriz con el estado que se asigno al inicio, mas la columna obtenida de los if y el switch anterior
        matrixValue = transitionMatrix[state][column];
        
        // En caso de que el valor seas menor que 100, esto quiere decir que es un estado
        if (matrixValue < 100) {
          
          // Al estado que ya tenimaos se le asignara el valor que tenemos registrado en el matrixValue
          state = matrixValue;

          // En caso de que el estado sea 0, significa una de dos cosas, que es el inicio del documento, o que ya se ingreso una cadena o caracter valido
          // por lo que se regresara a cadena vacia el lexema, en el caso de que no sea 0 el estado, al lexema se le agregara el caracter que se 
          // leyo por ultima vez en el documento
          if (state == 0) {
            lexema = "";
          } else {
            lexema += (char)character;
          } 
          // La siguiente validacion es la siguiente, que sea mayor o igual a 100 (Un token valido o palabra reservada) y menor a 500 (Errores)
        } else if (matrixValue >= 100 && matrixValue < 500) {
          // En caso de que el valor sea igual a 100, quiere decir que se detecto una palabra, por lo tanto se debera validar que si es una 
          // palabra reservada
          if (matrixValue == 100) {
            validateReservedWord();
          }

          // A continuacion, estos token son algunos que al momento de leerse pueden generar algunas inconsistencias, como que se añadan
          // caracteres de mas al lexema, por lo que al puntero se le debera restar uno para que se pueda realizar de manera correcta el lexema
          if (matrixValue == 100 || matrixValue == 101 || matrixValue == 102 || matrixValue == 105 ||
              matrixValue == 106 || matrixValue == 108 || matrixValue == 110 || 
              matrixValue == 123 || matrixValue >= 200) {
            file.seek(file.getFilePointer() - 1);
          } else {
            // Si no es ninguno de los anteriores, se le agregara el caracter leido al lexema
            lexema += (char)character;
          }

          // En la siguiente funcion se guarda el nodo con los datos obtenidos de la matriz
          insertNode();

          // Despues de esto se reinicia el estado y el lexema para continuar con la lectura
          state = 0;
          lexema = "";
        }
          
        // El siguiente codigo verifica si el token es de un error (mayor o igual a 500), y en caso de hacerlo imprime el error que sea
        errorMessage();

        //  Imprimir los nodos
        printNodes();
      }

      // Al momento de terminar la lextura se puede dar el caso de que no se lea correctamente el ultimo token, por lo que se utiliza el 
      // siguiente metodo, en el que primero valida si el lexema esta vacio o no
      if (!lexema.isEmpty()) {
        // Primero valida si esta palabra es reservada o no
        validateReservedWord();

        // A continuacion se hacen las mismas validaciones que se hicieron anteriormente, si es mayor o igual a 100 o mayor a 500
        if (matrixValue >= 100 && matrixValue < 500) {
          insertNode();
        } else if (matrixValue < 100) {
          matrixValue = transitionMatrix[matrixValue][19];
        } 

        if (matrixValue >= 500) {
          errorMessage();
        } else {
          insertNode();
        }
      }
    } catch (Exception e) {
      analizerMessage(e.getMessage());
    } finally {
      try {
        // A continuacion se cierra el documento para evitar fugas de informacion
        if (file != null) {
          file.close();
        }
      } catch (Exception e) {
        analizerMessage(e.getMessage());
      }
    }
  }

  // Esta funcin es utilizada para verificar si es una palabra reservada, recorriendo con un for la matriz de palabras reservadas
  // y comparandola con la palabra ingresada
  public void validateReservedWord() {
    for (int i = 0; i < reservedWordsMatrix.length; i++) {
      if (lexema.equals(reservedWordsMatrix[i][1])) {
        matrixValue = Integer.parseInt(reservedWordsMatrix[i][0]); 
        break;
      }
    }
  }

  // La siguiente funcion valida si es un token de error, o sea, mayor o igual a 500, y recorrre la matriz de errores comparando
  // con los datos de la matriz
  public void errorMessage() {
    if (matrixValue >= 500) {
      for (int i = 0; i < errorMatrix.length; i++) {
        if (matrixValue == Integer.parseInt(errorMatrix[i][0])) {
          analizerMessage("Error Line " + line + ": " + errorMatrix[i][0] + " " + errorMatrix[i][1]);
        }
      }
    }
  }

  // Imprimie los nodos del stack con un while en caso de no estar vacio
  public void printNodes() {
    while (!tokenStack.isEmpty()) {
      Token token = tokenStack.pop();
      System.out.println("Line " + token.line + ": " + token.token + " -> " + token.lexema);
    }
  }

  // Guarda los datos del token creado en el stack
  public void insertNode() {
    tokenStack.push(new Token(matrixValue, line, lexema));
  }
}