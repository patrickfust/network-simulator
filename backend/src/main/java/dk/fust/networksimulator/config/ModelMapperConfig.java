package dk.fust.networksimulator.config;

import dk.fust.networksimulator.dto.CreateScenarioDto;
import dk.fust.networksimulator.dto.ScenarioDto;
import dk.fust.networksimulator.model.Scenario;
import dk.fust.networksimulator.model.TargetSystem;
import dk.fust.networksimulator.repository.TargetSystemRepository;
import lombok.AllArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ModelMapperConfig {

    private final TargetSystemRepository targetSystemRepository;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        org.modelmapper.config.Configuration configuration = mapper.getConfiguration();
        configuration.setFieldMatchingEnabled(true);
        configuration.setCollectionsMergeEnabled(true);

        mapScenario(mapper);

        return mapper;
    }

    private void mapScenario(ModelMapper mapper) {
        // Converter: Long (targetSystemId) -> TargetSystem
        Converter<Long, TargetSystem> idToTargetSystem = ctx -> {
            Long id = ctx.getSource();
            return id == null ? null : targetSystemRepository.findById(id).orElse(null);
        };

        // Converter: TargetSystem (may be Hibernate proxy) -> Long (id)
        Converter<TargetSystem, Long> targetSystemToId = ctx -> {
            TargetSystem src = ctx.getSource();
            if (src == null) return null;
            if (src instanceof HibernateProxy) {
                Object impl = ((HibernateProxy) src).getHibernateLazyInitializer().getImplementation();
                if (impl instanceof TargetSystem) return ((TargetSystem) impl).getId();
                return null;
            }
            return src.getId();
        };

        // Scenario -> ScenarioDto: use converter to safely extract targetSystem id
        mapper.createTypeMap(Scenario.class, ScenarioDto.class)
                .addMappings(m -> m.using(targetSystemToId).map(Scenario::getTargetSystem, ScenarioDto::setTargetSystemId));

        // ScenarioDto -> Scenario: map targetSystemId -> targetSystem using repository converter
        mapper.createTypeMap(ScenarioDto.class, Scenario.class)
                .addMappings(new PropertyMap<ScenarioDto, Scenario>() {
                    @Override
                    protected void configure() {
                        using(idToTargetSystem).map(source.getTargetSystemId(), destination.getTargetSystem());
                    }
                });

        // Also skip mapping id when creating from CreateScenarioDto
        mapper.createTypeMap(CreateScenarioDto.class, Scenario.class)
                .addMappings(m -> m.skip(Scenario::setId));
    }

}
