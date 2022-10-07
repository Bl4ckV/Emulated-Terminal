import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Terminal {
	private Scanner kb;
	private String command, allCommands, globalPath;
	
	public Terminal() {
		kb = new Scanner(System.in);
		command = "";
		globalPath = System.getProperty("user.dir");
		while (!command.equalsIgnoreCase("exit")) {
			System.out.print(showPrompt());
			command = setCommand();
			allCommands = command;
			command = returnCommand(command)[0];
			runCommand(returnCommand(allCommands));
		}
		System.out.println("Saliendo...");
	}
	
	protected void runCommand(String[] arguments) {
		if (arguments[0].equalsIgnoreCase("pwd")) 
			pwd();
		else if ((arguments[0].equalsIgnoreCase("cd")) || (arguments[0].equalsIgnoreCase("chdir"))) 
			cd(arguments);
		else if (arguments[0].equalsIgnoreCase("dir"))
			dir();
		else if (arguments[0].equalsIgnoreCase("mkdir"))
			mkdir(arguments, true);	
		else if (arguments[0].equalsIgnoreCase("rd"))
			rd(arguments);
		else if (arguments[0].equalsIgnoreCase("cat"))
			cat(arguments);
		else if (arguments[0].equalsIgnoreCase("touch"))
			mkdir(arguments, false);
		else if (arguments[0].equalsIgnoreCase("write"))
			write(arguments);
		else if (arguments[0].equalsIgnoreCase("top"))
			cat(arguments);
		else if (arguments[0].equalsIgnoreCase("readpoint"))
			readpoint(arguments);
		else if (arguments[0].equalsIgnoreCase("help"))
			help();
		else if (arguments[0].equalsIgnoreCase("info"))
			info(arguments);
	}
	
	protected String setCommand() {
		return kb.nextLine();
	}
	
	protected static String showPrompt() {
		try {
			return System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName() + " > ";
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	protected String[] returnCommand(String command) {
		String[] c = new String[command.split(" ").length];
		c = command.split(" ");
		return c;
	}
	
	protected void cd(String[] directory) {
		if (directory.length < 2)
			pwd();
		else {
			if (directory[1].equalsIgnoreCase("..")) {
				File f = new File(globalPath);
				if (f.getParent() != null)
					globalPath = f.getParent();
			}
			else {
				File f = new File(globalPath, directory[1]);
				if (f.exists() && f.isDirectory()) 
				   globalPath = f.getAbsolutePath();
				else
					System.out.println("El sistema no puede encontrar la ruta especificada.");
			}	
		}
	}
	
	protected void pwd() {
		System.out.println(globalPath);
	}
	
	protected void dir() {
		String[] pathnames;
        File f = new File(globalPath);
        pathnames = f.list();
        for (String pathname : pathnames)
            System.out.println(pathname);
	}
	
	protected void mkdir(String[] directory, boolean control) {
		if (directory.length < 2)
			System.out.println("La sintaxis del comando no es correcta");
		else {
			File f = new File(globalPath, directory[1]);
			if (f.exists())
				System.out.println("El archivo o directorio ya existe");
			else {
				if (control)
					f.mkdirs();
				else {
					try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	protected void rd(String[] directory) {
		if (directory.length < 2)
			System.out.println("La sintaxis del comando no es correcta");
		else {
			File f = new File(globalPath, directory[1]);
			if (!f.delete())
				System.out.println("El archivo no existe"); 
		}
	}
	
	protected void cat(String[] directory) {
		if (directory.length < 2)
			System.out.println("La sintaxis del comando no es correcta");
		else {
	        Scanner sc = null;
	        if (directory.length < 3) {
	        	File file = new File(globalPath, directory[1]);
	        	try {
	    			sc = new Scanner(file);
	    		} catch (FileNotFoundException e) {
	    			e.printStackTrace();
	    		}
	        	while(sc.hasNextLine()){
	                System.out.println(sc.nextLine());
	            }
	        }
	        else {
	        	File file = new File(globalPath, directory[2]);
	        	try {
	    			sc = new Scanner(file);
	    		} catch (FileNotFoundException e) {
	    			e.printStackTrace();
	    		}
	        	for (int i = 0; i < Integer.parseInt(directory[1]); i++) {
	        		System.out.println(sc.nextLine());
	        	}
	        }
		}
	}
	
	protected void write(String[] directory) {
		if (directory.length < 2)
			System.out.println("La sintaxis del comando no es correcta");
		else {
			try {
				Writer output = new BufferedWriter(new FileWriter(globalPath + "\\" + directory[1], true));
				String texto = "";
				for (int i = 2; i < directory.length; i++)
					texto += directory[i] + " ";
				output.append(texto);
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void readpoint(String[] directory) {
		if (directory.length < 3)
			System.out.println("La sintaxis del comando no es correcta");
		else {
			try {
				RandomAccessFile randomAccessFile = new RandomAccessFile(globalPath + "\\" + directory[1], "r");
				randomAccessFile.seek(Integer.parseInt(directory[2]));
				String line = randomAccessFile.readLine();
				while (line != null) {
					System.out.println(line);
					line = randomAccessFile.readLine();
				}
				randomAccessFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 	
	}
	
	protected void help() {
		System.out.println("\nHELP                		      -> Lista todos los comandos");
		System.out.println("CD                 		      -> Muestra la ruta del directorio actual");
		System.out.println("CD ..               		      ->  Se mueve al directorio padre");
		System.out.println("CD + NOMBRE         		      -> Lista archivos de ese directorio");
		System.out.println("MKDIR               		      -> Crea un directorio de la ruta actual");
		System.out.println("INFO <nombre>       		      -> Muestra la información del elemento indicado");
		System.out.println("CAT <nombreFichero> 		      -> Muestra el contenido de un fichero");
		System.out.println("TOP <numeroLineas><NombreFichero>     -> Muestra las líneas especificadas de un fichero.");
		System.out.println(
				"MKFILE <nombreFichero> <texto>        -> Crea un fichero con ese nombre y el contenido de texto");
		System.out.println("WRITE <nombreFichero> <texto>         -> Añade 'texto' al final del fichero especificado");
		System.out.println("DIR 				      -> Lista los archivos o directorios de la ruta actual");
		System.out.println(
				"READPOINT <nombreFichero1> <posición> -> Lee un archivo desde una determinada posición del puntero");
		System.out.println(
				"DELETE <nombre>			      -> Borra el fichero, si es un directorio borra todo su contenido y a si mismo");
		System.out.println("CLOSE				      -> Cierra el programa");
		System.out.println("CLEAR 				      -> Vacía la lista\n");
	}
	
	protected void info(String[] directory) {
		if (directory.length < 2)
			System.out.println("La sintaxis del comando no es correcta");
		else {
			File file = new File(globalPath, directory[1]);
			if (file.exists() && file.isDirectory()) {
				System.out.println("Nombre file: " + file.getName());
				System.out.println("Ruta : " + file.getAbsolutePath());
				System.out.println("Oculto : " + file.isHidden());
				System.out.println("Solo lectura : " + file.setReadOnly());
				System.out.println("Espacio libre : " + file.getFreeSpace() + " Bytes");
				System.out.println("Tamaño : "+ file.getTotalSpace() + " Bytes");
				File f = new File(globalPath, directory[1]);
				int totalFiles=0;
				for (int i = 0; i < f.list().length; i++) 
					totalFiles++;
				System.out.println("Numero de Archivos: "+totalFiles);
			}
			else {
				System.out.println("El sistema no puede encontrar la ruta especificada.");
			}
		}
	}

}
