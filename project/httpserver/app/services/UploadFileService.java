package services;

import com.mongodb.gridfs.GridFSInputFile;
import models.KeyValueFile;

import java.io.IOException;
import java.util.UUID;

// TODO: Auto-generated Javadoc

/**
 * The Class SendMessageService.
 */
public class UploadFileService extends Service<String> {

    private final KeyValueFile anex;


    public UploadFileService(final KeyValueFile anex) {
        this.anex = anex;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public String dispatch() {

        String keyName = UUID.randomUUID().toString() + "/"
                + anex.getName();

        try {
            GridFSInputFile file = files.createFile(anex.getValue());
            file.setFilename(keyName);
            file.save();

            return keyName;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        return true;
    }

}
