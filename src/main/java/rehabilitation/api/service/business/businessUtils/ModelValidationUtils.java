package rehabilitation.api.service.business.businessUtils;

import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.CommonRepository;

public class ModelValidationUtils {
    /**
     * Checks if a model with the provided login exists in the database using the specified repository.
     *
     * @param login      Unique identifier for the model in the database
     * @param repository Model's repository handling SQL queries
    //     * @param <AnyModel> Generic model
     * @return Model found in the database by the repository
     * @throws NotFoundLoginException if the login is not found in the database
     */
    public static  <AnyModel extends UserModel> AnyModel getModelIfExists(String login, CommonRepository<AnyModel> repository) throws  NotFoundLoginException{
        return repository.findByLogin(login).orElseThrow(() -> new NotFoundLoginException(login));
    }

    /**
     * Checks if the model's email and login exist in the database using the specified repository.
     //     * @param <AnyModel>  Generic model
     //     * @param anyModel    Model to be checked
     * @param repository  Model's repository handling SQL queries
     * @return true if the model does not exist in the database
     * @throws AlreadyExistLoginException if the login or email already exist in the database
     */
    public static  <AnyModel extends UserModel> boolean checkIfBaseHasModel(
            String login,
            String email,
            CommonRepository<AnyModel> repository) throws AlreadyExistLoginException {

        if (repository.existsByLogin(login)) {
            throw new AlreadyExistLoginException(login);
        }
        if (repository.existsByEmail(email)) {
            throw new AlreadyExistLoginException(email);
        }
        return true;
    }
}
