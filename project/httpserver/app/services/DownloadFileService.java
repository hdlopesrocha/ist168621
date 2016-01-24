package services;

import com.mongodb.gridfs.GridFSDBFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * The Class DownloadFileService.
 */
public class DownloadFileService extends Service<byte[]> {

    /** The file name. */
    private final String fileName;

    /**
     * Instantiates a new download file service.
     *
     * @param fileName the file name
     */
    public DownloadFileService(String fileName) {
        this.fileName = fileName;
    }

    /* (non-Javadoc)
     * @see services.Service#dispatch()
     */
    @Override
    public byte[] dispatch() {

        try {
            GridFSDBFile file = files.findOne(fileName);
            if (file != null) {
                InputStream is = file.getInputStream();

                // Process the objectData stream.

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();

                is.close();
                return buffer.toByteArray();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /* (non-Javadoc)
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {
        // TODO Auto-generated method stub
        return true;
    }

}
