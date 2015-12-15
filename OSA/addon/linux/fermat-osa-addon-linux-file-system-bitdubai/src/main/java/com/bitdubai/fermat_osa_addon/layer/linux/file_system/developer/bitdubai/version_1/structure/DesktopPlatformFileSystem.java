package com.bitdubai.fermat_osa_addon.layer.linux.file_system.developer.bitdubai.version_1.structure;




import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PlatformBinaryFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PlatformFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PlatformTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantLoadFileException;

import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * The Platform File System is the implementation of the file system that is handled to external plugins not requires the plug in to identify itself.
 *
 * Created by Roberto Requena - (rart3001@gmail.com) on 08/12/2015.
 */
public class DesktopPlatformFileSystem implements PlatformFileSystem {


    /**
     * PlatformFileSystem interface member variables.
     */



    /**
     * PlatformFileSystem interface implementation.
     */

    /**
     *<p>This method gets a new PlatformTextFile object. And load file content on memory
     *
     * @param directoryName name of the directory where the files are stored
     * @param fileName name of file to load
     * @param privacyLevel level of privacy for the file, if it is public or private
     * @param lifeSpan lifeSpan of the file, whether it is permanent or temporary
     * @return PlatformTextFile object
     * @throws FileNotFoundException
     * @throws CantCreateFileException
     */
    @Override
    public PlatformTextFile getFile(String directoryName, String fileName, FilePrivacy privacyLevel, FileLifeSpan lifeSpan) throws FileNotFoundException,CantCreateFileException {
        DesktopPlatformTextFile newFile =null;
        try {

            newFile = new DesktopPlatformTextFile(directoryName,hashFileName(fileName), privacyLevel, lifeSpan);

        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            throw new CantCreateFileException();
        }

        try {
            newFile.loadFromMedia();
            return newFile;
        }
        catch (CantLoadFileException e){
            e.printStackTrace();
            throw new FileNotFoundException();
        }

    }

    /**
     *<p>This method create a new PlatformTextFile object.
     *
     * @param directoryName name of the directory where the files are stored
     * @param fileName name of file to load
     * @param privacyLevel level of privacy for the file, if it is public or private
     * @param lifeSpan lifeSpan of the file, whether it is permanent or temporary
     * @return PlatformTextFile object
     * @throws CantCreateFileException
     */
    @Override
    public PlatformTextFile createFile(String directoryName, String fileName, FilePrivacy privacyLevel, FileLifeSpan lifeSpan) throws  CantCreateFileException{
        try {
        return new DesktopPlatformTextFile(directoryName,hashFileName(fileName), privacyLevel, lifeSpan);
        }
        catch (NoSuchAlgorithmException e){
            throw new CantCreateFileException();
        }
    }

    @Override
    public PlatformBinaryFile getBinaryFile(String directoryName, String fileName, FilePrivacy privacyLevel, FileLifeSpan lifeSpan) throws FileNotFoundException, CantCreateFileException {
        try {

            final DesktopPlatformBinaryFile binaryFile = new DesktopPlatformBinaryFile(directoryName, fileName, privacyLevel, lifeSpan);
            binaryFile.loadFromMedia();
            return binaryFile;

        } catch (CantLoadFileException e){

            throw new FileNotFoundException(e, "", "Check the cause");
        }catch(Exception e){

            throw new CantCreateFileException(e, "", "Unhandled error.");
        }
    }

    @Override
    public PlatformBinaryFile createBinaryFile(String directoryName, String fileName, FilePrivacy privacyLevel, FileLifeSpan lifeSpan) throws CantCreateFileException {

        try {

            return new DesktopPlatformBinaryFile(directoryName, fileName, privacyLevel, lifeSpan);

        } catch(Exception e){

            throw new CantCreateFileException(e, "", "Unhandled error.");
        }
    }

    /**
     *
     * Hash the file name using the algorithm SHA 256
     */

    private String hashFileName(String fileName) throws NoSuchAlgorithmException {
        String encryptedString = fileName;
        try{

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(fileName.getBytes(Charset.forName("UTF-8")));
            byte[] digest = md.digest();
            Base64 base64 = new Base64();
            byte[] encoded = base64.encode(digest);

            try {
                encryptedString = new String(encoded, "UTF-8");
            } catch (Exception e){
            	throw new NoSuchAlgorithmException (e);
            }    

        }catch(NoSuchAlgorithmException e){
            throw e;
        }
        return encryptedString.replace("/","");
    }
}
