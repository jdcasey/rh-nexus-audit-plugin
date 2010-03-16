package com.redhat.tools.nexus.response.template;

public class TemplateException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    private Object[] params;

    public TemplateException( final String message, final Throwable cause )
    {
        super( message, cause );
    }

    public TemplateException( final String message )
    {
        super( message );
    }

    public TemplateException( final String message, final Throwable cause, final Object... params )
    {
        super( message, cause );
        this.params = params;
    }

    public TemplateException( final String message, final Object... params )
    {
        super( message );
        this.params = params;
    }

    @Override
    public String getLocalizedMessage()
    {
        return getMessage();
    }

    @Override
    public String getMessage()
    {
        if ( params == null )
        {
            return super.getMessage();
        }
        else
        {
            return String.format( super.getMessage(), params );
        }
    }

}
