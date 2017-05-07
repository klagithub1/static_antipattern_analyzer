public class User 
{
	@Id
	@SequenceGenerator ( sequenceName="user_seq", name = "hello" )
	@Column ( name = "user_id" )
	private int id ;

	public User()
	{
		//TODO...
	}

}
