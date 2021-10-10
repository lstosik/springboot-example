package net.purevirtual.springbootexample.repo;

import net.purevirtual.springbootexample.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class ApplicationRepositoryCustomImpl implements ApplicationRepositoryCustom {

    @Autowired
    @Lazy
    private ApplicationRepository applicationRepository;

    @Override
    public Application getById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

}
