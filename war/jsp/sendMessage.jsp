<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>
<body>

	   <form action="/social/sendMessage" method="post">
		
        <input type="text" name="email" value='${it.email}' /> 
	   <br>
	   <br>
      		<textarea rows="4" cols="50" name="Message">
			
			</textarea>
			
	   <br>	<br>
		<input type="submit" value="send Message" />
	
	</form>
</body>
</html>