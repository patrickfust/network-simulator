package dk.fust.networksimulator.service;

import dk.fust.networksimulator.model.GeneralConfiguration;
import dk.fust.networksimulator.repository.GeneralConfigurationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GeneralConfigurationService {

    private static final long DEFAULT_TIMEOUT = 10_000;

    private GeneralConfigurationRepository generalConfigurationRepository;

    public GeneralConfiguration getGeneralConfiguration() {
        GeneralConfiguration gc = generalConfigurationRepository.findFirstBy();
        if (gc == null) {
            return createGeneralConfiguration();
        }
        return gc;
    }

    public GeneralConfiguration updateGeneralConfiguration(GeneralConfiguration generalConfigurationDetails) {
        GeneralConfiguration generalConfiguration = getGeneralConfiguration();
        generalConfiguration.setTimeoutMs(generalConfigurationDetails.getTimeoutMs());
        generalConfiguration.setFollowRedirect(generalConfigurationDetails.isFollowRedirect());
        generalConfiguration.setTargetBaseUrl(generalConfigurationDetails.getTargetBaseUrl());
        return generalConfigurationRepository.save(generalConfiguration);
    }

    public GeneralConfiguration createGeneralConfiguration() {
        return generalConfigurationRepository.save(new GeneralConfiguration(null, "",
                DEFAULT_TIMEOUT, true, null, null));
    }

}
