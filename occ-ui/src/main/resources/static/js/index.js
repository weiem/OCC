$(function() {
	//登录
	$('#login').click(function(){
		login();
	});
});

//登录
//function login() {
//	var username = $('#username').val();
//	var password = $('#password').val();
//
//	if(!username || !password) {
//		$('#error').html('用户名、密码不能为空!');
//		return;
//	}
//	
//	if(!username.match(/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/))
//	{
//		$('#error').html('用户名必须为邮箱格式!');
//		return;
//	}
//	
//	$.post(	
//		"http://localhost/login",
//		{'username':username,'password':password},
//		function(result,a,b,c)
//		{
//			if(result.success) {
//				location.href = 'list.html';
//			} else {
//				$('#error').html(result.message);
//				$('#newcode').click();
//			}
//		}
//	);
//	
//	$.ajax({
//        type: "POST",
//        url: "../login",
//        data: {
//        	'username':username,
//        	'password':password
//        },                   
//        success: function(response, options)
//        {
//        	if(response.message != null)
//        	{
//            	$('#error').html(response.message);
//        	}
//        }
//         
//    });
//}