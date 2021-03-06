/**
 * Copyright (C) 2009 crja
 * <p>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dpr.mykeys.ihm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.app.utils.ProviderUtil;
import org.dpr.mykeys.ihm.user.CreateUserDialog;
import org.dpr.mykeys.ihm.user.SelectUserDialog;
import org.dpr.mykeys.utils.DialogUtil;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.security.Security;
import java.util.*;

/**
 * @author Christophe Roger
 */
public class MyKeys {

    private static final Log log = LogFactory.getLog(MyKeys.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        MyKeys mk = new MyKeys();
        mk.init();

    }

    private void init() {

        //to test another locale: Locale.setDefault(Locale.ENGLISH);
        log.debug("loading configuration...");

        KSConfig.initResourceBundle();
        log.info("initializing securiy provider...");
        Security.addProvider(new BouncyCastleProvider());
        ProviderUtil.init("BC");

        try {
            KSConfig.init();
            boolean justCreated = checkUpdate();
            checkConfig();
            if (justCreated) {
                log.debug("just created");
            } else {
                // migrate();
                //todo ?
            }
            login();

        } catch (Exception e) {

            DialogUtil.showError(null, Messages.getString("error.config"));
            throw new RuntimeException("Fatal Error", e);
        }

    }



    private void login() {
        SwingUtilities.invokeLater(() -> {
            SelectUserDialog cs = null;
            try {
                cs = new SelectUserDialog(
                        null, true);
            } catch (IhmException e) {
                log.error("login error", e);
            }

            cs.setVisible(true);
        });
    }

    private boolean checkUpdate() throws InvocationTargetException, InterruptedException {
        boolean justCreated = false;
        if (!KSConfig.getInternalKeystores().existsUserDatabase()) {
            boolean retour = DialogUtil.askConfirmDialog(null, Messages.getString("prompt.createUser"));
            if (!retour) {
                System.exit(0);
            }
            SwingUtilities.invokeAndWait(() -> {
                CreateUserDialog cs = new CreateUserDialog(
                        null, true);
                cs.setVisible(true);
            });
            justCreated = true;
        }

        if (!KSConfig.getInternalKeystores().existsUserDatabase())
            System.exit(0);

        return justCreated;
    }

    private void checkConfig() {

        Iterator<?> iter = KSConfig.getUserCfg().getKeys("store");
        boolean update = false;
        Map<String, HashMap> typesKS = new HashMap<>();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            log.info("found store info: " + key);
            List list = KSConfig.getUserCfg().getList(key);
            typesKS.put(key, new HashMap<String, String>());
            for (Object o : list) {
                String dirName = (String) o;
                log.info("check file store info: " + dirName);
                File f = new File(dirName);
                if (f.exists()) {
                    typesKS.get(key).put(dirName, dirName);
                    log.debug(dirName + " exists");
                } else {
                    update = true;
                }
            }
        }
        if (update) {
            Set ks1 = typesKS.keySet();
            for (String key1 : (Iterable<String>) ks1) {
                KSConfig.getUserCfg().clearProperty(key1);
                Set ks2 = typesKS.get(key1).keySet();
                for (String key2 : (Iterable<String>) ks2) {
                    KSConfig.getUserCfg().addProperty(key1, key2);

                }

            }
            KSConfig.save();
        }

    }


}
