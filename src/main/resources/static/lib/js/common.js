function serializeToJson(form){
		var result={};
		//获取表单输入的内容
		//{name:email,value:用户输入的内容}
		var f=form.serializeArray();
		f.forEach(function(item){
			//result.email
			result[item.name]=item.value;
			alert(result[item.name]);
		});
		return result;
		
	}
	//修改label标签内容
	var label=document.getElementById('label');

	//修改下拉表单内容
	var adminLogin=document.getElementById('admin-login');
	var userLogin=document.getElementById('user-login');
	var spanValue=document.getElementById('span-value');
	adminLogin.onclick=function(){
		spanValue.innerHTML='管理员登录';
	}
	userLogin.onclick=function(){
		spanValue.innerHTML='普通用户登录';
	}