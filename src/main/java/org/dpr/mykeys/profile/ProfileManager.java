package org.dpr.mykeys.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.ChildInfo;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.certificate.CertificateInfo;
import org.dpr.mykeys.ihm.components.ListPanel;
import org.dpr.mykeys.keystore.InternalKeystores;

public class ProfileManager

{
	public static final Log log = LogFactory.getLog(ProfileManager.class);
	public final static String PROFIL_EXTENSION = ".mkprof";

	public void saveToFile(Map<String, Object> elements, String name) throws ManageProfilException, IOException {
		if (StringUtils.isBlank(name)) {
			throw new ManageProfilException("nom obligatoire");
		}
		File profDir = new File(KSConfig.getProfilsPath());
		if (!profDir.exists()) {
			profDir.mkdirs();
		}
		File f = new File(profDir, name + PROFIL_EXTENSION);
		if (f.exists()) {
			throw new ManageProfilException("Le profil existe d�j�");
		}
		Properties p = new Properties();
		for (Map.Entry<String, Object> entry : elements.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			p.setProperty(entry.getKey(), (String) entry.getValue());
		}
		p.store(new FileOutputStream(f), "");
	}

	public Properties loadProfile(String name) throws ManageProfilException {
		File f = new File(KSConfig.getProfilsPath(), name + PROFIL_EXTENSION);

		if (!f.exists()) {
			throw new ManageProfilException("Le profil n'existe pas");

		}
		try (FileInputStream fis = new FileInputStream(f)) {
			Properties p = new Properties();
			p.load(new FileInputStream(f));
			return p;
		} catch (Exception e) {
			throw new ManageProfilException("Erreur chargement profil", e);
		}

	}

	public static List<? extends ChildInfo> getProfils() {
		List<Profil> profs = new ArrayList<Profil>();
		File profDir = new File(KSConfig.getProfilsPath());

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(KSConfig.getProfilsPath()))) {
            for (Path path : directoryStream) {
            	profs.add(new Profil(path));
               
            }
        } catch (IOException ex) {}
       
		return profs;

	}

	public void saveToFile(Map<String, Object> elements, String name, CertificateInfo certInfo)
			throws ManageProfilException, IOException {
		if (StringUtils.isBlank(name)) {
			throw new ManageProfilException("nom obligatoire");
		}
		File profDir = new File(KSConfig.getProfilsPath());
		if (!profDir.exists()) {
			profDir.mkdirs();
		}
		File f = new File(profDir, name + PROFIL_EXTENSION);
		if (f.exists()) {
			throw new ManageProfilException("Le profil existe d�j�");
		}
		Properties p = new Properties();
		for (Map.Entry<String, Object> entry : elements.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			p.setProperty(entry.getKey(), (String) entry.getValue());
		}

		p.setProperty("keyUSage", String.valueOf(certInfo.getIntKeyUsage()));
		p.setProperty("keyUSage2", String.valueOf(certInfo.getKeyUsage()));
		p.store(new FileOutputStream(f), "");

	}

	public String[] getProfiles() {
		File profDir = new File(KSConfig.getProfilsPath());
		String[] list = profDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".mkprof");
			}
		});
		return list;
	}

	public void delete(NodeInfo ksInfo, Profil profil) throws IOException {
		Files.delete(profil.getPath());
		
	}
}