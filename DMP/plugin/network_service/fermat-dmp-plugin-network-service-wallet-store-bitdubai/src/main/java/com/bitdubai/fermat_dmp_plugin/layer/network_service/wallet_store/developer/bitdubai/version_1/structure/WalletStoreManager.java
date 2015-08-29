package com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_language.exceptions.CantGetWalletLanguageException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantGetCatalogItemException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantGetDesignerException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantGetDeveloperException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantGetSkinException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantGetTranslatorException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantGetWalletIconException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantGetWalletsCatalogException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantPublishDesignerInCatalogException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantPublishDeveloperInCatalogException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantPublishLanguageInCatalogException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantPublishSkinInCatalogException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantPublishTranslatorInCatalogException;
import com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.exceptions.CantPublishWalletInCatalogException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DealsWithPluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.DealsWithPluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginBinaryFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantLoadFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantPersistFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.DealsWithLogger;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_api.layer.pip_Identity.developer.interfaces.DeveloperIdentity;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.exceptions.CantExecuteDatabaseOperationException;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.exceptions.CantPublishItemInCatalogException;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.catalog.CatalogItemImpl;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.catalog.Designer;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.catalog.DetailedCatalogItemImpl;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.catalog.Developer;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.catalog.Language;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.catalog.Skin;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.catalog.Translator;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.catalog.WalletCatalog;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.common.DatabaseOperations;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.database.WalletStoreCatalogDatabaseConstants;
import com.bitdubai.fermat_dmp_plugin.layer.network_service.wallet_store.developer.bitdubai.version_1.structure.database.WalletStoreCatalogDatabaseDao;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.DealsWithErrors;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.ErrorManager;

import java.util.List;
import java.util.UUID;

/**
 * Created by rodrigo on 7/21/15.
 */

public class WalletStoreManager implements DealsWithErrors, DealsWithLogger, DealsWithPluginDatabaseSystem, DealsWithPluginFileSystem {
    /**
     * WalletStoreManager member variables
     */
    UUID pluginId;

    /**
     * DealsWithErrors interface member variables
     */
    ErrorManager errorManager;

    /**
     * DealsWithLogger interface mmeber variables
     */
    LogManager logManager;

    /**
     * DealsWithPluginDatabaseSystem interface member variables
     */
    PluginDatabaseSystem pluginDatabaseSystem;

    /**
     * DealsWithPluginFileSystem interface member variables
     */
    PluginFileSystem pluginFileSystem;


    /**
     * Constructor
     * @param errorManager
     * @param logManager
     * @param pluginDatabaseSystem
     * @param pluginFileSystem
     */
    public WalletStoreManager(ErrorManager errorManager, LogManager logManager, PluginDatabaseSystem pluginDatabaseSystem, PluginFileSystem pluginFileSystem, UUID pluginId) {
        this.errorManager = errorManager;
        this.logManager = logManager;
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginFileSystem = pluginFileSystem;
        this.pluginId = pluginId;
    }

    /**
     * DealsWithErrors interface implementation
     */
    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    /**
     * DealsWithlogger interface implementation
     */
    @Override
    public void setLogManager(LogManager logManager) {
        this.logManager = logManager;
    }

