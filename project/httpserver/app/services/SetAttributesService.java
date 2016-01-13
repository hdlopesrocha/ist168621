package services;


import java.util.ArrayList;
import java.util.List;

import dtos.AttributeDto;
import models.Attribute;
import models.MetaData;
import models.Permission;
import org.bson.Document;
import org.bson.types.ObjectId;


/**
 * The Class AuthenticateUserService.
 */
public class SetAttributesService extends Service<Void> {

	private ObjectId owner;
	private ObjectId caller;
	List<AttributeDto> attributes;

	public SetAttributesService(String caller, String owner, List<AttributeDto> attributes) {
		this.caller =  new ObjectId(caller);
		this.owner =  new ObjectId(owner);
		this.attributes = attributes;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see services.Service#dispatch()
	 */
	@Override
	public Void dispatch() {

		Attribute.deleteByOwner(owner);
		List<String> tags = new ArrayList<String>();
		Document filters = new Document();

		for(AttributeDto attr : attributes){
			if(attr.isSearchable()){
				tags.add(attr.getValue().toString().toLowerCase());
			}

			Attribute at = new Attribute(owner,attr.getKey(),attr.getValue(),attr.isIdentifiable()).save();
			ObjectId from = attr.getVisibility().equals(AttributeDto.Visibility.PRIVATE) ? owner : null;
			if(attr.getAccess().equals(AttributeDto.Access.READ)){
				new Permission(from,Permission.PERMISSION_READ,at.getId() ).save();
			}
			else if(attr.getAccess().equals(AttributeDto.Access.WRITE)){
				new Permission(from,Permission.PERMISSION_WRITE,at.getId() ).save();
			}

			if(attr.isAggregator()){
				filters.append(attr.getKey(), attr.getValue());
			}
		}


		MetaData.deleteByOwner(owner);
		new MetaData(owner,tags,filters).save();

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see services.Service#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return (owner!=null && owner.equals(caller)) || Permission.find(caller,Permission.PERMISSION_ADMIN,null)!=null;
	}

}