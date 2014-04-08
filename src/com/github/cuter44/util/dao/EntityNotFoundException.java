package cn.edu.scau.tvprotal.util.dao.EntityNotFoundException;

public class EntityNotFoundException
    extends RuntimeException
{
    public EntityNotFoundException()
    {
        super();
    }

    public EntityNotFoundException(String msg)
    {
        super(msg);
    }

    public EntityNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public EntityNotFoundException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
