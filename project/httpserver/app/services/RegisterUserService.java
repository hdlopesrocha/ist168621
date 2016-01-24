package services;


import dtos.AttributeDto;
import exceptions.ServiceException;
import models.Data;
import models.Permission;
import models.Search;
import models.User;

import java.util.List;


/**
 * The Class AuthenticateUserService.
 */
public class RegisterUserService extends Service<User> {

	private String password;
	private List<AttributeDto> attributes;

	public RegisterUserService(final String password, List<AttributeDto> attributes) {
		this.password = password;
		this.attributes = attributes;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see services.Service#dispatch()
	 */
	@Override
	public synchronized User dispatch() throws ServiceException {
		attributes.add(new AttributeDto("type",User.class.getName(), AttributeDto.Access.READ, AttributeDto.Visibility.PUBLIC,false,false,true));

		User user = new User(password);
		user.save();
		new Search(user.getId(),attributes).save();
		new Permission(user.getId(),attributes).save();
		new Data(user.getId(),attributes).save();

		return user;
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
