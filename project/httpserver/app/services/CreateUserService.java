package services;

import exceptions.ConflictException;
import exceptions.ServiceException;
import main.Tools;
import models.IdentityProfile;
import models.KeyValueFile;
import models.PublicProfile;
import models.User;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticateUserService.
 */
public class CreateUserService extends Service<User> {

    private User user;
    private List<KeyValueFile> files;
    private String email, password;
    private Document properties;
    private List<String> permissions = new ArrayList<String>();

    public CreateUserService(final String email, final String password, final JSONObject properties, List<KeyValueFile> files) {
        this.user = User.findByEmail(email);
        this.properties = Document.parse(properties.toString());
        this.files = files;
        this.email = email;
        this.password = password;
    }

    public CreateUserService addPermission(String permission) {
        permissions.add(permission);
        return this;
    }


    /*
     * (non-Javadoc)
     *
     * @see services.Service#dispatch()
     */
    @Override
    public synchronized User dispatch() throws ServiceException {
        if (user != null) {
            throw new ConflictException();
        }

        for (KeyValueFile kvf : files) {
            UploadFileService service = new UploadFileService(kvf);
            String photoUrl = "";
            try {
                photoUrl = service.execute();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            properties.put(kvf.getKey(), photoUrl);

        }
        User newUser = new User(password);

        String token = Tools.getRandomString(32);

        while (User.findByToken(token) != null) {
            token = Tools.getRandomString(32);
        }

        newUser.setToken(token);
        newUser.save();
        new IdentityProfile(newUser.getId(), new Document().append("email", email)).save();
        new PublicProfile(newUser.getId(), properties).save();


        return newUser;
    }

    /*
     * (non-Javadoc)
     *
     * @see services.Service#canExecute()
     */
    @Override
    public boolean canExecute() {


        return password != null;
    }


}
