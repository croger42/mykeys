package org.dpr.mykeys.app.crl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;

import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.KeyToolsException;

public class CrlBuilder extends KeyTools {

public static void main(String[] args) {

		CrlBuilder test = new CrlBuilder();
				
//		try {
//			test.generateCrl2();
//		} catch (UnrecoverableKeyException | InvalidKeyException | KeyStoreException | NoSuchProviderException
//				| NoSuchAlgorithmException | CertificateException | CRLException | IllegalStateException
//				| SignatureException | IOException | KeyToolsException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	  
}

	public void generateCrl2ToFix() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException, InvalidKeyException, CRLException,
			IllegalStateException, SignatureException, KeyToolsException {

//		String kst = "C:/Documents and Settings/n096015/.myKeys/mykeysAc.jks";
//
//		char[] password = "mKeys983178".toCharArray();
//		KeyStore ks = loadKeyStore(kst, StoreFormat.JKS, password);
//		CertificateInfo cinfo = new CertificateInfo();
//		fillCertInfo(ks, cinfo, "MK DEV AC Intermediaire");
//		Calendar nextupdate = Calendar.getInstance();
//		CrlInfo crlInfo = new CrlInfo();
//		nextupdate.add(Calendar.DAY_OF_YEAR, 30);
//		crlInfo.setNextUpdate(nextupdate.getTime());
//		Key key = ks.getKey("mk dev root ca", password);
//		X509CRL crl = generateCrl(cinfo.getCertificate(), crlInfo, key);
//		OutputStream os = new FileOutputStream("c:/dev/crl2.crl");
//		os.write(crl.getEncoded());
//		os.close();

	}
}