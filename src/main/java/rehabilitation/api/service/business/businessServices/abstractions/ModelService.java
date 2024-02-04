package rehabilitation.api.service.business.businessServices.abstractions;

import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

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


    protected void executeUpdates(Map<String, Object> updates, UserModel currentUser){

    };
}