    /**
     * DealsWithPluginDatabaseSystem interface implementation
     */
    @Override
    public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
    }

    /**
     * DealsWithPluginFileSystem interface implementation
     */
    @Override
    public void setPluginFileSystem(PluginFileSystem pluginFileSystem) {
        this.pluginFileSystem = pluginFileSystem;
    }

    private WalletStoreCatalogDatabaseDao getDatabaseDAO() throws CantExecuteDatabaseOperationException {
        WalletStoreCatalogDatabaseDao dbDAO = new WalletStoreCatalogDatabaseDao(errorManager, logManager, pluginDatabaseSystem, pluginId, WalletStoreCatalogDatabaseConstants.WALLET_STORE_DATABASE);
        return dbDAO;
    }

    private void publishItemInDB (CatalogItemImpl catalogItemImpl, DeveloperIdentity developer, Language language, com.bitdubai.fermat_api.layer.dmp_identity.translator.interfaces.Translator translator, com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.interfaces.Skin skin, com.bitdubai.fermat_api.layer.dmp_identity.designer.interfaces.Designer designer) throws CantPublishItemInCatalogException {
        try {
            getDatabaseDAO().catalogDatabaseOperation(DatabaseOperations.INSERT, catalogItemImpl, developer, language, translator, skin, designer);
        } catch (CantExecuteDatabaseOperationException e) {
            throw new CantPublishItemInCatalogException(CantPublishItemInCatalogException.DEFAULT_MESSAGE, e, null, null);
        }
    }

    private void saveCatalogItemIconFile(CatalogItemImpl catalogItemImpl) throws CantPublishWalletInCatalogException {
        try {
            saveImageIntoFile(catalogItemImpl.getId().toString(), catalogItemImpl.getName(), catalogItemImpl.getIcon());
        } catch (CantPublishItemInCatalogException | CantGetWalletIconException  e) {
            throw new CantPublishWalletInCatalogException(CantPublishWalletInCatalogException.DEFAULT_MESSAGE, e, null, null);
        }
    }

    private void saveImageIntoFile(String directory, String filename, byte[] content) throws CantPublishItemInCatalogException {
        try{
            PluginBinaryFile imageFile = pluginFileSystem.createBinaryFile(pluginId, directory, filename, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
            imageFile.setContent(content);
            imageFile.persistToMedia();
        } catch (CantCreateFileException | CantPersistFileException e) {
            throw new CantPublishItemInCatalogException(CantPublishItemInCatalogException.DEFAULT_MESSAGE, e, null, null);
        }
    }

    /**
     * Saves the catalog Item information into database and the icon file into disk.
     * @param catalogItemImpl
     * @throws CantPublishWalletInCatalogException
     */
    public void publishWallet (CatalogItemImpl catalogItemImpl) throws CantPublishWalletInCatalogException {
        try {
            DeveloperIdentity developer = catalogItemImpl.getDetailedCatalogItemImpl().getDeveloper();
            Language language = (Language) catalogItemImpl.getDetailedCatalogItemImpl().getDefaultLanguage();
            com.bitdubai.fermat_api.layer.dmp_identity.translator.interfaces.Translator translator = catalogItemImpl.getDetailedCatalogItemImpl().getDefaultLanguage().getTranslator();
            Skin skin = (Skin) catalogItemImpl.getDetailedCatalogItemImpl().getDefaultSkin();
            com.bitdubai.fermat_api.layer.dmp_identity.designer.interfaces.Designer designer = skin.getDesigner();
            // I publish all the wallet info except the skin
            this.publishItemInDB(catalogItemImpl, developer, language, translator, null, designer);
            // I save the icon file of the wallet.
            saveCatalogItemIconFile(catalogItemImpl);

            // I publish the skin and the presentation images.
            for (com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.interfaces.Skin skinToPublish: catalogItemImpl.getDetailedCatalogItemImpl().getSkins()){
                this.publishSkin(skinToPublish);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CantPublishWalletInCatalogException(CantPublishWalletInCatalogException.DEFAULT_MESSAGE, exception, "Publish Wallet", "Wallet Store");
        }
    }

    /**
     * pubish the skin into DB and files into disk
     * @param skin
     * @throws CantPublishSkinInCatalogException
     */
    public void publishSkin (com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.interfaces.Skin skin) throws CantPublishSkinInCatalogException{
        try {
            publishItemInDB(null, null, null, null, skin, null);
            saveImageIntoFile(skin.getSkinId().toString(), skin.getSkinName(), skin.getPresentationImage());
            int i = 0;
            for (byte[] images : skin.getPreviewImageList()){
                i++;
                saveImageIntoFile(skin.getSkinId().toString(), skin.getSkinName() + "_" + i, images);
            }
        } catch (Exception exception) {
            throw new CantPublishSkinInCatalogException(CantPublishSkinInCatalogException.DEFAULT_MESSAGE, exception, null, null);
        }
    }

    /**
     * saves the language in DB
     * @param language
     * @throws CantPublishLanguageInCatalogException
     */
    public void publishLanguage(Language language) throws CantPublishLanguageInCatalogException {
        try{
            publishItemInDB(null, null, language, null, null, null);
        } catch (Exception e) {
            throw new CantPublishLanguageInCatalogException(CantPublishLanguageInCatalogException.DEFAULT_MESSAGE, e, null, null);
        }
    }

    /**
     * saves the designer in DB
     * @param designer
     * @throws CantPublishDesignerInCatalogException
     */
    public void publishDesigner(Designer designer) throws CantPublishDesignerInCatalogException {
        try{
            publishItemInDB(null, null, null, null, null, designer);
        } catch (Exception e) {
            throw new CantPublishDesignerInCatalogException (CantPublishDesignerInCatalogException.DEFAULT_MESSAGE, e, null, null);
        }
    }

    /**
     * saves the designer in DB
     * @param developer
     * @throws CantPublishDesignerInCatalogException
     */
    public void publishDeveloper(Developer developer) throws CantPublishDeveloperInCatalogException {
        try{
            publishItemInDB(null, developer, null, null, null, null);
        } catch (Exception e) {
            throw new CantPublishDeveloperInCatalogException (CantPublishDeveloperInCatalogException.DEFAULT_MESSAGE, e, null, null);
        }
    }

    /**
     * saves the translator in DB
     * @param translator
     * @throws CantPublishTranslatorInCatalogException
     */
    public void publishTranslator(Translator translator) throws CantPublishTranslatorInCatalogException{
        try{
            publishItemInDB(null, null, null, translator, null, null);
        } catch (Exception e) {
            throw new CantPublishTranslatorInCatalogException (CantPublishTranslatorInCatalogException.DEFAULT_MESSAGE, e, null, null);
        }
    }


    private DetailedCatalogItemImpl getDetailedCatalogItemFromDatabase (UUID walletId) throws CantGetCatalogItemException {
        try {
            return getDatabaseDAO().getDetailedCatalogItem(walletId);
        } catch (Exception e) {
            throw new CantGetCatalogItemException(CantGetCatalogItemException.DEFAULT_MESSAGE, e, null, null);
        }
    }

    private byte[] getSkinContent(String directory, String fileName) throws FileNotFoundException, CantCreateFileException, CantLoadFileException {
            PluginBinaryFile skinFile = pluginFileSystem.getBinaryFile(pluginId, directory, fileName, FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
            skinFile.loadFromMedia();
            return skinFile.getContent();

    }

    /**
     * Get the detailed catalog item from db and icon imagess.
     * @param walletId
     * @return
     * @throws CantGetCatalogItemException
     */
    public DetailedCatalogItemImpl getDetailedCatalogItem (UUID walletId) throws CantGetCatalogItemException {
        try{
            DetailedCatalogItemImpl detailedCatalogItemImpl;
            detailedCatalogItemImpl = getDetailedCatalogItemFromDatabase (walletId);

            Skin defaultSkin = (Skin) detailedCatalogItemImpl.getDefaultSkin();
            try {
                defaultSkin.setPresentationImage(getSkinContent(walletId.toString(), defaultSkin.getSkinName()));
            } catch (FileNotFoundException | CantCreateFileException | CantLoadFileException e) {
                defaultSkin.setPresentationImage(null);
            }
            detailedCatalogItemImpl.setDefaultSkin(defaultSkin);

            return detailedCatalogItemImpl;
        } catch (Exception exception){
          throw new CantGetCatalogItemException(CantGetCatalogItemException.DEFAULT_MESSAGE, exception, null, null);
        }
    }



    /**
     * Gets the catalogItem from the database
     * @param walletId
     * @return
     * @throws CantGetCatalogItemException
     */
    public CatalogItemImpl getCatalogItem(UUID walletId) throws CantGetCatalogItemException{
        CatalogItemImpl catalogItemImpl;
        try {
            catalogItemImpl = getDatabaseDAO().getCatalogItem(walletId);
            catalogItemImpl.setDetailedCatalogItemImpl(getDetailedCatalogItem(walletId));
        } catch (Exception e) {
            throw new CantGetCatalogItemException(CantGetCatalogItemException.DEFAULT_MESSAGE, e, null, null);
        }

        return catalogItemImpl;
    }

    /**
     * gets the entire wallet catalog
     * @return
     * @throws CantGetWalletsCatalogException
     */
    public WalletCatalog getWalletCatalogue() throws CantGetWalletsCatalogException {
        WalletCatalog walletCatalog = new WalletCatalog();
        try {
            List<CatalogItemImpl> catalogItemImplList = getDatabaseDAO().getCatalogItems();
            walletCatalog.setCatalogItems(catalogItemImplList);
            walletCatalog.setCatalogSize(catalogItemImplList.size());
        } catch (Exception e) {
            throw new CantGetWalletsCatalogException(CantGetWalletsCatalogException.DEFAULT_MESSAGE, e, null, null);
        }
        return walletCatalog;
    }

    /**
     * Returns the developer object
     * @param developerId
     * @return
     * @throws CantGetDeveloperException
     */
    public Developer getDeveloper(UUID developerId) throws CantGetDeveloperException {
        try {
            Developer developer = getDatabaseDAO().getDeveloper(developerId);
            return developer;
        } catch (Exception exception) {
            throw new CantGetDeveloperException(CantGetDeveloperException.DEFAULT_MESSAGE, exception, null, null);
        }
    }


    /**
     * gets the language for the specified wallsi let id
     * @param walletId
     * @return
     * @throws CantGetWalletLanguageException
     */
    public com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.interfaces.Language getLanguage(UUID walletId) throws CantGetWalletLanguageException {
        try {
            return getDatabaseDAO().getLanguageFromDatabase(walletId);
        } catch (Exception exception) {
            throw new CantGetWalletLanguageException(CantGetWalletLanguageException.DEFAULT_MESSAGE, exception, null, null);
        }
    }

    /**
     * gets the skin from the specified wallet
     * @param walletId
     * @return
     * @throws CantGetSkinException
     */
    public com.bitdubai.fermat_api.layer.dmp_network_service.wallet_store.interfaces.Skin getSkin(UUID walletId) throws CantGetSkinException {
        try{
            return getDatabaseDAO().getSkinFromDatabase(walletId);
        } catch (Exception exception){
            throw new CantGetSkinException(CantGetSkinException.DEFAULT_MESSAGE, exception, null,null);
        }
    }


    /**
     * gets the specified designer
     * @param designerId
     * @return
     * @throws CantGetDesignerException
     */
    public Designer getDesigner(UUID designerId) throws CantGetDesignerException {
        try{
            Designer designer= getDatabaseDAO().getDesigner(designerId);
            return designer;
        } catch (Exception exception){
            throw new CantGetDesignerException(CantGetDesignerException.DEFAULT_MESSAGE, exception, null, null);
        }
    }


    /**
     * Gets the specidied translarot from db
     * @param translatorId
     * @return
     * @throws CantGetTranslatorException
     */
    public Translator getTranslator(UUID translatorId) throws CantGetTranslatorException {
        try{
            Translator translatorr= getDatabaseDAO().getTranslator(translatorId);
            return translatorr;
        } catch (Exception exception){
            throw new CantGetTranslatorException(CantGetTranslatorException.DEFAULT_MESSAGE, exception, null, null);
        }
    }

}
