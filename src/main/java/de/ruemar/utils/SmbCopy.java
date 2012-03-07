package de.ruemar.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import org.apache.commons.io.IOUtils;

public class SmbCopy {

	private static String domain;
	private static String username;
	private static String password;
	private static SmbFile src;
	private static File dest;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		initializeProperties();
		if (argsAreValid(args)) {

			try {
				copyDirectory(src, dest);
			} catch (SmbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		else {
			System.out
					.println("Usage: java -jar smbCopy.jar <SRCFILE or SRCFOLDER> <DESTFILE OR DESTFOLDER>");

		}

	}

	private static boolean argsAreValid(String[] args) {

		if (args.length > 1) {

			String srcPath = getSmbUrl() + args[0];
			String destPath = args[1];

			try {
				SmbCopy.src = new SmbFile(srcPath);
				SmbCopy.dest = new File(destPath);
			} catch (MalformedURLException e) {
				return false;
			}

			return true;
		}

		return false;

	}

	private static void copyDirectory(SmbFile srcPath, File destPath) throws SmbException,
			MalformedURLException, UnknownHostException {

		if (srcPath.isDirectory()) {

			if (!destPath.exists()) {

				destPath.mkdir();

			}
			// get the correct path of the directory including the trailing
			// slash
			srcPath = new SmbFile(srcPath.getParent() + srcPath.getName() + "/");

			String files[] = srcPath.list();

			for (int i = 0; i < files.length; i++) {
				copyDirectory(new SmbFile(srcPath, files[i]), new File(destPath, files[i]));

			}

		}

		else {

			if (!srcPath.exists()) {

				System.out.println("File or directory does not exist:");
				System.out.println(srcPath.getPath());
				System.exit(0);

			}

			else

			{

				try {
					SmbFileInputStream fis = new SmbFileInputStream(srcPath);
					FileOutputStream fos = new FileOutputStream(destPath);
					IOUtils.copy(fis, fos);

				} catch (SmbException e) {

					e.printStackTrace();
				} catch (MalformedURLException e) {

					e.printStackTrace();
				} catch (UnknownHostException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}

		}

		System.out.println(srcPath.getName() + " copied." + " Date Last Modified: " + formatDate(srcPath.lastModified()));

	}
	
	
	private static String formatDate(long longDate)
	{
		
		DateFormat dfmt = new SimpleDateFormat( "dd.MM.yyyy hh:mm" ); 
		
		return dfmt.format(new Date(longDate));	
		
	}
	
	private static void initializeProperties() {
		SmbProperties prop = null;
		try {
			prop = new SmbProperties();
		} catch (IOException e) {
			System.out.println("Could not open configuration file");

		}

		domain = prop.getPropertyValue("smb.domain");
		username = prop.getPropertyValue("smb.username");
		password = prop.getPropertyValue("smb.password");

	}

	public static String getSmbUrl() {

		String url = "smb://" + domain + ";" + username + ":" + password + "@" + domain;

		return url;

	}

}
