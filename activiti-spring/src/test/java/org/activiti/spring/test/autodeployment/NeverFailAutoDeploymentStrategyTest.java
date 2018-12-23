package org.activiti.spring.test.autodeployment;

import java.util.List;

import org.activiti.spring.autodeployment.NeverFailAutoDeploymentStrategy;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:org/activiti/spring/test/autodeployment/errorHandling/spring-context.xml")
public class NeverFailAutoDeploymentStrategyTest extends SpringActivitiTestCase {

    private final String nameHint = "NeverFailAutoDeploymentStrategyTest";

    private final String validName1 = "org/activiti/spring/test/autodeployment/errorHandling/valid.bpmn20.xml";
    private final String invalidName1 = "org/activiti/spring/test/autodeployment/errorHandling/parsing-error.bpmn20.xml";
    private final String invalidName2 = "org/activiti/spring/test/autodeployment/errorHandling/validation-error.bpmn20.xml";

    private void cleanUp() {
        List<org.activiti.engine.repository.Deployment> deployments = repositoryService.createDeploymentQuery().list();
        for (org.activiti.engine.repository.Deployment deployment : deployments) {
            repositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    @Test
    public void testValidResources() {
        cleanUp();
        final Resource[] resources = new Resource[]{new ClassPathResource(validName1)};
        NeverFailAutoDeploymentStrategy deploymentStrategy = new NeverFailAutoDeploymentStrategy();
        deploymentStrategy.deployResources(nameHint, resources, repositoryService);
        assertEquals(1, repositoryService.createDeploymentQuery().count());
        cleanUp();
    }

    @Test
    public void testInvalidResources() {
        cleanUp();
        final Resource[] resources = new Resource[]{new ClassPathResource(validName1), new ClassPathResource(invalidName1), new ClassPathResource(invalidName2)};
        NeverFailAutoDeploymentStrategy deploymentStrategy = new NeverFailAutoDeploymentStrategy();
        deploymentStrategy.deployResources(nameHint, resources, repositoryService);
        assertEquals(1, repositoryService.createDeploymentQuery().count());
        cleanUp();
    }

    @Test
    public void testWithParsingErrorResources() {
        cleanUp();
        final Resource[] resources = new Resource[]{new ClassPathResource(validName1), new ClassPathResource(invalidName1)};
        NeverFailAutoDeploymentStrategy deploymentStrategy = new NeverFailAutoDeploymentStrategy();
        deploymentStrategy.deployResources(nameHint, resources, repositoryService);
        assertEquals(1, repositoryService.createDeploymentQuery().count());
        cleanUp();
    }

    @Test
    public void testWithValidationErrorResources() {
        cleanUp();
        final Resource[] resources = new Resource[]{new ClassPathResource(validName1), new ClassPathResource(invalidName2)};
        NeverFailAutoDeploymentStrategy deploymentStrategy = new NeverFailAutoDeploymentStrategy();
        deploymentStrategy.deployResources(nameHint, resources, repositoryService);
        assertEquals(1, repositoryService.createDeploymentQuery().count());
        cleanUp();
    }

    @Test
    public void testOnlyInvalidResources() {
        cleanUp();
        final Resource[] resources = new Resource[]{new ClassPathResource(invalidName1)};
        NeverFailAutoDeploymentStrategy deploymentStrategy = new NeverFailAutoDeploymentStrategy();
        deploymentStrategy.deployResources(nameHint, resources, repositoryService);
        assertEquals(0, repositoryService.createDeploymentQuery().count());
        cleanUp();
    }
}