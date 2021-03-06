/*
 * Copyright (C) 2018 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.infrastructure.initializer;

import org.apache.deltaspike.core.api.exclude.Exclude;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.deltaspike.core.api.projectstage.ProjectStage.Development;

/**
 * The Production {@link EnvironmentInitializer}
 * 
 * This one use database migrations strategy provided by FlywayDB
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 3.0.0, 27/12/2017
 */
@RequestScoped
@Exclude(ifProjectStage = Development.class)
public class ProductionInitializer implements EnvironmentInitializer {

    @Inject
    private Logger logger;
    
    @Resource(lookup = "java:/datasources/webBudgetDS")
    private DataSource dataSource;
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void initialize() {
        
        this.logger.warn("Initializing application in production mode");

        checkNotNull(this.dataSource, "No datasource found for migrations");
 
        final Flyway flyway = new Flyway();
        
        flyway.setDataSource(this.dataSource);
        
        flyway.setLocations("db/migration");
        flyway.setBaselineOnMigrate(true);
        
        final MigrationInfo migrationInfo = flyway.info().current();
 
        if (migrationInfo == null) {
            this.logger.info("No existing database at the actual datasource");
        }
        else {
            this.logger.info("Found a database with the version: {}", migrationInfo.getVersion() 
                    + " : " + migrationInfo.getDescription());
        }
        
        flyway.migrate();

        this.logger.info("Successfully migrated to database version: {}", 
                flyway.info().current().getVersion());
    }
}
