package rehabilitation.api.service.business.businessUtils;

import org.springframework.stereotype.Component;
import rehabilitation.api.service.dto.entities.ClientDto;
import rehabilitation.api.service.dto.entities.RehubDto;
import rehabilitation.api.service.dto.entities.SpecialistDto;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.ReHubModel;
import rehabilitation.api.service.entity.SpecialistModel;

@Component
public class MappingUtil {
    public SpecialistDto doMapSpecialistDtoAndGet(SpecialistModel specialistModel) {
        return new SpecialistDto(
                specialistModel.getLogin(), specialistModel.getFirstName(), specialistModel.getLastName(),
                specialistModel.getCity(), specialistModel.getAge(), specialistModel.getExperience(),
                specialistModel.getRate(), specialistModel.getSpeciality(),
                specialistModel.getImgUrl(), specialistModel.getDescription(),
                specialistModel.getReHub() != null ? specialistModel.getReHub().getLogin() : "",
                specialistModel.getListOfClientsLogin());
    }

    public ClientDto doMapClientDtoAndGet(ClientModel clientModel) {
        return new ClientDto(
                clientModel.getLogin(), clientModel.getFirstName(), clientModel.getLastName(),
                clientModel.getEmail(), clientModel.getAddress(), clientModel.getContactInformation(),
                clientModel.getImgUrl(),
                clientModel.getListOfSpecialistsLogin());
    }

    public RehubDto doMapReHubDtoAndGet(ReHubModel reHubModel) {
        return new RehubDto(
                reHubModel.getLogin(), reHubModel.getName(), reHubModel.getEmail(),
                reHubModel.getAddress(), reHubModel.getContactInformation(),
                reHubModel.getImgUrl(),
                reHubModel.getListOfSpecialistsLogin());
    }
}
