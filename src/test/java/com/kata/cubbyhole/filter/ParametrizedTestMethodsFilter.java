package com.kata.cubbyhole.filter;

import com.kata.cubbyhole.runner.SpringJUnitParams;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.model.FrameworkMethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParametrizedTestMethodsFilter {
    private final SpringJUnitParams jUnitParamsRunner;
    private final Filter filter;

    public ParametrizedTestMethodsFilter(SpringJUnitParams jUnitParamsRunner, Filter filter) {
        this.jUnitParamsRunner = jUnitParamsRunner;
        this.filter = filter;
    }

    public ParametrizedTestMethodsFilter(SpringJUnitParams jUnitParamsRunner) {
        this.jUnitParamsRunner = jUnitParamsRunner;
        this.filter = Filter.ALL;
    }

    public List<FrameworkMethod> filteredMethods(List<FrameworkMethod> frameworkMethods) {
        List<FrameworkMethod> filteredMethods = new ArrayList();
        Iterator var3 = frameworkMethods.iterator();

        while (var3.hasNext()) {
            FrameworkMethod frameworkMethod = (FrameworkMethod) var3.next();
            if (this.filter.shouldRun(this.jUnitParamsRunner.describeMethod(frameworkMethod))) {
                filteredMethods.add(frameworkMethod);
            }
        }

        return filteredMethods;
    }
}
