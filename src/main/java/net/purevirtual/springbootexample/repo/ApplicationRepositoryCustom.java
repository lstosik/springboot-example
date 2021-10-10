package net.purevirtual.springbootexample.repo;

import net.purevirtual.springbootexample.entity.Application;

public interface ApplicationRepositoryCustom {
    public Application getById(Long id);
}
