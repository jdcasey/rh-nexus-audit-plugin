package org.sonatype.plugins.it.applifecycle.dep;

import javax.inject.Inject;

public class DepComponent 
{

    @Inject
    private AnotherComponent anotherComponent;

    public AnotherComponent getAnotherComponent()
    {
        return anotherComponent;
    }

    public void setAnotherComponent( final AnotherComponent anotherComponent )
    {
        this.anotherComponent = anotherComponent;
    }
}
