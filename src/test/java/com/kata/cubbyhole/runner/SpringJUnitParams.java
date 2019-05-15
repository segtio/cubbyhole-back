package com.kata.cubbyhole.runner;

import com.kata.cubbyhole.runner.filter.ParametrizedTestMethodsFilter;
import junitparams.internal.ParameterisedTestClassRunner;
import junitparams.internal.TestMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.internal.runners.rules.RuleMemberValidator;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.validator.PublicClassValidator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpringJUnitParams extends SpringJUnit4ClassRunner {
    private ParametrizedTestMethodsFilter parametrizedTestMethodsFilter = new ParametrizedTestMethodsFilter(this);
    private ParameterisedTestClassRunner parameterisedRunner = new ParameterisedTestClassRunner(this.getTestClass());
    private Description description;
    public SpringJUnitParams(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Object[] $(Object... params) {
        return params;
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        super.filter(filter);
        this.parametrizedTestMethodsFilter = new ParametrizedTestMethodsFilter(this, filter);
    }

    protected void collectInitializationErrors(List<Throwable> errors) {
        this.validatePublicTestClass();
        this.validateZeroArgConstructor(errors);
        this.validateLifecycleMethods(errors);
        this.validateRules(errors);
        Iterator var2 = errors.iterator();

        while (var2.hasNext()) {
            Throwable throwable = (Throwable) var2.next();
            throwable.printStackTrace();
        }

    }

    private void validatePublicTestClass() {
        (new PublicClassValidator()).validateTestClass(this.getTestClass());
    }

    private void validateRules(List<Throwable> errors) {
        RuleMemberValidator.CLASS_RULE_VALIDATOR.validate(this.getTestClass(), errors);
        RuleMemberValidator.CLASS_RULE_METHOD_VALIDATOR.validate(this.getTestClass(), errors);
        RuleMemberValidator.RULE_METHOD_VALIDATOR.validate(this.getTestClass(), errors);
        RuleMemberValidator.RULE_VALIDATOR.validate(this.getTestClass(), errors);
    }

    private void validateLifecycleMethods(List<Throwable> errors) {
        this.validatePublicVoidNoArgMethods(BeforeClass.class, true, errors);
        this.validatePublicVoidNoArgMethods(AfterClass.class, true, errors);
        this.validatePublicVoidNoArgMethods(After.class, false, errors);
        this.validatePublicVoidNoArgMethods(Before.class, false, errors);
    }

    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (!this.handleIgnored(method, notifier)) {
            TestMethod testMethod = this.parameterisedRunner.testMethodFor(method);
            if (this.parameterisedRunner.shouldRun(testMethod)) {
                this.parameterisedRunner.runParameterisedTest(testMethod, this.methodBlock(method), notifier);
            } else {
                this.verifyMethodCanBeRunByStandardRunner(testMethod);
                super.runChild(method, notifier);
            }

        }
    }

    protected Description describeChild(FrameworkMethod method) {
        Description description = this.parameterisedRunner.getDescriptionFor(method);
        return description == null ? super.describeChild(method) : description;
    }

    private void verifyMethodCanBeRunByStandardRunner(TestMethod testMethod) {
        List<Throwable> errors = new ArrayList();
        testMethod.frameworkMethod().validatePublicVoidNoArg(false, errors);
        if (!errors.isEmpty()) {
            throw new RuntimeException((Throwable) errors.get(0));
        }
    }

    private boolean handleIgnored(FrameworkMethod method, RunNotifier notifier) {
        TestMethod testMethod = this.parameterisedRunner.testMethodFor(method);
        if (testMethod.isIgnored()) {
            notifier.fireTestIgnored(this.describeMethod(method));
        }

        return testMethod.isIgnored();
    }

    protected List<FrameworkMethod> computeTestMethods() {
        return this.parameterisedRunner.computeFrameworkMethods();
    }

    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        Statement methodInvoker = this.parameterisedRunner.parameterisedMethodInvoker(method, test);
        if (methodInvoker == null) {
            methodInvoker = super.methodInvoker(method, test);
        }

        return methodInvoker;
    }

    public Description getDescription() {
        if (this.description == null) {
            this.description = Description.createSuiteDescription(this.getName(), this.getTestClass().getAnnotations());
            List<FrameworkMethod> resultMethods = this.getListOfMethods();
            Iterator var2 = resultMethods.iterator();

            while (var2.hasNext()) {
                FrameworkMethod method = (FrameworkMethod) var2.next();
                this.description.addChild(this.describeMethod(method));
            }
        }

        return this.description;
    }

    private List<FrameworkMethod> getListOfMethods() {
        List<FrameworkMethod> frameworkMethods = this.parameterisedRunner.returnListOfMethods();
        return this.parametrizedTestMethodsFilter.filteredMethods(frameworkMethods);
    }

    public Description describeMethod(FrameworkMethod method) {
        Description child = this.parameterisedRunner.describeParameterisedMethod(method);
        if (child == null) {
            child = this.describeChild(method);
        }

        return child;
    }
}
