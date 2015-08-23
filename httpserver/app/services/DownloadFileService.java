package services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.mongodb.gridfs.GridFSDBFile;

public class DownloadFileService extends Service<byte[]> {

	
	private String fileName;
	public DownloadFileService(String fileName) {
		// TODO Auto-generated constructor stub
		this.fileName = fileName;
	}
	
	
	@Override
	public byte[] dispatch() {
		
		

		try {
			GridFSDBFile file = files.findOne(fileName);
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
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		

		return null;
	}

	@Override
	public boolean canExecute() {
		// TODO Auto-generated method stub
		return true;
	}

}
