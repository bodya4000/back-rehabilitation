package rehabilitation.api.service.business;

import rehabilitation.api.service.entity.BaseModel;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NullLoginException;
import rehabilitation.api.service.repositories.CommonRepository;

import java.util.List;
import java.util.Map;

public abstract class CommonService<Model extends BaseModel, Dto> {

    /**
     * Retrieves a list of all models and maps each model into a DTO.
     *
     * @return List of DTO models
     */
    abstract List<Dto> getAllModelView();

    /**
     * Retrieves a model by login, maps it into a DTO.
     *
     * @param login Unique identifier for the model in the database
     * @return DTO model
     * @throws NotFoundLoginException if the login is not found in the database
     */
    abstract Dto getModelViewByLogin(String login) throws NotFoundLoginException;

    /**
     * Saves a new model after checking for existing logins in the database.
     *
     * @param model Model received from the client-side
     * @throws AlreadyExistLoginException if the login or email already exist in the database
     */
    abstract void saveModel(Model model) throws AlreadyExistLoginException;

    /**
     * Updates a model's specific attributes identified by the provided map of updates.
     *
     * @param login   Unique identifier for the model in the database
     * @param updates Specific map with keys to be updated for the current model and their new values
     * @throws NotFoundLoginException if the login is not found in the database
     */
    abstract void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException;

    /**
     * Maps all updated values to the current model.
     *
     * @param updates      Specific map with keys to be updated for the current model and their new values
     * @param currentModel Model that requires updates
     */
    abstract void executeUpdates(Map<String, Object> updates, Model currentModel);

    /**
     * Deletes a model by login.
     *
     * @param login Unique identifier for the model in the database
     * @throws NotFoundLoginException if the login is not found in the database
     */
    abstract void deleteModel(String login) throws NotFoundLoginException;

    /**
     * Adds a new child model to the parent model.
     *
     * @param parentLogin Parent model's login
     * @param childLogin  Child model's login
     * @throws NotFoundLoginException if the login is not found in the database
     */
    abstract void addChild(String parentLogin, String childLogin) throws NotFoundLoginException;

    /**
     * Removes a child model from the parent model.
     *
     * @param parentLogin Parent model's login
     * @param childLogin  Child model's login
     * @throws NotFoundLoginException if the login is not found in the database
     */
    abstract void removeChild(String parentLogin, String childLogin) throws NotFoundLoginException;

    /**
     * Maps a model into a DTO and returns it.
     *
     * @param model           Model to be mapped
     * @param listOfModelLogin List of daughter logins
     * @return DTO model
     */
    abstract Dto doMapModelDtoAndGet(Model model, List<String> listOfModelLogin);

    /**
     * Checks if a model with the provided login exists in the database using the specified repository.
     *
     * @param login      Unique identifier for the model in the database
     * @param repository Model's repository handling SQL queries
     * @param <AnyModel> Generic model
     * @return Model found in the database by the repository
     * @throws NotFoundLoginException if the login is not found in the database
     */
    public <AnyModel extends BaseModel> AnyModel checkIfBaseHasLogin(String login, CommonRepository<AnyModel> repository) throws NotFoundLoginException {
        return repository.findByLogin(login).orElseThrow(() -> new NotFoundLoginException(login));
    }

    /**
     * Checks if the model's email and login exist in the database using the specified repository.
     *
     * @param <AnyModel>  Generic model
     * @param anyModel    Model to be checked
     * @param repository  Model's repository handling SQL queries
     * @return true if the model does not exist in the database
     * @throws AlreadyExistLoginException if the login or email already exist in the database
     */
    public <AnyModel extends BaseModel> boolean checkIfBaseHasModel(AnyModel anyModel, CommonRepository<AnyModel> repository) throws AlreadyExistLoginException {
        if (repository.existsByLogin(anyModel.getLogin())) {
            throw new AlreadyExistLoginException(anyModel.getLogin());
        }
        if (repository.existsByEmail(anyModel.getEmail())) {
            throw new AlreadyExistLoginException(anyModel.getEmail());
        }
        return true;
    }

}
