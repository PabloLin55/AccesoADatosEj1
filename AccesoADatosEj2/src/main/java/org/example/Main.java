package org.example;

import java.io.*;
import java.util.*;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        File fichero;
        while (true) {
            try {
                System.out.println("Introduce el nombre del fichero de datos: ");
                String nombreFichero = sc.next();
                fichero = new File(nombreFichero);
                if (!fichero.exists()) {
                    throw new FileNotFoundException();
                }
                if (fichero.length() > 10000) {
                    throw new FicheroMuyGrande();
                }
                break;
            } catch (FicheroMuyGrande e) {
                System.out.println("El tamaño del fichero de entrada supera los 10000 bytes.");
            } catch (FileNotFoundException e) {
                System.out.println("El fichero especificado no existe.");
            }
        }



        int opcion = 0;
        while (opcion != 4) {
            System.out.println("+++MENÚ PRINCIPAL+++");
            System.out.println("""
                    1.Añadir usuario.
                    2.Mostrar usuarios introducidos.
                    3.Generar fichero de concordancias.
                    4.Salir.""");

            System.out.println("Introduce una opción: ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1 -> anadirUsuarios(fichero);
                case 2 -> mostrarUsuarios(fichero);
                case 3 -> buscarConcordancias(fichero);
                case 4 -> System.out.println("Me despido de usted inmediatamente.");
            }
        }
    }
    //Este método muestra los usuarios del fichero que se le indique.
    public static void mostrarUsuarios(File fichero) {
            //Obtengo todos los códigos del fichero a partir del método obtenerCodigos(fichero).
            ArrayList<String> codigos = obtenerCodigos(fichero);
            //Obtengo todas las aficiones de cada uno de los usuarios del fichero a partir del método obtenerAficiones(fichero).
            ArrayList<ArrayList<String>> aficiones = obtenerAficiones(fichero);
            //Creo un un treemap con en el que voy a poder poner elementos clave-valor y ordenarlos según un comparador.
            //En este caso las claves de los elementos serán los códigos, el valor, serán las aficiones y voy a ordenarlos por códigos de menor a mayor.
            TreeMap<String, ArrayList<String>> usuarios = new TreeMap<>(Main::comparadorCodigos);
            for (int i = 0; i < codigos.size(); i++) {
                usuarios.put(codigos.get(i), aficiones.get(i));
            }
            //Imprimo cada uno de los pares clave-valor.
            for (Map.Entry<String, ArrayList<String>> entry : usuarios.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
    }

    //Este método añade nuevos usuarios al fichero de usuarios.
    public static void anadirUsuarios(File fichero) {
            System.out.println("""
                Opción 1: Introducir código propio.
                Opción 2: Código automático generado por el programa.
                Opción 3: Volver al menú principal.""");
            System.out.println("Elija la opción que quiera: ");
            int opcionCodigo = sc.nextInt();

            while (opcionCodigo < 1 || opcionCodigo > 3) {
                System.out.println("Opción inválida, introduce una opción válida: ");
                opcionCodigo = sc.nextInt();
            }

            //En esta variable voy a ir metiéndo todos los datos del nuevo usuario.
            StringBuilder usuarioCompleto = new StringBuilder();

            //Si el usuario elige la opción 1, podrá escribir él mismo un código.
            if (opcionCodigo == 1) {
                try {
                    System.out.println("Introduce el código del nuevo usuario: ");
                    String codigo = sc.next();
                    //Obtengo todos los códigos para poder compara comprobar si el código metido por el usuario no esta repetido.
                    ArrayList<String> codigos = obtenerCodigos(fichero);

                    //Compruebo si el código escrito por el usuario empieza por una letra mayúscula.
                    if (!(codigo.charAt(0) > 'A' && codigo.charAt(0) < 'Z')) {
                        throw new CodigoFormatoIncorrecto();
                    }
                    //Compruebo si después de la letra mayúscula, el resto del código son números.
                    try {
                        Integer.parseInt(codigo.substring(1));
                    } catch (NumberFormatException e) {
                        System.out.println("Error: el formato del código es incorrecto.");
                        System.out.println("Ejemplo del formato correcto: U123.");
                        return;
                    }
                    if (codigos.contains(codigo)) {
                    throw new CodigoYaExistente();
                    }
                    //En caso de que todo haya ido bien, el código se añade al StringBuilder usuarioCompleto.
                    usuarioCompleto.append(codigo + " ");
                } catch (CodigoYaExistente e) {
                    System.out.println("Error: El código introducido ya existe.");
                    return;
                } catch (CodigoFormatoIncorrecto e) {
                    System.out.println("Error: El formato del código es incorrecto.");
                    System.out.println("Ejemplo del formato correcto: U123.");
                    return;
                }
            //Si el usuario elige la opción 2, será el programa el que elija el nuevo código.
            } else if (opcionCodigo == 2) {
                //Obtengo una lista con todos los códgios del fichero indicado.
                ArrayList<String> codigos = obtenerCodigos(fichero);
                //Ordeno la lista comparando los códigos.
                codigos.sort(Main::comparadorCodigos);
                //Cojo el último códgio que es el más grande, le añado 1 y luego el nuevo número lo junto con la letra
                //del último código.
                int numeroUltimoCodigo = Integer.parseInt(codigos.getLast().substring(1));
                int numeroNuevoCodigo = numeroUltimoCodigo + 1;
                String codigoCreado = codigos.getLast().substring(0, 1) + numeroNuevoCodigo;
                System.out.println("El código del nuevo usuario será:  "  + codigoCreado);
                //Después de haber creado el nuevo código, lo añado al StringBuilder usuarioCompleto.
                usuarioCompleto.append(codigoCreado + " ");
            } else if (opcionCodigo == 3) {
                //Vuelvo al menú principal.
                return;
        }

                //Pido al usuario que introduzca las aficiones y las meto también en el StringBuilder de usuarioCompleto.
                ArrayList<String> aficionesLista;
                sc.nextLine();
                while (true) {
                    try {
                        System.out.print("Introduce las aficiones del usuario: ");
                        String[] aficionesArray = sc.nextLine().toUpperCase().split(" ");
                        aficionesLista = new ArrayList<>();
                        //Introduzco las aficiones de aficionesArray uno a uno en aficionesLista para evitar aficiones duplicadas.
                        for (int i = 0; i < aficionesArray.length; i++) {
                            if (!aficionesLista.contains(aficionesArray[i])) {aficionesLista.add(aficionesArray[i]);}
                        }
                        //Si no hay aficiones, lanzo exepción.
                        if (aficionesLista.size() == 1 && aficionesLista.getFirst().isEmpty()) {
                            throw new UsuarioSinAficiones();
                        }
                        //Si el formato de entrada de las aficiones es incorrecto (hay espacios de más), lanzo excepción.
                        if (aficionesLista.contains("")) {
                            throw new EntradaFormatoIncorrecto();
                        }
                        for (int i = 0; i < aficionesLista.size(); i++) {
                            if (aficionesLista.get(i).contains(",")) {
                                throw new EntradaFormatoIncorrecto();
                            }
                        }
                        //Ordeno alfabéticamente la lista de aficiones y las meto en el StringBuilder de usuarioCompleto.
                        aficionesLista.sort(String::compareTo);
                        for (int i = 0; i < aficionesLista.size(); i++) {
                            if (i != aficionesLista.size() - 1) {
                                usuarioCompleto.append(aficionesLista.get(i)).append(" ");
                            } else {
                                usuarioCompleto.append(aficionesLista.get(i));
                            }
                        }
                        break;
                    } catch (UsuarioSinAficiones e) {
                        System.out.println("Introduce al menos una afición!");
                    } catch (EntradaFormatoIncorrecto e) {
                        System.out.println("Error: El formato de las aficiones es incorrecto.");
                        System.out.println("Ejemplo del formato correcto: aficion1 aficion2 aficion3");
                    }

                }
            //Escribo el contenido del StringBuilder usuarioCompleto en el fichero de usuarios.
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fichero, true));
                writer.write("\n" + usuarioCompleto);
                writer.close();
                System.out.println("Usuario añadido correctamente.");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


