package com.monochromatic.god_of_fire.utility.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;

public final class Logger {
	private static boolean logTest = true;
	private static String location = System.getProperty("user.home")
			+ File.separator + "God of Fire" + File.separator + "logs";
	private static String name = ("" + new Timestamp(System.currentTimeMillis()))
			.substring(0, 10)
			+ "_"
			+ ("" + new Timestamp(System.currentTimeMillis()))
					.substring(11, 19);
	private final static String filepath = location + File.separator + name
			+ ".txt";

	public static void createLog() throws FileNotFoundException {
		System.out.println("Attempting to create a log at " + filepath);
		File file = new File(filepath);
		file.getParentFile().mkdirs();
		PrintWriter out = new PrintWriter(file);
		out.close();
	}

	public static void log(String message) {
		System.out.println(message);
		if (logTest) {
			try {
				java.nio.file.Files.write(Paths.get(filepath),
						(message + "\n").getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				disable();
				e.printStackTrace();
			}
		}
	}

	public static void log() {
		System.out.println();
		if (logTest) {
			try {
				java.nio.file.Files.write(Paths.get(filepath),
						("\n").getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				disable();
				e.printStackTrace();
			}
		}
	}

	public static void logWithoutSyso(String message) throws IOException {
		java.nio.file.Files.write(Paths.get(filepath),
				(message + "\n").getBytes(), StandardOpenOption.APPEND);
	}

	public static void logWithoutSyso() throws IOException {
		java.nio.file.Files.write(Paths.get(filepath), ("\n").getBytes(),
				StandardOpenOption.APPEND);
	}

	public static void disable() {
		System.out.println("Something's wrong with logging! ABORT! ABORT! ABORT!");
		System.out.println("Disabling logging.");
		logTest = false;
	}
}
