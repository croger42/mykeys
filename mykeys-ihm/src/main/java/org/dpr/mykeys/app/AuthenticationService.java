package org.dpr.mykeys.app;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.List;

import org.bouncycastle.operator.OperatorCreationException;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreInfo;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.windows.CertificateHelperNew;
import org.dpr.mykeys.ihm.windows.certificate.AuthenticationException;
import org.dpr.mykeys.keystore.CertificateType;

public class AuthenticationService {

	public void createUser(String id, char[] pwd) throws ServiceException {
		CertificateHelperNew ch = new CertificateHelperNew();
		CertificateValue cer = null;
		KeyStoreInfo ki =null;
		try {
			cer = ch.createCertificate(CertificateType.AUTH, id, pwd);
			cer.setPassword(pwd);
			 ki = KSConfig.getInternalKeystores().getUserDB();
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException
				| NoSuchProviderException | SignatureException | OperatorCreationException | KeyStoreException | IOException  e) {
			throw new ServiceException(Messages.getString("certificate.error.create") + id, e); //$NON-NLS-1$
		}
		
		KeyStoreHelper kh = new KeyStoreHelper();

		kh.addCertToKeyStore(ki, cer, null);
	}

	public CertificateValue loadUser(String id, char[] pwd) throws ServiceException {
		KeyStoreHelper ch = new KeyStoreHelper();
		CertificateValue cer = null;

		try {
			cer = ch.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, pwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cer.setPassword(pwd);
		return cer;
	}

	public CertificateValue AuthenticateUSer(String id, char[] pwd) throws AuthenticationException {
		KeyStoreHelper ch = new KeyStoreHelper();
		CertificateValue cer = null;

		try {
			cer = ch.findCertificateByAlias(KSConfig.getInternalKeystores().getUserDB(), id, pwd);
		} catch (Exception e) {
			throw new AuthenticationException("authentication failed");
		}

		cer.setPassword(pwd);
		return cer;
	}
	
	public List<CertificateValue> listUsers() throws ServiceException {
		KeyStoreHelper ch = new KeyStoreHelper();
		List<CertificateValue> cer = null;

		try {
			cer = ch.getCertificates(KSConfig.getInternalKeystores().getUserDB());
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		return cer;
	}

}