//Este método forma parejas de usuarios que comparten hobbies y las escribe en un fichero de concordancias.txt.
    public static  void buscarConcordancias(File fichero) {
        int opcion;
        while (true) {
            try {
                System.out.println("""
                1.Indicar número de concondarcias del fichero.
                2.Volver al menú principal.""");
                System.out.println("Elija la opción que quiera: ");
                opcion = sc.nextInt();
                if (opcion < 1 || opcion > 2) {
                    throw new OpcionInvalida();
                }
                break;
            } catch (OpcionInvalida e) {
                System.out.println("La opción introducida no es válida");
            }

        }
        if (opcion == 1) {
            System.out.println("Especifíque el número de concordancias que quiere: ");
            int numConcordanciasMinimas = sc.nextInt();
            while (numConcordanciasMinimas < 1 ) {
                System.out.println("El número de concordancias debe ser como mínimo 1: ");
                numConcordanciasMinimas = sc.nextInt();
            }
            //Variable que se va a actualizar dentro del bucle para contar el número de parejas.
            int contadorConcordancias = 0;

            try {
                BufferedWriter writer;

                ArrayList<String> concordancias = new ArrayList<>();
                ArrayList<String> codigos = obtenerCodigos(fichero);
                //Obtengo dos listas con todas las aficiones de cada uno de los usuarios.
                //En cada vuelta del bucle externo, cojo las aficiones de un usuario y en el bucle interno las comparo con las afciiones del resto de usuarios.
                ArrayList<ArrayList<String>> aficionesTotales = obtenerAficiones(fichero);
                ArrayList<ArrayList<String>> aficionesTotales2 = obtenerAficiones(fichero);
                for (int i = 0; i < aficionesTotales.size(); i++) {
                    for (int j = i; j < aficionesTotales2.size(); j++) {
                        //Con esta condición me evito comparar las aficiones del usuario de ese momento consigo mismas.
                        if (!(i == j)) {
                            //Creo una tercera lista con las aficiones del usuario en el que estoy en ese momento
                            //para poder utilizar el método retainAll(), en caso de que haya coincidencias, entre la
                            //lista de copia y la de aficionesTotales2, las coincidencias se guardarán en la lista de
                            //aficiones copia.
                            ArrayList<String> aficionesListaCopia = new ArrayList<>(aficionesTotales.get(i));
                            aficionesListaCopia.retainAll(aficionesTotales2.get(j));
                            //En caso de que haya concordancias, ordeno la lista de aficiones concordantes de forma alfabética
                            //y añado un elemento a la lista de concordancias.
                            if (!aficionesListaCopia.isEmpty() && aficionesListaCopia.size() >= numConcordanciasMinimas) {
                                aficionesListaCopia.sort(String::compareTo);
                                concordancias.add(codigos.get(i) + " " + codigos.get(j) + aficionesListaCopia + "\n");
                                contadorConcordancias++;
                            }
                        }
                    }
                }
                //Si hay concordancias, ordenó las concordancias por longitud y las escribo en el fichero concordancias.txt.
                if (!concordancias.isEmpty()) {
                    concordancias.sort(Main::comparadorLongitudes);
                    writer = new BufferedWriter(new FileWriter("concordancias.txt", true));
                    for (int i = 0; i < concordancias.size(); i++) {
                        writer.write(concordancias.get(i));
                    }
                    System.out.println("Se han formado " + contadorConcordancias + " parejas.");
                } else {
                    throw new FicheroConcordanciasVacio();
                }

                writer.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (FicheroConcordanciasVacio e) {
                System.out.println("No se ha podido formar ninguna pareja.");
            }
        }
    }
    //Comparador de textos por longitud para ordenar de mayor a menor (el normal es de menor a mayor).
    public static int comparadorLongitudes(String texto1, String texto2) {
        if (texto1.length() > texto2.length()) {
            return -1;
        } else if (texto1.length() < texto2.length()) {
            return 1;
        } else {return 0;}
    }
    //Comparador para ordenar de menor a mayor los códigos, primero ordena en base a la letra y si las letras son iguales,
    //ordena en base a la parte numérica.
    public static int comparadorCodigos(String codigo1, String codigo2) {
        char letraCodigo1 = codigo1.charAt(0);
        int numeroCodigo1 = Integer.parseInt(codigo1.substring(1));
        char letraCodigo2 = codigo2.charAt(0);
        int numeroCodigo2 = Integer.parseInt(codigo2.substring(1));
        if (letraCodigo1 > letraCodigo2) {
            return 1;
        } else if (letraCodigo1 < letraCodigo2) {
            return -1;
        } else {
            if (numeroCodigo1 > numeroCodigo2) {
                return 1;
            } else if (numeroCodigo1 < numeroCodigo2) {
                return -1;
            }
        }
        return 0;
    }
    //Leo del fichero de usuarios cada uno de los usuarios y meto los códigos en una lista.
    public static ArrayList<String> obtenerCodigos(File fichero) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichero));
            String linea;
            String[] usuario;
            ArrayList<String> codigos = new ArrayList<>();
            while ((linea = reader.readLine()) != null) {
                usuario = linea.split(" ");
                codigos.add(usuario[0]);
            }
            reader.close();
            return codigos;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Leo del fichero de usuarios cada uno de los usuarios y meto las aficiones en una lista.
    public static ArrayList<ArrayList<String>> obtenerAficiones(File fichero) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichero));
            String linea;
            String[] usuario;
            ArrayList<ArrayList<String>> aficionesTotales = new ArrayList<>();
            while ((linea = reader.readLine()) != null) {
                usuario = linea.split(" ");
                ArrayList<String> aficionesUsuario = new ArrayList<>();
                for (int i = 1; i < usuario.length; i++) {
                    aficionesUsuario.add(usuario[i]);
                }
                //La lista de las aficiones del usuario la meto en otra lista que va a albergar cada una de las aficiones
                //de todos los usuarios.
                aficionesTotales.add(aficionesUsuario);
            }
            reader.close();
            return aficionesTotales;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}