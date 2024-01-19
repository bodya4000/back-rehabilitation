package rehabilitation.api.service.business.businessServices.abstractions;

import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;

import java.util.Map;

public abstract class ModelService<Model extends UserModel>  {

    /**
     * Updates a model's specific attributes identified by the provided map of updates.
     *
     * @param login   Unique identifier for the model in the database
     * @param updates Specific map with keys to be updated for the current model and their new values
     * @throws NotFoundLoginException if the login is not found in the database
     */
    protected abstract void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException;


    /**
     * Deletes a model by login.
     *
     * @param login Unique identifier for the model in the database
     * @throws NotFoundLoginException if the login is not found in the database
     */
    protected abstract void deleteModel(String login) throws NotFoundLoginException;

    /**
     * Adds a new child model to the parent model.
     *
     * @param parentLogin Parent model's login
     * @param childLogin  Child model's login
     * @throws NotFoundLoginException if the login is not found in the database
     */
    protected abstract void addChild(String parentLogin, String childLogin) throws NotFoundLoginException;

    /**
     * Removes a child model from the parent model.
     *
     * @param parentLogin Parent model's login
     * @param childLogin  Child model's login
     * @throws NotFoundLoginException if the login is not found in the database
     */
    protected abstract void removeChild(String parentLogin, String childLogin) throws NotFoundLoginException;


    protected void executeUpdates(Map<String, Object> updates, UserModel currentUser){

    };
}
